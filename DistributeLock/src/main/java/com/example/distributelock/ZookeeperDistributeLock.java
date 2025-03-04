package com.example.distributelock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ZookeeperDistributeLock {

    private ZooKeeper zookeeper;
    private String lockRoot;
    private String currentZkNode;

    public ZookeeperDistributeLock(ZooKeeper zookeeper, String lockPath) {
        this.zookeeper = zookeeper;
        this.lockRoot = lockPath;
    }

    public void lock() throws Exception {
        Stat stat = zookeeper.exists(lockRoot, false);
        if (stat == null) {
            try {
                zookeeper.create(lockRoot, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (KeeperException.NodeExistsException e) {
            }
        }
        currentZkNode = zookeeper.create(lockRoot + "/lock_", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        while (true) {
            List<String> nodes = zookeeper.getChildren(lockRoot, false);
            Collections.sort(nodes);
            String smallestNode = nodes.get(0);
            if (currentZkNode.equals(lockRoot + "/" + smallestNode)) {
                return;
            } else {
                String previousNode = nodes.get(Collections.binarySearch(nodes, currentZkNode.substring(lockRoot.length() + 1)) - 1);
                Object lock = new Object();
                synchronized (lock) {
                    Stat previousNodeStat = zookeeper.exists(lockRoot + "/" + previousNode, watchedEvent -> {
                        if (watchedEvent.getType() == Watcher.Event.EventType.NodeDeleted) {
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                        }
                    });
                    if (previousNodeStat != null) {
                        lock.wait();
                    }
                }
            }
        }
    }

    public void unlock() throws Exception {
        zookeeper.delete(currentZkNode, -1);
    }

    public void close() throws InterruptedException {
        zookeeper.close();
    }

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                try {
                    //multiple processes to connect to zk
                    ZooKeeper zookeeper = new ZooKeeper("localhost:2181", 5000, null);
                    ZookeeperDistributeLock lock = new ZookeeperDistributeLock(zookeeper, "/distributelocks");
                    lock.lock();
                    System.out.println("Lock acquired, executing critical section... " + Thread.currentThread().getName());
                    lock.unlock();
                    System.out.println("unlocked " + Thread.currentThread().getName());
                    lock.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(5000, TimeUnit.MILLISECONDS);
    }
}

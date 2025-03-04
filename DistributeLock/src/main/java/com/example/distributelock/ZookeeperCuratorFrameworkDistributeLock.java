package com.example.distributelock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Zookeep distribute lock implementation with CuratorFramework
 */
public class ZookeeperCuratorFrameworkDistributeLock {
    private static final String ZOOKEEPER_CONNECTION_STRING = "localhost:2181";
    private static final int BASE_SLEEP_TIME_MS = 5000;
    private static final int MAX_RETRIES = 3;

    private CuratorFramework client;
    private String lockRoot;
    private String currentZkNode;

    public ZookeeperCuratorFrameworkDistributeLock(String lockPath) {
        client = CuratorFrameworkFactory.newClient(
                ZOOKEEPER_CONNECTION_STRING,
                new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
        this.lockRoot = lockPath;
    }

    public void lock() throws Exception {
        Stat stat = client.checkExists().forPath(lockRoot);
        if (stat == null) {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(lockRoot, new byte[0]);
        }

        currentZkNode = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(lockRoot + "/lock_", new byte[0]);
        while (true) {
            List<String> nodes = client.getChildren().forPath(lockRoot);
            Collections.sort(nodes);
            String smallestNode = nodes.get(0);
            if (currentZkNode.equals(lockRoot + "/" + smallestNode)) {
                return;
            } else {
                String previousNode = nodes.get(Collections.binarySearch(nodes, currentZkNode.substring(lockRoot.length() + 1)) - 1);
                Object lock = new Object();
                synchronized (lock) {
                    Stat previousNodeStat = client.checkExists().usingWatcher((Watcher) watchedEvent -> {
                        if (watchedEvent.getType() == Watcher.Event.EventType.NodeDeleted) {
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                        }
                    }).forPath(lockRoot + "/" + previousNode);

                    if (previousNodeStat != null) {
                        lock.wait();
                    }
                }
            }
        }
    }

    public void unlock() throws Exception {
        client.delete().deletingChildrenIfNeeded().forPath(currentZkNode);
    }

    public void close() {
        client.close();
    }

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                try {
                    //multiple processes to connect to zk
                    ZookeeperCuratorFrameworkDistributeLock lock = new ZookeeperCuratorFrameworkDistributeLock("/distributelocks1");
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
package com.example.distributelock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZookeeperInternalDistributeLock {

    private static final String ZOOKEEPER_CONNECTION_STRING = "localhost:2181";
    private static final String LOCK_PATH = "/distributed_lock";
    private static final int BASE_SLEEP_TIME_MS = 1000;
    private static final int MAX_RETRIES = 3;

    private final CuratorFramework client;
    private final InterProcessMutex lock;

    public ZookeeperInternalDistributeLock() {
        client = CuratorFrameworkFactory.newClient(
                ZOOKEEPER_CONNECTION_STRING,
                new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES)
        );
        client.start();
        lock = new InterProcessMutex(client, LOCK_PATH);
    }

    public void acquireLock() throws Exception {
        lock.acquire();
    }

    public void releaseLock() throws Exception {
        lock.release();
    }

    public void close() {
        client.close();
    }

    public static void main(String[] args) {
        ZookeeperInternalDistributeLock distributedLock = new ZookeeperInternalDistributeLock();
        try {
            distributedLock.acquireLock();
            System.out.println("Lock acquired!");
            distributedLock.releaseLock();
            System.out.println("Lock released!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        distributedLock.close();

    }
}

package com.example.distributelock;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RedisDistributeLock {

    public RedisDistributeLock() {
    }

    public boolean tryLock(Jedis jedis, String lockKey, String lockValue, long expireTime) {
        SetParams params = SetParams.setParams().nx().px(expireTime);
        String res = jedis.set(lockKey, lockValue, params);
        return res != null && res.equals("OK");
    }

    public boolean unlock(Jedis jedis, String lockKey, String lockValue) {
        String script = """
                if redis.call('get', KEYS[1])== ARGV[1] then
                  return redis.call('del', KEYS[1])
                else
                  return 0
                end
                """;
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(lockValue));
        Long SUCCESS = 1L;
        return SUCCESS.equals(result);
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    Jedis jedis = new Jedis("localhost", 6379);
                    RedisDistributeLock lock = new RedisDistributeLock();
                    if (lock.tryLock(jedis, "lock", "lock", 5000)) {
                        try {
                            System.out.println("Lock acquired, executing critical sections");
                        } finally {
                            lock.unlock(jedis, "lock", "lock");
                        }
                    } else {
                        System.out.println("Failed to acquire lock");
                    }

                    jedis.close();
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(5000, TimeUnit.MILLISECONDS);
    }
}

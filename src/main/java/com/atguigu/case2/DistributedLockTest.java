package com.atguigu.case2;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

/**
 * @author Feng
 * @date 2021/7/10 20:14
 */
public class DistributedLockTest {
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        final DistributedLock lock1 = new DistributedLock();
        final DistributedLock lock2 = new DistributedLock();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock1.Zklock();
                    System.out.println("线程1  启动,获取到锁");
                    Thread.sleep(5 * 1000);
                    lock1.unZklock();
                    System.out.println("线程1  释放锁");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (KeeperException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock2.Zklock();
                    System.out.println("线程2  启动,获取到锁");
                    Thread.sleep(5 * 1000);
                    lock2.unZklock();
                    System.out.println("线程2  释放锁");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (KeeperException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}

package com.atguigu.case2;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Feng
 * @date 2021/7/10 18:20
 * <p>
 * 原生 Zookeeper 实现分布式锁案例
 */
public class DistributedLock {
    private String connectString = "node1:2181,node2:2181";//node3因为电脑原因，暂时不开启
    private int sessionTimeout = 2000;
    private ZooKeeper zk;
    //ZooKeeper 连接
    private CountDownLatch connectLatch = new CountDownLatch(1);
    //ZooKeeper 节点等待
    private CountDownLatch waitLatch = new CountDownLatch(1);
    // 当前 client 等待的子节点
    private String waitPath;
    // 当前 client 创建的子节点
    private String currentNode;


    // 和 zk 服务建立连接，并创建根节点
    public DistributedLock() throws IOException, InterruptedException, KeeperException {
        //获取连接
        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                //connectLatch 如果连接上zk  可以释放
                // 连接建立时, 打开 latch, 唤醒 wait 在该 latch 上的线程
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    connectLatch.countDown();
                }
                //waitLatch  需要释放
                // 发生了 waitPath 的删除事件
                if (watchedEvent.getType() == Event.EventType.NodeDeleted && watchedEvent.getPath().equals(waitPath)) {
                    waitLatch.countDown();
                }
            }
        });

        //等待zk正常连接后，往下走程序
        // 等待连接建立
        connectLatch.await();

        //判断根节点、lock是否存在
        //获取根节点状态
        Stat stat = zk.exists("/locks", false);
        if (stat == null) {
            //创建根节点
            zk.create("/locks", "locks".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    //对zk加锁
    public void Zklock() throws InterruptedException, KeeperException {
        //创建对应的临时代序号节点
        currentNode = zk.create("/locks/" + "seq-", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        //判断创建的节点是否是最小的序号节点，
        //如果是，获取到锁
        //如果不是，监听他序号前一个节点
        List<String> children = zk.getChildren("/locks", false);
        //如果children  只有一个，那就直接获取，如果有多个节点,需啊哟判断，谁最小
        if (children.size() == 1) {
            return;
        } else {
            Collections.sort(children);

            //获取节点名称seq-000000000
            String thisNode = currentNode.substring("/locks/".length());
            //通过seq-00000000获取该节点在children集合的位置
            int index = children.indexOf(thisNode);
            //判断
            if (index == -1) {
                System.out.println("数据异常");
            } else if (index == 0) {
                // index == 0, 说明 thisNode 在列表中最小, 当前client 获得锁
                return;
            } else {
                //需要监听 前一个节点的变化
                waitPath = "/locks/" + children.get(index - 1);
                // 在 waitPath 上注册监听器, 当 waitPath 被删除时,
                // zookeeper 会回调监听器的 process 方法
                zk.getData(waitPath, true, null);
                //等待监听
                waitLatch.await();
                return;
            }

        }
    }

    //对zk解锁
    public void unZklock() {
        //删除节点
        try {
            zk.delete(this.currentNode, -1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

}

package com.atguigu.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author Feng
 * @date 2021/7/9 17:25
 * <p>
 * zk客户端
 */
public class ZkClient {
    private String connectString = "node1:2181,node2:2181";
    private int sessionTimeout = 20000;
    private ZooKeeper zkClient;

    @Before
    public void init() throws IOException {

        zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
//                System.out.println("-----------------------------");
//                List<String> children = null;
//                try {
//                    children = zkClient.getChildren("/", true);
//                    for (String child : children) {
//                        System.out.println(child);
//                    }
//                    System.out.println("-----------------------------");
//
//                } catch (KeeperException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    // 创建子节点
    @Test
    public void create() throws KeeperException, InterruptedException {
        // 参数 1：要创建的节点的路径；
        // 参数 2：节点数据 ；
        // 参数 3：节点权限 ；
        // 参数 4：节点的类型
        String nodeCreated = zkClient.create("/atguigu", "shuaige".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    // 获取子节点
    @Test
    public void getChildren() throws Exception {
        List<String> children = zkClient.getChildren("/", true);
        for (String child : children) {
            System.out.println(child);
        }
        // 延时阻塞
        Thread.sleep(Long.MAX_VALUE);
    }

    // 判断 znode 是否存在
    @Test
    public void exist() throws Exception {
        Stat stat = zkClient.exists("/atguigu", false);
        System.out.println(stat == null ? "not exist" : "exist");
    }


}

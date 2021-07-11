package com.atguigu.case1;

import com.atguigu.utils.ZkUtil;
import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * @author Feng
 * @date 2021/7/9 22:13
 * <p>
 * 服务器动态上下线监听案例服务器端代码
 */
public class DistributeServer {

    private String connectString = "node1:2181,node2:2181";
    private int sessionTimeout = 2000;
    private ZooKeeper zk;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        DistributeServer server = new DistributeServer();
        //获取zk连接
        server.getConnect();

        //注册服务器到ZK集群
        server.regist(args[0]);

        //启动业务逻辑(睡觉)
        server.business();

    }

    //启动业务逻辑(睡觉)
    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    //注册服务器到ZK集群
    private void regist(String hostname) throws InterruptedException, KeeperException {
        String creat = zk.create("/servers/" + hostname, hostname.getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostname + " is online!");
    }

    //获取zk连接
    private void getConnect() throws IOException {
        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }
}

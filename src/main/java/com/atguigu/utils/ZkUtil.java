package com.atguigu.utils;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import javax.swing.plaf.PanelUI;
import java.io.IOException;

/**
 * @author Feng
 * @date 2021/7/9 22:54
 */
public class ZkUtil {

     static String connectString = "node1:2181,node2:2181";
     static int sessionTimeout = 2000;
     static ZooKeeper zk;

    public static void getConnect() throws IOException {
        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }
}

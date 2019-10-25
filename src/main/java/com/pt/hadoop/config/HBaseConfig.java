package com.pt.hadoop.config;

import com.pt.hadoop.util.HBaseUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HBase相关配置
 *
 * @author zifangsky
 * @date 2018/7/12
 * @since 1.0.0
 */
@Configuration
public class HBaseConfig {
    @Value("${hbase.zookeeper.quorum}")
    private String nodes;

    @Value("${hbase.zookeeper.property.clientPort}")
    private String port;

    @Bean
    public HBaseUtil getHbaseUtil(){
        System.setProperty("hadoop.home.dir", "D:\\tools\\hadoop-3.1.2");
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum",nodes);
        conf.set("hbase.zookeeper.property.clientPort",port);
//        conf.set("hadoop.home.dir", "D:\\tools\\hadoop2.6_Win_x64");

        return new HBaseUtil(conf);
    }
}

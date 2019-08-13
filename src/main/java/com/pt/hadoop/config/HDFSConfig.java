package com.pt.hadoop.config;

import com.pt.hadoop.util.HDFSUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
/**
 * HDFS相关配置
 *
 * @author zifangsky
 * @date 2018/7/20
 * @since 1.0.0
 */
@Configuration
public class HDFSConfig {
 
    @Value("${HDFS.defaultFS}")
    private String defaultHDFSUri;
 
    @Bean
    public HDFSUtil getHbaseService(){
 
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
 
        conf.set("fs.defaultFS",defaultHDFSUri);
 
        return new HDFSUtil(conf,defaultHDFSUri);
    }
 
}
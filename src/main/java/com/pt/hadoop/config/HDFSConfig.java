package com.pt.hadoop.config;

import com.pt.hadoop.util.HDFSUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * HDFS相关配置
 *
 * @author zifangsky
 * @date 2018/7/20
 * @since 1.0.0
 */
@Configuration
public class HDFSConfig {

    @Value("${hdfs.defaultFS}")
    private String defaultHDFSUri;

    @Bean
    public HDFSUtil getHbaseService() {
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();

        String krb5File = "src/main/resources/krb5.conf";
        String user = "hadoop/node1@HADOOP.COM";
        String keyPath = "src/main/resources/hadoop.keytab";

        System.setProperty("java.security.krb5.conf", krb5File);

        conf.set("fs.defaultFS", defaultHDFSUri);
        conf.set("hadoop.security.authentication", "kerberos");
        conf.addResource("src/main/resources/hdfs-site.xml");
        conf.addResource("src/main/resources/core-site.xml");
        conf.addResource("src/main/resources/yarn-site.xml");
        UserGroupInformation.setConfiguration(conf);
        try {
            UserGroupInformation.loginUserFromKeytab(user, keyPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new HDFSUtil(conf, defaultHDFSUri);
    }

}

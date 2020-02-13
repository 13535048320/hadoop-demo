package com.pt.hadoop;

import com.pt.hadoop.util.HDFSUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;

public class HDFSTest {
    public static void main(String[] args) {
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();

        String defaultHDFSUri = "hdfs://node1:9000";
        String krb5File = "src/main/resources/krb5.conf";
        String user = "hadoop/node1@HADOOP.COM";
        String keyPath = "src/main/resources/hadoop.keytab";

        conf.set("fs.defaultFS",defaultHDFSUri);
        conf.set("hadoop.security.authentication", "kerberos");
        conf.set("hadoop.security.authorization", "true");
        conf.addResource("src/main/resources/hdfs-site.xml");
        conf.addResource("src/main/resources/core-site.xml");
        conf.addResource("src/main/resources/yarn-site.xml");

        System.setProperty("java.security.krb5.conf", krb5File);
        UserGroupInformation.setConfiguration(conf);
        try {
            UserGroupInformation.loginUserFromKeytab(user, keyPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HDFSUtil util = new HDFSUtil(conf,defaultHDFSUri);
        System.out.println(util.checkExists("/test.txt"));
    }
}

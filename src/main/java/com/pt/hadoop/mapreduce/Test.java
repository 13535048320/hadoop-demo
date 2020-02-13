package com.pt.hadoop.mapreduce;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws Exception {
        String krb5File = "src/main/resources/krb5.conf";
        System.setProperty("java.security.krb5.conf", krb5File);

        String user = "hadoop/node1@HADOOP.COM";
        String keyPath = "src/main/resources/hadoop.keytab";

        JobConf conf = new JobConf();
        conf.set("fs.defaultFS", "hdfs://node1:9000");
        conf.set("hadoop.security.authentication", "kerberos");
        conf.set("hadoop.security.authorization", "true");
        conf.addResource("src/main/resources/hdfs-site.xml");
        conf.addResource("src/main/resources/core-site.xml");
        conf.addResource("src/main/resources/yarn-site.xml");
        UserGroupInformation.setConfiguration(conf);
        try {
            UserGroupInformation.loginUserFromKeytab(user, keyPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.setProperty("hadoop.home.dir", "D:\\tools\\hadoop-3.1.2");
        System.load("D:\\tools\\hadoop.dll");
        Job job = new Job(conf, "Score Average");
        job.setJarByClass(Test.class);

        // 设置Map、Combine和Reduce处理类
        job.setMapperClass(Map.class);
        job.setCombinerClass(Reduce.class);
        job.setReducerClass(Reduce.class);

        // 设置输出类型
        job.setOutputKeyClass(Text.class);

        job.setOutputValueClass(IntWritable.class);

        // 将输入的数据集分割成小数据块splites，提供一个RecordReder的实现
        job.setInputFormatClass(TextInputFormat.class);

        // 提供一个RecordWriter的实现，负责数据输出
        job.setOutputFormatClass(TextOutputFormat.class);

        for (int i = 1; i < 4; i++) {
            FileInputFormat.addInputPath(job, new Path("hdfs://node1:9000/test/test" + i + ".txt"));
        }

        FileOutputFormat.setOutputPath(job, new Path("hdfs://node1:9000/test/result"));
        if (job.waitForCompletion(true)) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}

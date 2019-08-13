package com.pt.hadoop.MapReduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobPriority;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class Test {
    public static void main(String[] args) throws Exception {

        JobConf conf = new JobConf();
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

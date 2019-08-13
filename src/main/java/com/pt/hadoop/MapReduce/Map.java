package com.pt.hadoop.MapReduce;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

public class Map extends Mapper<LongWritable, Text, Text, IntWritable> {

    // 实现map函数
    public void map(LongWritable key, Text value, Context context)

            throws IOException, InterruptedException {

        // 将输入的纯文本文件的数据转化成String
        String line = value.toString();

        // 将输入的数据首先按行进行分割
        StringTokenizer tokenizerArticle = new StringTokenizer(line, "\n");

        // 分别对每一行进行处理
        while (tokenizerArticle.hasMoreElements()) {
            // 每行按空格划分
            StringTokenizer tokenizerLine = new StringTokenizer(tokenizerArticle.nextToken());

            String strName = tokenizerLine.nextToken();// 学生姓名部分

            String strScore = tokenizerLine.nextToken();// 成绩部分

            Text name = new Text(strName);
            int scoreInt = Integer.parseInt(strScore);
            // 输出姓名和成绩
            context.write(name, new IntWritable(scoreInt));
        }
    }
}
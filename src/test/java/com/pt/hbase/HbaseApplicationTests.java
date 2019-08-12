package com.pt.hbase;

import com.pt.hbase.util.HBaseUtil;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.shaded.protobuf.generated.FilterProtos;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HbaseApplicationTests {

    @Autowired
    private HBaseUtil hbaseUtil;

    @Test
    public void contextLoads() {
    }

    private String namespace = "demo";
    private String tableName = namespace + ":user";
    private String[] personalInfoColumns = new String[]{"name", "age"};
    private String[] employeeInfoColumns = new String[]{"salary", "role"};

    @Test
    public void createTable() {
        //创建命名空间
        hbaseUtil.createNamespace(namespace);

        //删除表
        hbaseUtil.deleteTable(tableName);

        //创建表
        //第一个参数为 命名空间:表名; 第二个参数为列族
        hbaseUtil.createTable(tableName, Arrays.asList("personalInfo", "employeeInfo"));
    }

    @Test
    public void insert() {
        //插入三条数据
        hbaseUtil.putData(tableName, "test_01", "personalInfo", personalInfoColumns,
                new String[]{"张志成", "24"});
        hbaseUtil.putData(tableName, "test_01", "employeeInfo", employeeInfoColumns,
                new String[]{"100", "admin"});

        hbaseUtil.putData(tableName, "test_02", "personalInfo", personalInfoColumns,
                new String[]{"zzc", "23"});
        hbaseUtil.putData(tableName, "test_02", "employeeInfo", employeeInfoColumns,
                new String[]{"1000", "staff"});

        hbaseUtil.putData(tableName, "test_03", "employeeInfo", employeeInfoColumns,
                new String[]{"1000", "staff"});
        hbaseUtil.putData(tableName, "test_03", "personalInfo", personalInfoColumns,
                new String[]{"zzc123", "22"});

    }

    @Test
    public void query() {
        //根据rowKey查询
        Map<String, String> result1 = hbaseUtil.getRowData(tableName, "test_01");
        System.out.println("+++++++++++根据rowKey查询+++++++++++");
        result1.forEach((k, value) -> {
            System.out.println(k + "---" + value);
        });
        System.out.println();

        //精确查询某个单元格的数据
        String str1 = hbaseUtil.getColumnValue(tableName, "test_02", "personalInfo", "name");
        System.out.println("+++++++++++精确查询某个单元格的数据+++++++++++");
        System.out.println(str1);
        System.out.println();

        //遍历查询全表
        Map<String, Map<String, String>> result2 = hbaseUtil.getResultScanner(tableName);
        System.out.println("+++++++++++遍历查询+++++++++++");
        result2.forEach((k, value) -> {
            System.out.println(k + "---" + value);
        });
        System.out.println();

        //根据rowkey查询
        String rowKey = "test_02";
        Map<String, Map<String, String>> result3 = hbaseUtil.scanByRowKey(tableName, rowKey, CompareOperator.EQUAL);
        System.out.println("+++++++++++根据rowkey查询+++++++++++");
        result3.forEach((k, value) -> {
            System.out.println(k + "---" + value);
        });
        System.out.println();


        //行键过滤
        rowKey = "test_03";
        String regex = ".*" + rowKey + ".*";
        RowFilter rowFilter = new RowFilter(CompareOperator.EQUAL, new RegexStringComparator(regex));
        //列族过滤
        String family = "personal";
        regex = ".*" + family + ".*";
        FamilyFilter familyFilter = new FamilyFilter(CompareOperator.EQUAL, new RegexStringComparator(regex));
        //列过滤
        String Qualifier = "name";
        regex = ".*" + Qualifier + ".*";
        QualifierFilter columnFilter = new QualifierFilter(CompareOperator.EQUAL, new RegexStringComparator(regex));
        FilterList filterList = new FilterList(rowFilter, familyFilter, columnFilter);
        Map<String, Map<String, String>> result4 = hbaseUtil.scanByFilter(tableName, filterList);
        System.out.println("+++++++++++根据rowkey查询+++++++++++");
        result4.forEach((k, value) -> {
            System.out.println(k + "---" + value);
        });
    }
}

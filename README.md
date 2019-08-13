# Hadoop (双NameNode)
## 1. 下载
下载地址: https://hadoop.apache.org/releases.html

下载 binary

## 2. 安装zookeeper
下载地址：https://www.apache.org/dyn/closer.cgi#verify
```
解压：
    tar -xf apache-zookeeper-3.5.5-bin.tar.gz
    
修改配置：
    cd conf/
    cp zoo_sample.cfg zoo.cfg
    vi zoo.cfg
    修改
        存储路径 dataDir=
        端   口  clientPort=
        
启动：
    cd bin/
    ./zkServer.sh start
    
查看端口：
    lsof -i:2181
```

## 3. 配置
组件|功能
:--:|:--:
NameNode | 指导Slave端的DataNode执行I/O任务，他跟踪文件如何分割成文件块，然后被什么节点存储，分布式文件系统的运行状态是否正常。
DataNode | 每个Slave节点都会有个DataNode守护进程来执行文件系统的工作——将HDFS数据块读取或者写入本地系统的实际文件中。
SecondaryNameNode | 备份系统，备份NameNode的数据
DFSZKFailoverController | NameNode HA实现的中心组件，它负责整体的故障转移控制等。
ResourceManager | 当应用程序对集群资源需求时，ResourceManager是Yarn集群主控节点，负责协调和管理整个集群（所有NodeManager）的资源。
NodeManager | 管理Hadoop集群中单个计算节点，功能包含与ResourceManager保持通信，管理Container的生命周期、监控每一个Container的资源使用(内存、CPU等）情况、追踪节点健康状况、管理日志和不同应用程序用到的附属服务等。
JournalNode | NameNode通过其进行相互通信，active NameNode 将 edit log 写入这些JournalNode，而 standby NameNode 读取这些 edit log，并作用在内存中的目录树中。



机器|服务
:--:|:--:
node1|Zookeeper、NameNode、SecondaryNameNode、DFSZKFailoverController、ResourceManager、DataNode、NodeManager、JournalNode
node2|Zookeeper、NameNode、SecondaryNameNode、DFSZKFailoverController、ResourceManager、DataNode、NodeManager、JournalNode


解压:
```
tar -xf hadoop-3.1.2.tar.gz
```

配置环境变量
```
# hadoop
export HADOOP_HOME=/路径/hadoop-3.1.2
export PATH=$HADOOP_HOME/bin:$PATH
export PATH=$HADOOP_HOME/sbin:$PATH
```

配置文件:
```
cd hadoop-3.1.2/etc/hadoop
```

<b>hdfs-site.xml 配置</b>
```
<configuration>

        <!--指定hdfs的nameservice为mycluster，需要和core-site.xml中的fs.defaultFS对应 -->
        <property>
                <name>dfs.nameservices</name>
                <value>mycluster</value>
        </property>

        <!-- mycluster下面有两个NameNode，分别是nn1，nn2, 名字可自定义, 注意与通信地址配置一致即可 -->
        <property>
                <name>dfs.ha.namenodes.mycluster</name>
                <value>nn1,nn2</value>
        </property>

        <!-- nn1的RPC通信地址, 注意name最后两个名称 -->
        <property>
                <name>dfs.namenode.rpc-address.mycluster.nn1</name>
                <value>node1:9000</value>
        </property>

        <!-- nn1的http通信地址 -->
        <property>
                <name>dfs.namenode.http-address.mycluster.nn1</name>
                <value>node1:50070</value>
        </property>

        <!-- nn2的RPC通信地址 -->
        <property>
                <name>dfs.namenode.rpc-address.mycluster.nn2</name>
                <value>node2:9000</value>
        </property>
        <!-- nn2的http通信地址 -->
        <property>
                <name>dfs.namenode.http-address.mycluster.nn2</name>
                <value>node2:50070</value>
        </property>

        <!-- 设置一组 journalNode 的 URI 地址，active NameNode 将 edit log 写入这些JournalNode -->
        <!-- 而 standby NameNode 读取这些 edit log，并作用在内存中的目录树中。-->
        <property>
                <name>dfs.namenode.shared.edits.dir</name>
                <value>qjournal://node1:8485;node2:8485/mycluster</value>
        </property>

        <!-- JournalNode 存储路径 -->
        <property>
                <name>dfs.journalnode.edits.dir</name>
                <value>/data/hadoop/journal/</value>
        </property>

        <!-- 数据块大小，默认为64M -->
        <property>
                <name>dfs.block.size</name>
                <value>134217728</value>
        </property>

        <!-- 对于大集群或者有大量客户端的集群来说，通常需要增大参数dfs.namenode.handler.count的默认值10。-->
        <!-- 设置该值的一般原则是将其设置为集群大小的自然对数乘以20，即20logN，N为集群大小。-->
        <!-- 集群大小是如何定义的，是datanode节点数量吗？？？-->
        <property>
                <name>dfs.namenode.handler.count</name>
                <value>20</value>
        </property>

        <!-- DataNode 存储路径 -->
        <property>
                <name>dfs.datanode.data.dir</name>
                <value>/data/hadoop/data/</value>
        </property>

        <!-- 块副本数 -->
        <property>
                <name>dfs.replication</name>
                <value>2</value>
        </property>

        <!-- 关闭权限管理 -->
        <property>
                <name>dfs.permissions</name>
                <value>false</value>
        </property>

        <!-- NameNode 存储路径 -->
        <property>
                <name>dfs.namenode.name.dir</name>
                <value>/data/hadoop/name/</value>
        </property>

        <!-- 开启NameNode故障时自动切换 -->
        <property>
                <name>dfs.ha.automatic-failover.enabled</name>
                <value>true</value>
        </property>

        <!-- 指定mycluster（与最上方dfs.nameservices配置对应）出故障时，哪个实现类负责执行故障切换 -->
        <property>
                <name>dfs.client.failover.proxy.provider.mycluster</name>
                <value>org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider</value>
        </property>
        
        <!-- 发生故障时，避免两个NameNode都为Active状态，使用ssh方式kill掉一个 -->
        <property>
                <name>dfs.ha.fencing.methods</name>
                <value>
                    sshfence
                    shell(/bin/true)
                </value>
        </property>
        
        <!-- 配置sshfence使用的私钥路径 -->
        <property>
                <name>dfs.ha.fencing.ssh.private-key-files</name>
                <value>/root/.ssh/id_rsa</value>
        </property>
        
        <!-- 配置sshfence超时时长 -->
        <property>
                <name>dfs.ha.fencing.ssh.connect-timeout</name>
                <value>5000</value>
        </property>

</configuration>
```

<b>core-site.xml 配置</b>
```
<configuration>

  <!-- 指定hdfs的nameservice为node，需要和hdfs-site.xml中的dfs.nameservices对应 -->
  <property>
      <name>fs.defaultFS</name>
      <value>hdfs://mycluster</value>
  </property>
  
  <!--指定hadoop数据临时存放目录-->
  <property>
      <name>hadoop.tmp.dir</name>
      <value>/data/hadoop/tmp</value>
  </property>
 
  <!--指定hdfs操作数据的缓冲区大小 可以不配-->
  <property>
      <name>io.file.buffer.size</name>
      <value>131072</value>
  </property>

  <!--指定zookeeper地址-->
  <property>
      <name>ha.zookeeper.quorum</name>
      <value>node1:2181,node2:2181</value>
  </property>

</configuration>
```

<b>yarn-site.xml 配置</b>
```
<configuration>
    <property>
        <name>yarn.resourcemanager.ha.enabled</name>
        <value>true</value>
    </property>
    <property>
        <name>yarn.resourcemanager.cluster-id</name>
        <value>cluster1</value>
    </property>
    <property>
        <name>yarn.resourcemanager.ha.rm-ids</name>
        <value>rm1,rm2</value>
    </property>
    <property>
        <name>yarn.resourcemanager.hostname.rm1</name>
        <value>node1</value>
    </property>
    <property>
        <name>yarn.resourcemanager.hostname.rm2</name>
        <value>node2</value>
    </property>
    <property>
        <name>yarn.resourcemanager.webapp.address.rm1</name>
        <value>node1:8088</value>
    </property>
    <property>
        <name>yarn.resourcemanager.webapp.address.rm2</name>
        <value>node2:8088</value>
    </property>
    <property>
        <name>yarn.resourcemanager.zk-address</name>
        <value>node1:2181,node2:2181</value>
    </property>
    <!-- 指定nodemanager启动时加载server的方式为shuffle server -->
    <property>    
        <name>yarn.nodemanager.aux-services</name>    
        <value>mapreduce_shuffle</value>    
    </property>  
</configuration>
```


<b>hadoop-env.sh</b>
```
# 配置JAVA_HOME
export JAVA_HOME=
```

<b>start-dfs.sh、stop-dfs.sh</b>
```
cd ../../sbin
添加：
    HDFS_DATANODE_USER=root
    HDFS_DATANODE_SECURE_USER=hdfs
    HDFS_NAMENODE_USER=root
    HDFS_SECONDARYNAMENODE_USER=root
    HDFS_JOURNALNODE_USER=root
    HDFS_ZKFC_USER=root
```

<b>start-yarn.sh、stop-yarn.sh</b>
```
添加：
    YARN_RESOURCEMANAGER_USER=root
    HADOOP_SECURE_DN_USER=yarn
    YARN_NODEMANAGER_USER=root
```

同步所有配置
```
scp conf/* node2:/路径/conf/
```

## 4. 启动
### 4.1 启动JournalNode
```
hadoop-daemons.sh start journalnode
```

### 4.2 格式化zkfc
```
node1机器执行
    hdfs zkfc -formatZK
```

### 4.3 格式化hdfs
```
node1机器执行
    hadoop namenode -format
```

### 4.4 启动NameNode
```
node1机器执行
    hadoop-daemon.sh start namenode

node2机器执行
    hdfs namenode -bootstrapStandby #把NameNode的数据同步到node2上
    hadoop-daemon.sh start namenode
```

### 4.5 启动DataNode
```
hadoop-daemons.sh start datanode
```

### 4.6 启动yarn
```
start-yarn.sh
```

### 4.7 启动ZKFC
```
hadoop-daemon.sh start zkfc
```

## 5. 查看运行情况
```
使用 jps 命令查看各个组件是否在运行

访问
    http://node1:50070
    http://node2:50070
    查看NameNode是否一个为active，另一个为standby

    在页面上选择上方DataNodes查看datanode节点是否齐全

使用简单的hdfs命令
    hdfs dfs -fs hdfs://node1:9000 -ls /
    hdfs dfs -fs hdfs://node1:9000 -mkdir /hbase
    
    注意node1要替换为active状态的NameNode，不能用standby状态的
```

# Hbase
## 1. 下载
下载地址： http://hbase.apache.org/downloads.html

## 2. 配置
hbase-site.xml
```
<configuration>
    <!-- 指定hbase在HDFS上存储的路径 -->
    <property>
        <name>hbase.rootdir</name>
        <value>hdfs://node1:9000/data/hbase</value>
    </property>

    <!-- 指定hbase是分布式的 -->
    <property>
        <name>hbase.cluster.distributed</name>
        <value>true</value>
    </property>
    
    <!-- 指定zookeeper的地址，多个用“,”分割 -->
    <property>
        <name>hbase.zookeeper.quorum</name>
        <value>node1,node2</value>
    </property>
    
    <!-- 指定zookeeper端口 -->
    <property>
        <name>hbase.zookeeper.property.clientPort</name>
            <value>2181</value>
    </property>
    
    <!-- Master绑定的端口，包括backup-master -->
    <property>
        <name>hbase.master.port</name>
        <value>16000</value>
    </property>
    
    <!-- zookeeper存储路径 -->
    <property>
        <name>hbase.zookeeper.property.dataDir</name>
        <value>/data/zookeeper</value>
    </property>
    
    <!-- 临时文件存储路径 -->
    <property>
        <name>hbase.tmp.dir</name>
        <value>/data/tmp</value>
    </property>
    
    <property>
        <name>zookeeper.znode.parent</name>
        <value>/hbase</value>
    </property>
    
    <property>
        <name>hbase.unsafe.stream.capability.enforce</name>
        <value>false</value>
    </property>
</configuration>
```

## 3. 启动
```
配置环境变量
# hbase
export HBASE_HOME=/路径/hbase-2.2.0
export PATH=$HBASE_HOME/bin:$PATH

启动
start-hbase.sh
```
## 4. 个人未解之谜
```
    在hbase配置里，hbase.rootdir配置项怎么实现HDFS高可用，在网上找到的均是说配置为
    <property>
        <name>hbase.rootdir</name>
        <value>hdfs://mycluster/data/hbase</value>
    </property>
    
    mycluster 与 HDFS 配置里的 dfs.nameservices 项一致
    同时将hadoop的配置文件hdfs-site.xml和core-site.xml复制到hbase的conf目录下。
    
    但是启动后，HMaster会报错找不到host：mycluster
    
```

# Springboot 中使用hbase
## 1. maven依赖
```
<!-- https://mvnrepository.com/artifact/org.apache.hbase/hbase-client -->
    <dependency>
        <groupId>org.apache.hbase</groupId>
        <artifactId>hbase-client</artifactId>
        <version>2.1.3</version>
    </dependency>
```

## 2. 使用
```
详情请参照项目中application.properties、HBaseConfig、HBaseUtil 和 HbaseApplicationTests
```

# MapReduce 计算平均分例子
## 1. Windows下使用需要
```
 下载路径 https://github.com/cdarlint/winutils
 下载对应版本的 hadoop.all 和 winutils.exe
 并将 hadoop.dll 放到 C 盘的Windows/System32目录下
 将 winutils.exe 放到 windows 的 %HADOOP_HOME%\bin目录下
```

## 2. maven依赖
```
    <dependency>
        <groupId>org.apache.hadoop</groupId>
        <artifactId>hadoop-mapreduce-client-core</artifactId>
        <version>3.1.2</version>
    </dependency>
    <dependency>
        <groupId>org.apache.hadoop</groupId>
        <artifactId>hadoop-common</artifactId>
        <version>3.1.2</version>
    </dependency>
```

## 3. 使用
```
详情请参照 MapReduce 目录下的 Test.class
```

## 4. 问题
```
问题1: 
    错误 org.apache.hadoop.io.nativeio.NativeIO$Windows.createDirectoryWithMode0(Ljava/lang/String;I)V

原因: 
    windows 下的 hadoop.dll 和 winutils.exe 与 hadoop 服务器版本不一致
    
解决办法: 
    下载路径 https://github.com/cdarlint/winutils
    下载对应版本的 hadoop.all 和 winutils.exe
    并将 hadoop.dll 放到 C 盘的Windows/System32目录下
    将 winutils.exe 放到 windows 的 %HADOOP_HOME%\bin目录下
```

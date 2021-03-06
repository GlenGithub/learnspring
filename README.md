# learnspring
Spring学习记录

## 配置云主机
CentOS 6.5

### 登录云主机
打开Xshell，点击新建，根据要求输入相应参数
```
名称：自定义设置
协议：SSH
主机：主机公网IP
端口号：22
```
选择用户身份认证
```
方法选择：Password
用户名：默认用户名为root
密码：在主机中自定义的密码
```

### 安装JDK
yum安装
```
# yum list java*
# yum install -y java-1.8.0-openjdk*
```

### 安装tomcat
创建个人文件夹
```
# cd /home
# mkdir wcy
# cd wcy
```
下载并解压
```
# wget http://mirror.bit.edu.cn/apache/tomcat/tomcat-7/v7.0.81/bin/apache-tomcat-7.0.81.tar.gz
# tar -zxf apache-tomcat-7.0.81.tar.gz
```
启动tomcat
```
# cd apache-tomcat-7.0.81/bin/
# ./startup.sh
```
设置开机自启
```
# vi /etc/rc.d/rc.local
添加
/home/wcy/software/apache-tomcat-7.0.81/bin/startup.sh
```

### 安装lrzsz
```
# yum install -y lrzsz
```

### 安装MySQL
yum安装
```
# yum install -y mysql-server
```
设置开机自启
```
# chkconfig mysqld on
```
启动MySql服务
```
# service mysqld start
```
设置root用户密码
```
// 登录root
# mysql -u root
// 查询用户的密码，都为空
mysql> select user,host,password from mysql.user;
// 设置root的密码为yourpassword
mysql> set password for root@localhost=password('yourpassword');
mysql> exit
```
用新密码登录
```
# mysql -u root -p
```
开放远程登录权限
```
mysql> GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'yourpassword' WITH GRANT OPTION;
mysql> FLUSH PRIVILEGES;
```

**设置MySQL编码**

查看编码
```
mysql> SHOW VARIABLES LIKE 'character%';
```

设置编码
```
退出MySQL
mysql> exit

编辑/etc/my.cnf，添加

[client]
default-character-set=utf8

[mysql]
default-character-set=utf8

[mysqld]
character-set-server=utf8

重启MySQL
# service mysqld restart

重新连接MySQL
# mysql -u root -p
```

### 安装Redis
下载并解压
```
# cd /home/wcy/software/
# wget http://download.redis.io/releases/redis-4.0.1.tar.gz
# tar -zxf redis-4.0.1.tar.gz
```
编译及安装
```
# cd redis-4.0.1
# make && make install
```
复制配置文件
```
# cp redis.conf /etc/
```
修改配置文件
```
# vi /etc/redis.conf

找到`daemonize`改为`yes`设置后台运行
找到`requirepass`设置密码
```
设置开机自启
```
复制启动文件
# cp utils/redis_init_script /etc/init.d/redis

修改配置
# vi /etc/init.d/redis
在#!/bin/sh下添加
# add by wangchenyan
# chkconfig: 2345 90 10
# description: Redis is a persistent key-value database
修改CONF文件位置
CONF="/etc/redis.conf"

保存退出

启动服务
# service redis start

设置开机自启
# chkconfig redis on

测试
# redis-cli
```

解决`redis.clients.jedis.HostAndPort - cant resolve localhost address`
```
查看Linux系统的主机名
# hostname
jdu4e00u53f7.jcloud.local
查看/etc/hosts文件中是否有127.0.0.1对应主机名，如果没有则添加
# vi /etc/hosts
127.0.0.1   localhost ... jdu4e00u53f7.jcloud.local
```

### 安装Git
yum安装
```
# yum install -y git
```

### 安装Maven
下载并解压
```
# cd /home/wcy/software/
# wget http://mirror.bit.edu.cn/apache/maven/maven-3/3.5.0/binaries/apache-maven-3.5.0-bin.tar.gz
# tar -zxf apache-maven-3.5.0-bin.tar.gz
```
添加环境变量
```
# vi /etc/profile
添加
export MAVEN_HOME=/home/wcy/software/apache-maven-3.5.0
export PATH=$PATH:$MAVEN_HOME/bin
```
刷新环境变量
```
# source /etc/profile
```
查看是否添加成功
```
# mvn -version
```

### 添加一键部署脚本
clone项目
```
# cd /home/wcy/project/
# git clone https://github.com/wangchenyan/learnspring.git
```
复制打包脚本
```
# cp /home/wcy/project/learnspring/learnspring.sh /usr/local/bin/
```
添加可执行权限
```
# chmod +x /usr/local/bin/learnspring.sh
```
一键部署
```
# learnspring.sh
```
添加开机自动部署
```
# chmod +x /etc/rc.d/rc.local
# vi /etc/rc.d/rc.local
添加
/usr/local/bin/learnspring.sh
```

### 安装 Zookeeper

1. 下载解压
```
# cd /home/wcy/software/
# wget http://mirrors.hust.edu.cn/apache/zookeeper/stable/zookeeper-3.4.10.tar.gz
# tar -zxf zookeeper-3.4.10.tar.gz
```
2. 配置
```
# cd zookeeper-3.4.10/conf/
# cp zoo_sample.cfg zoo.cfg
```
3. 启动
```
# cd zookeeper-3.4.10/bin/
# ./zkServer.sh start
```

### 安装 Dubbo 管理界面

克隆Dubbo项目，打包dubbo-admin，把打包后的war放到tomcat

### 安装 RabbitMQ

1. 安装
```
// 安装Erland
# wget https://dl.bintray.com/rabbitmq/rpm/erlang/19/el/6/x86_64/erlang-19.3.6.5-1.el6.x86_64.rpm
# yum -y install erlang-19.3.6.5-1.el6.x86_64.rpm
// 安装socat
# wget –no-cache http://www.convirture.com/repos/definitions/rhel/6.x/convirt.repo -O /etc/yum.repos.d/convirt.repo
# yum install socat
// 安装RabbitMQ
# wget https://dl.bintray.com/rabbitmq/all/rabbitmq-server/3.7.1/rabbitmq-server-3.7.1-1.el6.noarch.rpm
# yum -y install rabbitmq-server-3.7.1-1.el6.noarch.rpm
```
2. 启动
```
# rabbitmq-server start
```
3. 添加用户
```
// 用户名和密码都是root
# rabbitmqctl add_user root root
// 授予管理权限
# rabbitmqctl set_user_tags root administrator
// 授予访问权限
# rabbitmqctl set_permissions -p / root '.*' '.*' '.*'
```
4. 开启管理界面插件
```
# rabbitmq-plugins enable rabbitmq_management
// 重启RabbitMQ
# rabbitmq-server stop
// 后台启动
# rabbitmq-server -detached
// 通过以下地址访问管理界面
http://本地IP:15672
```

### 安装Kafka

1. 下载解压
```
# wget http://mirrors.hust.edu.cn/apache/kafka/1.0.0/kafka_2.11-1.0.0.tgz
# tar -zxf kafka_2.11-1.0.0.tgz
```
2. 启动
```
# cd kafka_2.11-1.0.0
# bin/kafka-server-start.sh config/server.properties &
```
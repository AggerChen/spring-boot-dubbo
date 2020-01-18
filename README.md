## SpringBoot集成org.apache.Dubbo

### 一、前言
Dubbo作为一款优秀的RPC框架，在国内有着众多的使用者，自从2018年2月，Dubbo被阿里捐献给Apache基金会以后，Dubbo似乎以全新的名称 Apache Dubbo焕发了新的生命力。虽然有着同样优秀和优势的Spring Clould框架，但是Dubbo能焕发新生也是一件好事，期待着Dubbo能够完善机制，更新迭代出的更好。

所有代码我已上传到github，需要的可以自取测试，欢迎start，传送门[
https://github.com/AggerChen/spring-boot-dubbo](
https://github.com/AggerChen/spring-boot-dubbo)

### 二、项目简介
在Dubbo新的官网上查看资料，虽然也有很多demo发现要不还是旧包名com.alibaba.dubbo，要不就是集成的SpringBoot1.x，根本没法满足新的需要嘛，既然都已经是Apache Dubbo了，当然要使用最新的org.apache.dubbo和最新的SpringBoot2.x版本了~
此篇文章就是以一下版本来展示示例：
> SpringBoot 2.2.2
> org.apache.dubbo 2.7.5
> zookeeper 3.4.14

### 三、项目开发
既然是Dubbo项目，那么我们的使用场景就是分布式微服务，多个service和一个consumer，还需要一个公共项目common来编写公用的类与接口。

#### 3.1 创建项目spring-boot-dubbo
1. 在IDEA中创建一个project，取名spring-boot-dubbo。这是一个根项目，其中pom.xml包含依赖如下：
>pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- 父级引用 -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.2.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <!-- 基本信息 -->
    <groupId>com.agger</groupId>
    <artifactId>spring-boot-dubbo</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    <name>${project.artifactId}</name>
    <description>dubbo根项目</description>

    <!--配置-->
    <properties>
        <skip_maven_deploy>true</skip_maven_deploy>
        <dubbo.version>2.7.5</dubbo.version>
        <zookeeper.version>3.4.14</zookeeper.version>
        <spring.version>5.2.2.RELEASE</spring.version>
        <dubbo.common>0.0.1-SNAPSHOT</dubbo.common>
    </properties>


    <modules>
        <module>dubbo-common</module>
        <module>dubbo-consumer</module>
        <module>dubbo-provider</module>
    </modules>

    <!--声明全局依赖（子项目需要显示的引用才会继承依赖）-->
    <dependencyManagement>
        <dependencies>

            <!-- dubbo依赖 -->
            <dependency>
                <groupId>org.apache.dubbo</groupId>
                <artifactId>dubbo</artifactId>
                <version>${dubbo.version}</version>
                <exclusions>
                    <exclusion>
                        <groupId>org.springframework</groupId>
                        <artifactId>spring</artifactId>
                    </exclusion>
                    <exclusion>
                        <groupId>javax.servlet</groupId>
                        <artifactId>servlet-api</artifactId>
                    </exclusion>
                    <exclusion>
                        <groupId>log4j</groupId>
                        <artifactId>log4j</artifactId>
                    </exclusion>
                </exclusions>
            </dependency>

            <!-- dubbo-start依赖 -->
            <dependency>
                <groupId>org.apache.dubbo</groupId>
                <artifactId>dubbo-spring-boot-starter</artifactId>
                <version>${dubbo.version}</version>
            </dependency>

            <!-- zookeeper依赖 -->
            <dependency>
                <groupId>org.apache.zookeeper</groupId>
                <artifactId>zookeeper</artifactId>
                <version>${zookeeper.version}</version>
                <exclusions>
                    <exclusion>
                        <artifactId>slf4j-log4j12</artifactId>
                        <groupId>org.slf4j</groupId>
                    </exclusion>
                </exclusions>
            </dependency>
            <dependency>
                <groupId>com.github.sgroschupf</groupId>
                <artifactId>zkclient</artifactId>
                <version>0.1</version>
            </dependency>
            <dependency>
                <groupId>org.apache.curator</groupId>
                <artifactId>curator-recipes</artifactId>
                <version>4.2.0</version>
            </dependency>
            <dependency>
                <groupId>org.apache.curator</groupId>
                <artifactId>curator-framework</artifactId>
                <version>4.2.0</version>
            </dependency>

        </dependencies>
    </dependencyManagement>

    <!--声明全局依赖（子项目不需要显示的引用，自动继承依赖）-->
    <dependencies>

        <!--spring-boot依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <!-- lombook依赖 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- 公共项目依赖 -->
        <dependency>
            <groupId>com.agger</groupId>
            <artifactId>dubbo-common</artifactId>
            <version>${dubbo.common}</version>
        </dependency>

    </dependencies>

    <!-- 打包插件 -->
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```
2. 通过IDEA在项目spring-boot-dubbo中创建三个module，分别为：dubbo-common、dubbo-consumer、dubbo-privider。生成的目录结构为：
![1](https://github.com/AggerChen/imageLibrary/blob/master/spring-boot-dubbo/Image.png?raw=true)

#### 3.2 dubbo-comm项目
此项目是一个公共项目，不需要其他多余的依赖，只是编写接口和公共的类使用。
编写一个接口名为HelloService：
>HelloService.java
```java
package com.agger.dubbocommon.service;
/**
 * @classname: HelloService
 * @description: Hello服务接口
 * @author chenhx
 * @date 2020-01-14 13:55:38
 */
public interface HelloService {
    String sayHello(String name);
}
```
>pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.agger</groupId>
        <artifactId>spring-boot-dubbo</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <groupId>com.agger</groupId>
    <artifactId>dubbo-common</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>dubbo-common</name>
    <packaging>jar</packaging>
    <description>dubbo公共项目</description>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```
#### 3.3 dubbo-provider项目
此项目是一个服务类项目，也就是将接口服务注册到zookeeper注册中心供消费端调取使用。
1. 编写pom文件依赖
>pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.agger</groupId>
        <artifactId>spring-boot-dubbo</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <!-- 基本信息 -->
    <groupId>com.agger</groupId>
    <artifactId>dubbo-provider</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>dubbo-provider</name>
    <packaging>jar</packaging>
    <description>dubbo项目服务端</description>

    <dependencies>
        <!-- dubbo依赖 -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
        </dependency>
        <!-- zookeeper依赖 -->
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>${zookeeper.version}</version>
            <exclusions>
                <exclusion>
                    <artifactId>slf4j-log4j12</artifactId>
                    <groupId>org.slf4j</groupId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>com.github.sgroschupf</groupId>
            <artifactId>zkclient</artifactId>
            <version>0.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-recipes</artifactId>
            <version>4.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-framework</artifactId>
            <version>4.2.0</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```
2. 编写一个service类实现接口
注意：@Service注解是dubbo里的注解，不是spring里的注解

>HelloServiceImpl.java
```java
package com.agger.dubboprovider.impl;

import com.agger.dubbocommon.service.HelloService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

/**
 * @classname: HelloServiceImpl
 * @description: HelloService实现
 * @author chenhx
 * @date 2020-01-14 14:11:39
 */

@Service
@Component
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        System.out.println("dubbo服务调用：" + name);
        return "早上好啊~" + name;
    }
}
```
3. 在springBoot启动类上添加配置
@EnableDubboConfig注解表示开启Dubbo的相关配置
@DubboComponentScan用来扫描提供的接口实现位置

>DubboProviderApplication.java
```java
package com.agger.dubboprovider;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubboConfig
@DubboComponentScan("com.agger.dubboprovider.impl")
@SpringBootApplication
public class DubboProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(DubboProviderApplication.class, args);
    }
}
```
4. 添加dubbo配置
dubbo添加配置的方式有多种，既有xml和properties也有API方式。既然我们使用的是SpringBoot那么就应该消灭xml的配置方式，使用properties和yml方式都就可以，在此使用yml作为示例。

>application.yml
```yml
server:
  port: 8087    # 服务端口

dubbo:
  application:
    id: dubbo-provider
    name: dubbo-provider  #应用名称
    owner: aggerChen      #应用所属者
    organization: agger   #应用所属组织
  registry:
    id: zookeeper-registry #注册中心id
    protocol: zookeeper    #注册中心协议
    address: zookeeper://127.0.0.1:2181 #注册中心地址
  metadata-report:
    address: zookeeper://127.0.0.1:2181
  protocol:
    name: dubbo   #协议名称
    port: 20880   #协议端口
    accesslog: dubbo-access.log #协议访问log
  provider:
    retries: 0    #重试次数
    timeout: 3000 #超时时间
  monitor:
    protocol: registry # 注册监控中心
```

#### 3.4 dubbo-consumer项目
1. 此项目就是一个web消费项目，当然也可以不是web项目，在此方便演示就使用了web项目，首先来看依赖文件。依赖于provider项目一样，只多了一个spring-boot-starter-web
>pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.agger</groupId>
        <artifactId>spring-boot-dubbo</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <groupId>com.agger</groupId>
    <artifactId>dubbo-consumer</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>dubbo-consumer</name>
    <packaging>jar</packaging>
    <description>dubbo项目客户端</description>

    <dependencies>
        <!-- dubbo依赖 -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
        </dependency>

        <!-- dubbo的zookeeper依赖 -->
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>${zookeeper.version}</version>
            <exclusions>
                <exclusion>
                    <artifactId>slf4j-log4j12</artifactId>
                    <groupId>org.slf4j</groupId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>com.github.sgroschupf</groupId>
            <artifactId>zkclient</artifactId>
            <version>0.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-recipes</artifactId>
            <version>4.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-framework</artifactId>
            <version>4.2.0</version>
        </dependency>

        <!-- web项目依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```
2. 编写一个controller来调用方法
使用@Reference注解来注入接口

>HelloController.java
```java
package com.agger.dubboconsumer.controller;

import com.agger.dubbocommon.service.HelloService;
import com.agger.dubboconsumer.vo.ResultVO;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @classname: HelloController
 * @description: Hello控制器
 * @author chenhx
 * @date 2020-01-14 13:57:48
 */
@RestController
@RequestMapping("/hello")
public class HelloController {

    @Reference
    HelloService helloService;

    @GetMapping("/morning/{name}")
    public ResultVO morning(@PathVariable("name") String name){
        System.out.println(name);
        String hello = helloService.sayHello(name);
        System.out.println(hello);
        return ResultVO.builder().flag(true).msg("调用成功").data(hello).build();
    }
}
```
当然你可以将调用接口的方法再封装一层service，方便更改service的版本号等等。
 3. 在项目启动类上添加配置
 在项目的启动类DubboConsumerApplication中添加Dubbo启动注解和扫描注解。
 >DubboConsumerApplication.java
```java
package com.agger.dubboconsumer;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubboConfig
@DubboComponentScan("com.agger.dubboconsumer.controller")
@SpringBootApplication
public class DubboConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DubboConsumerApplication.class, args);
    }
}
```
3. 填写配置文件
>application.yml
```yml
server:
  port: 8088

dubbo:
  application:
    name: dubbo-consumer  # 应用名称
    owner: aggerChen      # 应用所属者
    organization: agger   # 应用所属组织
  registry:
    id: zookeeper-registry #注册中心id
    protocol: zookeeper    #注册中心协议
    address: zookeeper://127.0.0.1:2181 #注册中心地址
  monitor:
    protocol: registry # 注册监控中心
```

#### 3.5 测试
到此为止编码部分就完成了，现在开始启动测试。
1. 启动zookeeper 
启动项目之前需要先启动zookeeper客户端，这个可以从官网上去下载[传送门](https://www.apache.org/dyn/closer.cgi/zookeeper/)
选择稳定版本并下载。
![1](https://github.com/AggerChen/imageLibrary/blob/master/spring-boot-dubbo/Image2.png?raw=true)
![1](https://github.com/AggerChen/imageLibrary/blob/master/spring-boot-dubbo/Image3.png?raw=true)
下载完成后，记得将conf文件夹下面的文件zoo_sample.cfg改名为zoo.cfg，然后启动bin/zkServer.cmd就可以了。
2. 启动dubbo-provider
如果你在内网有多台机器或者虚拟机，可以打包dubbo-provider项目在不同的机器上多启动几台。并使用java -jar dubbo-provider命令启动运行。我在IDEA中运行查看控制台信息：已经成功将接口HelloService注册到zookeeper上了
![1](https://github.com/AggerChen/imageLibrary/blob/master/spring-boot-dubbo/Image4.png?raw=true)

3. 启动dubbo-cosumer
启动后可以看到consumer已经从注册中心订阅了自己需要的接口
![1](https://github.com/AggerChen/imageLibrary/blob/master/spring-boot-dubbo/Image5.png?raw=true)
4. 测试访问
在浏览器访问地址：http://localhost:8088/hello/morning/tom
或者可以用IDEA的测试工具HTTP Client非常好用
看到测试结果如下，OK，测试成功！
![1](https://github.com/AggerChen/imageLibrary/blob/master/spring-boot-dubbo/Image6.png?raw=true)

### 四、总结
- 以上的所有代码我已上传到github，需要的可以自取测试，欢迎start，传送门[
https://github.com/AggerChen/spring-boot-dubbo](
https://github.com/AggerChen/spring-boot-dubbo)
- 大家可以访问阿里中间件团队的博客，[传送门](
http://jm.taobao.org/2019/07/16/%E5%BC%80%E6%BA%90%E8%BD%AF%E4%BB%B6-Apache-Dubbo-%E7%89%B5%E6%89%8B-IDE-%E6%8F%92%E4%BB%B6%EF%BC%8C%E5%BC%80%E5%8F%91%E9%83%A8%E7%BD%B2%E6%8F%90%E9%80%9F%E4%B8%8D%E6%AD%A2-8-%E5%80%8D/#more)，下载阿里插件可以快速构建项目并生成需要的common、consumer、provider包。
插件如下：Alibaba Cloud Toolkit
![1](https://github.com/AggerChen/imageLibrary/blob/master/spring-boot-dubbo/Image7.png?raw=true)
构建Dubbo项目：
![1](https://github.com/AggerChen/imageLibrary/blob/master/spring-boot-dubbo/Image8.png?raw=true)
![1](https://github.com/AggerChen/imageLibrary/blob/master/spring-boot-dubbo/Image9.png?raw=true)
一路完成，就会自动构建需要的项目，大家可以测试一下~
- 此篇文章没有讲解Dubbo的太多原理，只是一个快速上手demo，要学习更多Dubbo的其他方面还需要多多啃一啃官网。
- 最后我们可以在dubbo-admin上查看我们的服务情况，需要选取下载项目并构建启动，传送门[
https://github.com/apache/dubbo-admin](
https://github.com/apache/dubbo-admin)
1. 我们需要先打开dubbo-admin\dubbo-admin-server\src\main\resources\application.properties配置文件填写zookeeper地址和登陆用户名和密码。
2. Maven方式安装：
 ```cmd
git clone https://github.com/apache/dubbo-admin.git 
cd dubbo-admin mvn clean package 
cd dubbo-admin-distribution/target java -jar dubbo-admin-0.1.jar
```
3. 如果启动是报8080端口被占用，那么一定是zookeeper捣鬼，因为zookeeper客户端3.5版本Zookeeper AdminServer默认占用8080，所以我们需要在客户端配置文件zoo.cfg上添加更改端口配置：`admin.serverPort=8999`
4. 登陆http://localhost:8080/ 点击服务查询，输入账号密码，默认root root。可以看到我启动的两个dubbo-provider服务，点击消费者可以查看消费者ip等信息。
![1](https://github.com/AggerChen/imageLibrary/blob/master/spring-boot-dubbo/Image10.png?raw=true)

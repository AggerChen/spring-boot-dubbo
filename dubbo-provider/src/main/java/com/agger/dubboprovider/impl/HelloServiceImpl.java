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

    /**
     * @Title: sayHello
     * @Description:
     * @param
     * @return java.lang.String
     * @author chenhx
     * @date 2020-01-14 14:11:55
     */
    @Override
    public String sayHello(String name) {
        System.out.println("dubbo服务调用：" + name);
        return "早上好啊~" + name;
    }
}

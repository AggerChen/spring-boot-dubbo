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

    /**
     * @Title: hello
     * @Description:hello方法
     * @param
     * @return void
     * @author chenhx
     * @date 2020-01-14 13:59:13
     */
    @GetMapping("/morning/{name}")
    public ResultVO morning(@PathVariable("name") String name){

        System.out.println(name);
        String hello = helloService.sayHello(name);
        System.out.println(hello);

        return ResultVO.builder().flag(true).msg("调用成功").data(hello).build();
    }
}

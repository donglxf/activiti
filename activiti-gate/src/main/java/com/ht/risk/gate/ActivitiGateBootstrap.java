package com.ht.risk.gate;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Created by Ace on 2017/6/2.
 */
@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
public class ActivitiGateBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(ActivitiGateBootstrap.class, args);
        System.err.println("ヾ(◍°∇°◍)ﾉﾞ    ActivitiGateBootstrap启动成功      ヾ(◍°∇°◍)ﾉﾞ\n");
    }
}

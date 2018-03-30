package ht.activiti.center.activiticenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer  //启动一个服务注册中心提供给其他应用进行对话
@SpringBootApplication
public class ActivitiCenterApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActivitiCenterApplication.class, args);
		System.err.println("ヾ(◍°∇°◍)ﾉﾞ    ActivitiCenterApplication启动成功      ヾ(◍°∇°◍)ﾉﾞ\n");
	}
}

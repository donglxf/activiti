package com.example.commonactivity;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@EnableFeignClients
@MapperScan("com.example.commonactivity.mapper")
@EnableTransactionManagement
@EnableDiscoveryClient
@SpringBootApplication
public class CommonActivityApplication {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(CommonActivityApplication.class, args);
		System.err.println("ヾ(◍°∇°◍)ﾉﾞ    CommonActivityApplication启动成功      ヾ(◍°∇°◍)ﾉﾞ\n");
	}
}

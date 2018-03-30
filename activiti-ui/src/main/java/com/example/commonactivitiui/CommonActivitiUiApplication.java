package com.example.commonactivitiui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class CommonActivitiUiApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(CommonActivitiUiApplication.class).web(true).run(args);
		System.err.println("ヾ(◍°∇°◍)ﾉﾞ    CommonActivitiUiApplication启动成功      ヾ(◍°∇°◍)ﾉﾞ\n");
	}
}

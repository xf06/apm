package com.blackjade.apm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.blackjade.apm.dao")
public class ApmApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApmApplication.class, args);
	}
}

package com.blackjade.apm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.blackjade.apm.dao")
public class ApmApplication {

	@Bean
	@LoadBalanced
	public RestTemplate rest() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(ApmApplication.class, args);
	}
}

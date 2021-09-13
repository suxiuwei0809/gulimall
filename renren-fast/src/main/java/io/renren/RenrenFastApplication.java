package io.renren;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class RenrenFastApplication {

	public static void main(String[] args) {
		SpringApplication.run(RenrenFastApplication.class, args);
	}

}
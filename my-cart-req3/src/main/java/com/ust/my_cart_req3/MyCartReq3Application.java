package com.ust.my_cart_req3;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MyCartReq3Application {

	public static void main(String[] args) {
		SpringApplication.run(MyCartReq3Application.class, args);
	}

	@Bean(name = "mongoBean")
	public MongoClient mongoClient() {
		return MongoClients.create("mongodb://localhost:27017");
	}
}

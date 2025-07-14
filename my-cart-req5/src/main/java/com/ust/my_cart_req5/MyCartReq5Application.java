package com.ust.my_cart_req5;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MyCartReq5Application {

	public static void main(String[] args) {
		SpringApplication.run(MyCartReq5Application.class, args);
	}

	@Bean(name = "mongoBean")
	public MongoClient mongoClient() {
		return MongoClients.create("mongodb://localhost:27017");
	}
}

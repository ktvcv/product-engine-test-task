package com.productengine.productenginetesttask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@EnableFeignClients
@SpringBootApplication
public class ProductEngineTestTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductEngineTestTaskApplication.class, args);
    }

}

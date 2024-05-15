package com.ulla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

import com.ulla.service.WebSocketServer;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
public class WebSocketServerApplication {

    public static void main(String[] args) {
        // SpringApplication.run(WebSocketServerApplication.class, args);

        // 解决websocketServer无法注入mapper问题
        SpringApplication springApplication = new SpringApplication(WebSocketServerApplication.class);
        ConfigurableApplicationContext configurableApplicationContext = springApplication.run(args);
        WebSocketServer.setApplicationContext(configurableApplicationContext);
    }

}

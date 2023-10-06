package com.ov;

import com.ov.config.netty.WebSocketServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.ov.mapper")
@EnableScheduling
public class OverlapBackendApplication implements CommandLineRunner {

    @Autowired
    private WebSocketServer webSocketServer;

    public static void main(String[] args) {
        SpringApplication.run(OverlapBackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        webSocketServer.run();
    }
}

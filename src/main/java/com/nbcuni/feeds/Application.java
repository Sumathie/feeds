package com.nbcuni.feeds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

import com.nbcuni.feeds.config.AppConfig;


@SpringBootConfiguration
@EnableAutoConfiguration
@Import(AppConfig.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

package com.nbcuni.feeds.config;

import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.nbcuni.feeds.IngestExcecutor;
import com.nbcuni.feeds.IngestScheduler;
import com.nbcuni.feeds.services.McpApiService;
import com.nbcuni.feeds.services.McpService;

@Configuration
@EnableSwagger2

@EnableScheduling
@Import({AwsConfig.class, AwsProperties.class})
public class AppConfig implements AsyncConfigurer {

    @Value("${intake.corePoolSize:2}")
    private int corePoolSize;

    @Value("${intake.maxPoolSize:10}")
    private int maxPoolSize;

    @Value("${intake.keepAliveTime:5000}")
    private int keepAliveTime;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }


    @Bean
    public McpApiService feedService() {
        return new McpApiService();
    }

    @Bean
    public McpService mcpService() {
        return new McpService();
    }

    @Bean
    public IngestExcecutor ingestExcecutor(McpApiService mcpApiService) {
        return new IngestExcecutor(mcpApiService);
    }

    @Bean
    public IngestScheduler ingestScheduler(IngestExcecutor ingestExcecutor) {
        return new IngestScheduler(ingestExcecutor);
    }

    @Override
    public Executor getAsyncExecutor() {
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new SynchronousQueue<>(true));
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}


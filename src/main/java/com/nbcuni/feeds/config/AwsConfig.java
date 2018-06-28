package com.nbcuni.feeds.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan
public class AwsConfig {

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider(AwsProperties awsProperties) {
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsProperties.getAccessKey(), awsProperties.getSecretKey()));
    }


    @Bean
    public ClientConfiguration clientConfiguration(Environment environment) {
        return new ClientConfiguration()
                .withProxyHost(environment.getProperty("https.proxyHost"))
                .withProxyPort(environment.getProperty("https.proxyPort", Integer.class, -1));
    }


    @Bean
    public AWSSecretsManager awsSecretsManager(AWSCredentialsProvider awsCredentialsProvider, AwsProperties awsProperties) {

        AWSSecretsManagerClientBuilder builder = AWSSecretsManagerClientBuilder.standard();
        builder.setCredentials(awsCredentialsProvider);
        builder.setRegion(awsProperties.getStaticRegion());
        AWSSecretsManager secretsManager = builder.build();
        return secretsManager;
    }

}

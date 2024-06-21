package com.bandlab.assignment.config;

import com.bandlab.assignment.service.clients.impl.LocalStorageClient;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import javax.sql.DataSource;

@Configuration
public class AppConfiguration {

    @Bean
    @Profile({"staging", "prod"})
    public S3Client s3Client() {
        return S3Client.builder().region(Region.US_EAST_1).build();
    }
    @Bean
    @Profile({"local", "test"})
    public LocalStorageClient localStorageClient() {
        return new LocalStorageClient();
    }

    @Bean
    @ConfigurationProperties(prefix = "app.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public Hibernate6Module hibernateModule() {
        return new Hibernate6Module();
    }

}

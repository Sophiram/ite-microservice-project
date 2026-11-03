package kh.edu.istad.platform.order;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = "kh.edu.istad.platform.order.persistence")
@EnableConfigurationProperties
public class OrderApplicationService {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplicationService.class, args);
    }
}

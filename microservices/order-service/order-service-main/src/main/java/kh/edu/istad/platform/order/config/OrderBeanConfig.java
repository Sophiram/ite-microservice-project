package kh.edu.istad.platform.order.config;

import kh.edu.istad.platform.order.domain.service.OrderDomainService;
import kh.edu.istad.platform.order.domain.service.OrderDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderBeanConfig {

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }
}

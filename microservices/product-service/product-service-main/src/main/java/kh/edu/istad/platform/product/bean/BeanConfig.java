package kh.edu.istad.platform.product.bean;

import kh.edu.istad.platform.product.domain.service.ProductDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public ProductDomainService productDomainService() {
        return new ProductDomainService();
    }
}

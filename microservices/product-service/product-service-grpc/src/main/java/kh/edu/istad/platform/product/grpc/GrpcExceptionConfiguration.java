package kh.edu.istad.platform.product.grpc;

import io.grpc.ServerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.GlobalServerInterceptor;

@Configuration
public class GrpcExceptionConfiguration {
    @Bean
    @GlobalServerInterceptor
    ServerInterceptor productGrpcExceptionInterceptor(ProductGrpcExceptionHandler handler) {
        return new ProductGrpcExceptionInterceptor(handler);
    }
}

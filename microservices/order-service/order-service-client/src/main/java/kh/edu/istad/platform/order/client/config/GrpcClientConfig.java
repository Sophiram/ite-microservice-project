package kh.edu.istad.platform.order.client.config;

import kh.edu.istad.common.grpc.product.v1.ProductServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {
    @Bean(destroyMethod = "shutdown")
    ManagedChannel productServiceChannel(
            @Value("${service.product.grpc.address:localhost:1013}") String address) {
        return ManagedChannelBuilder.forTarget(address).usePlaintext().build();
    }

    @Bean
    ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub(ManagedChannel channel) {
        return ProductServiceGrpc.newBlockingStub(channel);
    }
}

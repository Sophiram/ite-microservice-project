package kh.edu.istad.platform.product.grpc;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.ServerCall.Listener;
import io.grpc.ForwardingServerCallListener;

public class ProductGrpcExceptionInterceptor implements ServerInterceptor {
    private final ProductGrpcExceptionHandler exceptionHandler;

    public ProductGrpcExceptionInterceptor(ProductGrpcExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                       Metadata headers,
                                                       ServerCallHandler<ReqT, RespT> next) {
        Listener<ReqT> listener = next.startCall(call, headers);
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(listener) {
            @Override
            public void onHalfClose() {
                try {
                    super.onHalfClose();
                } catch (RuntimeException exception) {
                    call.close(exceptionHandler.handleException(exception).getStatus(), new Metadata());
                }
            }
        };
    }
}

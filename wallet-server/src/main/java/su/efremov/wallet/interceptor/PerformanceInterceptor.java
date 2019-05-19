package su.efremov.wallet.interceptor;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

@GrpcGlobalServerInterceptor
@Slf4j
public class PerformanceInterceptor implements ServerInterceptor {

    private final AtomicInteger requestCounter = new AtomicInteger(0);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {

        final ServerCall.Listener<ReqT> listener = serverCallHandler.startCall(serverCall, metadata);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(listener) {
            @Override
            public void onComplete() {
                requestCounter.incrementAndGet();
                super.onComplete();
            }
        };
    }

    @Scheduled(fixedDelay = 1000)
    public void logRequestCount() {
        int count = requestCounter.getAndUpdate((i) -> 0);
        if (count > 0) log.info("Current rps = {}", count);
    }
}

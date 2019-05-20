package su.efremov.wallet.config;

import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import io.grpc.ServerBuilder;

@Component
public class WalletGrpcServerConfig implements GrpcServerConfigurer {

    @Value("${grpc.server.threads.number:2}")
    private int threadNumber;

    @Override
    public void accept(ServerBuilder<?> serverBuilder) {
        serverBuilder.executor(Executors.newFixedThreadPool(threadNumber));
    }
}

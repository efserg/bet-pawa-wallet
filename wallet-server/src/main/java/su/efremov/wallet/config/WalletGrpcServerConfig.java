package su.efremov.wallet.config;

import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;

import io.grpc.ServerBuilder;

@Component
public class WalletGrpcServerConfig implements GrpcServerConfigurer {
    @Override
    public void accept(ServerBuilder<?> serverBuilder) {
        serverBuilder.executor(Executors.newFixedThreadPool(1));
    }
}

package su.efremov.wallet.client;


import org.springframework.stereotype.Service;

import su.efremov.bet.pawa.helloworld.GreeterGrpc;
import su.efremov.bet.pawa.helloworld.HelloReply;
import su.efremov.bet.pawa.helloworld.HelloRequest;
import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
public class WalletClient {

    @GrpcClient("local-grpc-server")
    private GreeterGrpc.GreeterBlockingStub stub;

    public String sendMessage(String name) {
        HelloReply response = stub.sayHello(HelloRequest.newBuilder().setName(name).build());
        return response.getMessage();
    }
}

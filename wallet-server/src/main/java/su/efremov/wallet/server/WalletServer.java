package su.efremov.wallet.server;

import net.devh.boot.grpc.server.service.GrpcService;

import io.grpc.stub.StreamObserver;
import su.efremov.bet.pawa.helloworld.GreeterGrpc;
import su.efremov.bet.pawa.helloworld.HelloReply;
import su.efremov.bet.pawa.helloworld.HelloRequest;

@GrpcService
public class WalletServer extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello =============> " + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}

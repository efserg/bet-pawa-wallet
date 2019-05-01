package su.efremov.wallet.grpc.service;

import net.devh.boot.grpc.server.service.GrpcService;
import com.google.protobuf.Empty;

import io.grpc.stub.StreamObserver;
import su.efremov.bet.pawa.withdraw.WithdrawGrpc;
import su.efremov.bet.pawa.withdraw.WithdrawRequest;

@GrpcService
public class WithdrawGrpcService extends WithdrawGrpc.WithdrawImplBase {

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Empty> responseObserver) {
        Empty reply = Empty.newBuilder().build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

}

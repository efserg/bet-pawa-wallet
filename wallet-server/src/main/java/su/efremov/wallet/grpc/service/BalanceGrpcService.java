package su.efremov.wallet.grpc.service;

import net.devh.boot.grpc.server.service.GrpcService;

import io.grpc.stub.StreamObserver;
import su.efremov.bet.pawa.balance.BalanceGrpc;
import su.efremov.bet.pawa.balance.BalanceRequest;
import su.efremov.bet.pawa.balance.BalanceResponse;

@GrpcService
public class BalanceGrpcService extends BalanceGrpc.BalanceImplBase {

    @Override
    public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        BalanceResponse balance = BalanceResponse.newBuilder().build();
        responseObserver.onNext(balance);
        responseObserver.onCompleted();
    }

}

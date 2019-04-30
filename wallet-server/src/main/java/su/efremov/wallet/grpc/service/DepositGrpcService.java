package su.efremov.wallet.grpc.service;

import net.devh.boot.grpc.server.service.GrpcService;
import com.google.protobuf.Empty;

import io.grpc.stub.StreamObserver;
import su.efremov.bet.pawa.deposit.AddFundsRequest;
import su.efremov.bet.pawa.deposit.DepositGrpc;

@GrpcService
public class DepositGrpcService extends DepositGrpc.DepositImplBase {

    @Override
    public void addFunds(AddFundsRequest request, StreamObserver<Empty> responseObserver) {
        Empty reply = Empty.newBuilder().build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}

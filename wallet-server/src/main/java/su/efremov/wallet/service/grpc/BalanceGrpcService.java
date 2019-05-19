package su.efremov.wallet.service.grpc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import su.efremov.bet.pawa.balance.BalanceGrpc;
import su.efremov.bet.pawa.balance.BalanceRequest;
import su.efremov.bet.pawa.balance.BalanceResponse;
import su.efremov.wallet.service.WalletService;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class BalanceGrpcService extends BalanceGrpc.BalanceImplBase {

    private final WalletService walletService;

    @Override
    public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {

        try {
            responseObserver.onNext(walletService.receiveBalance(request.getUserId()));
            responseObserver.onCompleted();
        } catch (Exception ex) {
            log.error("Error during get wallet balance", ex);
            responseObserver.onError(Status.INTERNAL
                .withDescription(ex.getMessage())
                .augmentDescription("Unknown error")
                .withCause(ex)
                .asRuntimeException());
        }
    }



}

package su.efremov.wallet.service.grpc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import su.efremov.bet.pawa.withdraw.WithdrawGrpc;
import su.efremov.bet.pawa.withdraw.WithdrawRequest;
import su.efremov.wallet.exception.InsufficientFundsException;
import su.efremov.wallet.exception.UnknownCurrencyException;
import su.efremov.wallet.service.WalletService;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class WithdrawGrpcService extends WithdrawGrpc.WithdrawImplBase {

    private final WalletService walletService;

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Empty> responseObserver) {
        try {
            walletService.withdraw(request.getAmount(), request.getUserId(), request.getCurrency());
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (UnknownCurrencyException ex) {
            responseObserver.onError(Status.FAILED_PRECONDITION
                .augmentDescription("Unknown currency")
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException());
        } catch (InsufficientFundsException ex) {
            responseObserver.onError(Status.UNAVAILABLE
                .augmentDescription("Insufficient funds")
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException());
        } catch (Exception ex) {
            log.error("Error during wallet withdraw", ex);
            responseObserver.onError(Status.INTERNAL
                .withDescription(ex.getMessage())
                .augmentDescription("Unknown error")
                .withCause(ex)
                .asRuntimeException());
        }
    }

}

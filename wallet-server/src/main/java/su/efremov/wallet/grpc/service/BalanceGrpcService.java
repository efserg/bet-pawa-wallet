package su.efremov.wallet.grpc.service;

import static java.util.stream.Collectors.toSet;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import su.efremov.bet.pawa.balance.BalanceGrpc;
import su.efremov.bet.pawa.balance.BalanceRequest;
import su.efremov.bet.pawa.balance.BalanceResponse;
import su.efremov.bet.pawa.balance.CurrencyBalance;
import su.efremov.bet.pawa.deposit.Currency;
import su.efremov.wallet.repository.BalanceRepository;

@GrpcService
@AllArgsConstructor
@Slf4j
public class BalanceGrpcService extends BalanceGrpc.BalanceImplBase {

    private final BalanceRepository balanceRepository;

    @Override
    public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {

        try {
            Iterable<CurrencyBalance> balances = balanceRepository.findByIdUserId(request.getUserId()).stream()
                .map(balance -> CurrencyBalance.newBuilder()
                    .setCurrency(Currency.valueOf(balance.getId().getCurrency().name()))
                    .setAmount(balance.getAmount().toString())
                    .build())
                .collect(toSet());

            BalanceResponse balance = BalanceResponse.newBuilder()
                .addAllBalance(balances)
                .build();

            responseObserver.onNext(balance);
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

package su.efremov.wallet.grpc.service;

import static java.util.stream.Collectors.toSet;

import lombok.AllArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import io.grpc.stub.StreamObserver;
import su.efremov.bet.pawa.balance.BalanceGrpc;
import su.efremov.bet.pawa.balance.BalanceRequest;
import su.efremov.bet.pawa.balance.BalanceResponse;
import su.efremov.bet.pawa.balance.CurrencyBalance;
import su.efremov.bet.pawa.deposit.Currency;
import su.efremov.wallet.repository.BalanceRepository;

@GrpcService
@AllArgsConstructor
public class BalanceGrpcService extends BalanceGrpc.BalanceImplBase {

    private final BalanceRepository balanceRepository;

    @Override
    public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        Iterable<CurrencyBalance> balances = balanceRepository.findByUserId(request.getUserId()).stream()
            .map(balance -> CurrencyBalance.newBuilder()
                .setCurrency(Currency.valueOf(balance.getCurrency().name()))
                .setAmount(balance.toString())
                .build())
            .collect(toSet());

        BalanceResponse balance = BalanceResponse.newBuilder()
            .addAllBalance(balances)
            .build();

        responseObserver.onNext(balance);
        responseObserver.onCompleted();
    }

}

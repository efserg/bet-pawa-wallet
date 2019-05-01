package su.efremov.wallet.service;

import org.springframework.stereotype.Service;
import net.devh.boot.grpc.client.inject.GrpcClient;

import su.efremov.bet.pawa.balance.BalanceGrpc;
import su.efremov.bet.pawa.balance.BalanceRequest;
import su.efremov.bet.pawa.balance.BalanceResponse;

@Service
public class BalanceClientService {

    @GrpcClient("local-grpc-server")
    private BalanceGrpc.BalanceBlockingStub stub;

    public BalanceResponse balance(BalanceRequest request) {
        return stub.balance(request);
    }
}

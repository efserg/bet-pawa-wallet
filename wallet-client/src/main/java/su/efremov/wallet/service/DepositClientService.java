package su.efremov.wallet.service;

import org.springframework.stereotype.Service;
import net.devh.boot.grpc.client.inject.GrpcClient;

import su.efremov.bet.pawa.deposit.AddFundsRequest;
import su.efremov.bet.pawa.deposit.DepositGrpc;

@Service
public class DepositClientService {

    @GrpcClient("local-grpc-server")
    private DepositGrpc.DepositBlockingStub stub;

    public void addFunds(AddFundsRequest request) {
        stub.addFunds(request);
    }
}

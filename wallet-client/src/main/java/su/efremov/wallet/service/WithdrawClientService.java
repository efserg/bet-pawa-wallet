package su.efremov.wallet.service;

import org.springframework.stereotype.Service;
import net.devh.boot.grpc.client.inject.GrpcClient;

import su.efremov.bet.pawa.deposit.AddFundsRequest;
import su.efremov.bet.pawa.deposit.DepositGrpc;
import su.efremov.bet.pawa.withdraw.WithdrawGrpc;
import su.efremov.bet.pawa.withdraw.WithdrawRequest;

@Service
public class WithdrawClientService {

    @GrpcClient("local-grpc-server")
    private WithdrawGrpc.WithdrawBlockingStub stub;

    public void withdraw(WithdrawRequest request) {
        stub.withdraw(request);
    }
}

package su.efremov.wallet.grpc.service;

import static java.time.ZoneOffset.UTC;
import static su.efremov.wallet.domain.CurrencyEnum.fromGrpc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import net.devh.boot.grpc.server.service.GrpcService;
import com.google.protobuf.Empty;

import io.grpc.stub.StreamObserver;
import su.efremov.bet.pawa.withdraw.WithdrawGrpc;
import su.efremov.bet.pawa.withdraw.WithdrawRequest;
import su.efremov.wallet.domain.Balance;
import su.efremov.wallet.domain.CurrencyEnum;
import su.efremov.wallet.domain.Transaction;
import su.efremov.wallet.domain.User;
import su.efremov.wallet.exception.InsufficientFundsException;
import su.efremov.wallet.repository.BalanceRepository;
import su.efremov.wallet.repository.TransactionRepository;
import su.efremov.wallet.repository.UserRepository;

@GrpcService
@AllArgsConstructor
public class WithdrawGrpcService extends WithdrawGrpc.WithdrawImplBase {

    private final UserRepository userRepository;

    private final TransactionRepository transactionRepository;

    private final BalanceRepository balanceRepository;

    @Override
    @Transactional
    public void withdraw(WithdrawRequest request, StreamObserver<Empty> responseObserver) {
        final BigDecimal amount = new BigDecimal(request.getAmount()).abs().negate();
        final Long userId = request.getUserId();
        final LocalDateTime now = LocalDateTime.now(UTC);
        final CurrencyEnum currency = fromGrpc(request.getCurrency());

        final User user = userRepository.findById(userId)
            .orElseThrow(InsufficientFundsException::new);

        final BigDecimal currentAmount = balanceRepository.findByUserIdAndCurrency(userId, currency)
            .map(Balance::getAmount)
            .orElse(BigDecimal.ZERO);

        if (currentAmount.compareTo(amount) > 0) {
            throw new InsufficientFundsException();
        }

        transactionRepository.save(Transaction.builder()
            .date(now)
            .user(user)
            .amount(amount)
            .currency(currency)
            .build());
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

}

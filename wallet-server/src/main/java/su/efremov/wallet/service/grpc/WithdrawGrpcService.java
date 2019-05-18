package su.efremov.wallet.service.grpc;

import static java.time.ZoneOffset.UTC;
import static su.efremov.wallet.domain.CurrencyEnum.fromGrpc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import net.devh.boot.grpc.server.service.GrpcService;
import com.google.protobuf.Empty;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import su.efremov.bet.pawa.withdraw.WithdrawGrpc;
import su.efremov.bet.pawa.withdraw.WithdrawRequest;
import su.efremov.wallet.domain.Balance;
import su.efremov.wallet.domain.BalanceId;
import su.efremov.wallet.domain.CurrencyEnum;
import su.efremov.wallet.domain.Transaction;
import su.efremov.wallet.domain.User;
import su.efremov.wallet.exception.InsufficientFundsException;
import su.efremov.wallet.exception.UnknownCurrencyException;
import su.efremov.wallet.repository.BalanceRepository;
import su.efremov.wallet.repository.TransactionRepository;
import su.efremov.wallet.repository.UserRepository;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class WithdrawGrpcService extends WithdrawGrpc.WithdrawImplBase {

    private static final String INSUFFICIENT_FUNDS_ERROR_MSG = "User (id = %d) has attempted to withdraw %s%s, but has only %s%s on the account";

    private static final String USER_NOT_FOUND_ERROR_MSG = "User id = %d not found";

    private final UserRepository userRepository;

    private final TransactionRepository transactionRepository;

    private final BalanceRepository balanceRepository;

    @Override
    @Transactional
    public void withdraw(WithdrawRequest request, StreamObserver<Empty> responseObserver) {
        try {
            final BigDecimal amount = new BigDecimal(request.getAmount()).abs();
            final Long userId = request.getUserId();
            final LocalDateTime now = LocalDateTime.now(UTC);
            final CurrencyEnum currency = fromGrpc(request.getCurrency());

            final User user = userRepository.findById(userId)
                .orElseThrow(() -> new InsufficientFundsException(String.format(USER_NOT_FOUND_ERROR_MSG, userId)));

            Balance balance = balanceRepository.findByIdUserIdAndIdCurrency(userId, currency)
                .orElse(Balance.builder()
                    .amount(BigDecimal.ZERO)
                    .id(BalanceId.builder()
                        .currency(currency)
                        .userId(userId)
                        .build())
                    .build());

            if (balance.getAmount().compareTo(amount) < 0) {
                String msg = String.format(INSUFFICIENT_FUNDS_ERROR_MSG, userId, amount.toString(), currency, balance.getAmount().toString(), currency);
                throw new InsufficientFundsException(msg);
            }

            balance.setAmount(balance.getAmount().subtract(amount));

            transactionRepository.saveAndFlush(Transaction.builder()
                .date(now)
                .user(user)
                .amount(amount.negate())
                .currency(currency)
                .build());

            balanceRepository.saveAndFlush(balance);

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

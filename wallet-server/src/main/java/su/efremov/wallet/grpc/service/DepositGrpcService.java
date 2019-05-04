package su.efremov.wallet.grpc.service;

import static java.time.ZoneOffset.UTC;
import static su.efremov.wallet.domain.CurrencyEnum.fromGrpc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.transaction.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import com.google.protobuf.Empty;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import su.efremov.bet.pawa.deposit.AddFundsRequest;
import su.efremov.bet.pawa.deposit.DepositGrpc;
import su.efremov.wallet.domain.CurrencyEnum;
import su.efremov.wallet.domain.Transaction;
import su.efremov.wallet.domain.User;
import su.efremov.wallet.exception.UnknownCurrencyException;
import su.efremov.wallet.repository.TransactionRepository;
import su.efremov.wallet.repository.UserRepository;

@GrpcService
@AllArgsConstructor
@Slf4j
public class DepositGrpcService extends DepositGrpc.DepositImplBase {

    private final UserRepository userRepository;

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public void addFunds(AddFundsRequest request, StreamObserver<Empty> responseObserver) {
        try {
            final BigDecimal amount = new BigDecimal(request.getAmount()).abs();
            final Long userId = request.getUserId();
            final CurrencyEnum currency = fromGrpc(request.getCurrency());
            final LocalDateTime now = LocalDateTime.now(UTC);
            synchronized (userId) {
                final User user = userRepository.findById(userId)
                    .orElseGet(() -> userRepository.save(User.builder().id(userId).build()));
                transactionRepository.save(Transaction.builder()
                    .date(now)
                    .user(user)
                    .amount(amount)
                    .currency(currency)
                    .build());
            }
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (UnknownCurrencyException ex) {
            responseObserver.onError(Status.FAILED_PRECONDITION
                .augmentDescription("Unknown currency")
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException());
        } catch (Exception ex) {
            log.error("Error during wallet add funds", ex);
            responseObserver.onError(Status.INTERNAL
                .withDescription(ex.getMessage())
                .augmentDescription("Unknown error")
                .withCause(ex)
                .asRuntimeException());
        }
    }
}

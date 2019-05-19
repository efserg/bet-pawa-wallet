package su.efremov.wallet.service;

import static java.time.ZoneOffset.UTC;
import static java.util.stream.Collectors.toSet;
import static su.efremov.wallet.domain.CurrencyEnum.fromGrpc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.efremov.bet.pawa.balance.BalanceResponse;
import su.efremov.bet.pawa.balance.CurrencyBalance;
import su.efremov.bet.pawa.deposit.Currency;
import su.efremov.wallet.domain.Balance;
import su.efremov.wallet.domain.BalanceId;
import su.efremov.wallet.domain.CurrencyEnum;
import su.efremov.wallet.domain.Transaction;
import su.efremov.wallet.domain.User;
import su.efremov.wallet.exception.InsufficientFundsException;
import su.efremov.wallet.repository.BalanceRepository;
import su.efremov.wallet.repository.TransactionRepository;
import su.efremov.wallet.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private static final String INSUFFICIENT_FUNDS_ERROR_MSG = "User (id = %d) has attempted to withdraw %s%s, but has only %s%s on the account";

    private static final String USER_NOT_FOUND_ERROR_MSG = "User id = %d not found";

    private final UserRepository userRepository;

    private final BalanceRepository balanceRepository;

    private final TransactionRepository transactionRepository;

    @Transactional
    public void addFunds(final String _amount, final long userId, final Currency _currency) {
        final BigDecimal amount = convertAmount(_amount);
        final CurrencyEnum currency = fromGrpc(_currency);
        final User user = userRepository.findById(userId)
            .orElseGet(() -> userRepository.save(User.builder().id(userId).build()));

        saveTransaction(currency, user, amount);
        Balance balance = getBalance(userId, currency);
        balance.setAmount(balance.getAmount().add(amount));
        balanceRepository.saveAndFlush(balance);
    }

    @Transactional(readOnly = true)
    public BalanceResponse receiveBalance(final long userId) {
        Iterable<CurrencyBalance> balances = balanceRepository.findByIdUserId(userId).stream()
            .map(balance -> CurrencyBalance.newBuilder()
                .setCurrency(Currency.valueOf(balance.getId().getCurrency().name()))
                .setAmount(balance.getAmount().toString())
                .build())
            .collect(toSet());

        return BalanceResponse.newBuilder()
            .addAllBalance(balances)
            .build();
    }

    @Transactional
    public void withdraw(final String _amount, final long userId, final Currency _currency) {
        final BigDecimal amount = convertAmount(_amount);
        final CurrencyEnum currency = fromGrpc(_currency);

        final User user = userRepository.findById(userId)
            .orElseThrow(() -> new InsufficientFundsException(String.format(USER_NOT_FOUND_ERROR_MSG, userId)));

        Balance balance = getBalance(userId, currency);
        checkBalance(amount, userId, currency, balance);
        balance.setAmount(balance.getAmount().subtract(amount));
        saveTransaction(currency, user, amount.negate());
        balanceRepository.saveAndFlush(balance);
    }

    private void saveTransaction(CurrencyEnum currency, User user, BigDecimal negate) {
        final LocalDateTime now = LocalDateTime.now(UTC);
        transactionRepository.saveAndFlush(Transaction.builder()
            .date(now)
            .user(user)
            .amount(negate)
            .currency(currency)
            .build());
    }

    private void checkBalance(BigDecimal amount, Long userId, CurrencyEnum currency, Balance balance) {
        if (balance.getAmount().compareTo(amount) < 0) {
            String msg = String.format(INSUFFICIENT_FUNDS_ERROR_MSG, userId, amount.toString(), currency, balance.getAmount().toString(), currency);
            throw new InsufficientFundsException(msg);
        }
    }

    private Balance getBalance(Long userId, CurrencyEnum currency) {
        return balanceRepository.findByIdUserIdAndIdCurrency(userId, currency)
            .orElse(Balance.builder()
                .amount(BigDecimal.ZERO)
                .id(BalanceId.builder()
                    .currency(currency)
                    .userId(userId)
                    .build())
                .build());
    }

    private BigDecimal convertAmount(String amount) {
        return new BigDecimal(amount).abs();
    }

}

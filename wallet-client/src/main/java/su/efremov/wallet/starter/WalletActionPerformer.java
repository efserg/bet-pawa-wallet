package su.efremov.wallet.starter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.efremov.bet.pawa.balance.BalanceRequest;
import su.efremov.bet.pawa.balance.BalanceResponse;
import su.efremov.bet.pawa.balance.CurrencyBalance;
import su.efremov.bet.pawa.deposit.AddFundsRequest;
import su.efremov.bet.pawa.deposit.Currency;
import su.efremov.bet.pawa.withdraw.WithdrawRequest;
import su.efremov.wallet.service.BalanceClientService;
import su.efremov.wallet.service.DepositClientService;
import su.efremov.wallet.service.WithdrawClientService;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletActionPerformer {

    private final BalanceClientService balanceService;

    private final DepositClientService depositService;

    private final WithdrawClientService withdrawService;

    public void perform(final WalletAction action, final Long userId) {

        try {
            log.info("{} {} {}", action.getActionType(), action.getAmount(), action.getCurrency());
            switch (action.getActionType()) {
                case BALANCE:
                    boolean isCorrectBalance = checkBalances(action.getCurrentBalances(), userId);
                    if (!isCorrectBalance) {
                        log.error("-- Inappropriate balance!");
                    }
                    break;
                case DEPOSIT:
                    addFunds(action, userId);
                    break;
                case WITHDRAW:
                    withdraw(action, userId);
                    break;
            }
        } catch (Exception e) {
            if (action.isMustError()) {
                log.warn("insufficient funds");
            } else {
                log.error("unknown error");
            }
        }

    }

    private void withdraw(final WalletAction walletAction, final Long userId) {
        withdrawService.withdraw(WithdrawRequest.newBuilder()
            .setUserId(userId)
            .setAmount(walletAction.getAmount().toString())
            .setCurrency(walletAction.getCurrency())
            .build());
    }

    private void addFunds(final WalletAction walletAction, final Long userId) {
        depositService.addFunds(AddFundsRequest.newBuilder()
            .setUserId(userId)
            .setAmount(walletAction.getAmount().toString())
            .setCurrency(walletAction.getCurrency())
            .build());
    }

    private boolean checkBalances(final Map<Currency, BigDecimal> currentBalances, final Long userId) {
        BalanceResponse balance = balanceService.balance(BalanceRequest.newBuilder().setUserId(userId).build());
        final Map<Currency, BigDecimal> balancesFromDB = balance.getBalanceList().stream()
            .collect(Collectors.toMap(CurrencyBalance::getCurrency, c -> new BigDecimal(c.getAmount())));
        log.info("balancesFromDB {}", balancesFromDB);
        log.info("currentBalances {}", currentBalances);

        return currentBalances.entrySet().stream()
            .allMatch(currencyBigDecimalEntry -> {
                Currency currency = currencyBigDecimalEntry.getKey();
                BigDecimal amount = currencyBigDecimalEntry.getValue().setScale(2, RoundingMode.HALF_UP);
                return balancesFromDB.getOrDefault(currency, BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP)
                    .equals(amount);
            });

    }
}

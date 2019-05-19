package su.efremov.wallet.test;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import su.efremov.bet.pawa.deposit.Currency;

@Data
@Builder
public class WalletAction {
    enum ActionType {WITHDRAW, DEPOSIT, BALANCE}

    private ActionType actionType;
    private BigDecimal amount;
    private Currency currency;
    private Map<Currency, BigDecimal> currentBalances;
    private boolean mustError;
}

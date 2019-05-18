package su.efremov.wallet.starter;

import static su.efremov.wallet.starter.WalletAction.ActionType.BALANCE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.google.common.collect.ImmutableMap;
import su.efremov.bet.pawa.deposit.Currency;

@Service
public class TestCaseParser {

    private static final Map<Currency, BigDecimal> INITIAL_BALANCES = Stream.of(Currency.values())
        .collect(Collectors.toMap(Function.identity(), currency -> BigDecimal.ZERO));

    public List<WalletAction> parse(InputStream inputStream) throws IOException {

        final List<WalletAction> walletActions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while (!StringUtils.isEmpty(line = br.readLine())) {
                if (line.startsWith("//")) continue;
                final String[] strings = line.split("\\s");
                final WalletAction.ActionType actionType = WalletAction.ActionType.valueOf(strings[0].toUpperCase());
                if (BALANCE == actionType) {
                    walletActions.add(WalletAction.builder()
                        .actionType(BALANCE)
                        .currentBalances(ImmutableMap.copyOf(INITIAL_BALANCES))
                        .build());
                    continue;
                }

                final BigDecimal amount = new BigDecimal(strings[1]).setScale(2, RoundingMode.HALF_UP);
                final Currency currency = Currency.valueOf(strings[2].toUpperCase());
                final BigDecimal currentBalance = INITIAL_BALANCES.get(currency);
                final boolean isWithdraw = WalletAction.ActionType.WITHDRAW.equals(actionType);
                final boolean mustError = isWithdraw && currentBalance.compareTo(amount) < 0;
                if (!mustError) {
                    if (isWithdraw) {
                        INITIAL_BALANCES.put(currency, currentBalance.subtract(amount));
                    } else {
                        INITIAL_BALANCES.put(currency, currentBalance.add(amount));
                    }
                }
                walletActions.add(WalletAction.builder()
                    .actionType(actionType)
                    .amount(amount)
                    .currentBalances(ImmutableMap.copyOf(INITIAL_BALANCES))
                    .currency(currency)
                    .mustError(mustError)
                    .build());
            }
        }
        return walletActions;
    }

}

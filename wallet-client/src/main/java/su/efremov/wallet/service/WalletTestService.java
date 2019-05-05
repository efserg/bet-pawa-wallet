package su.efremov.wallet.service;

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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.google.common.collect.ImmutableMap;

import su.efremov.bet.pawa.balance.BalanceRequest;
import su.efremov.bet.pawa.balance.BalanceResponse;
import su.efremov.bet.pawa.balance.CurrencyBalance;
import su.efremov.bet.pawa.deposit.AddFundsRequest;
import su.efremov.bet.pawa.deposit.Currency;
import su.efremov.bet.pawa.withdraw.WithdrawRequest;

@Service
@AllArgsConstructor
@Slf4j
public class WalletTestService {

    private final WalletTestConfigService config;

    private final BalanceClientService balanceService;

    private final DepositClientService depositService;

    private final WithdrawClientService withdrawService;

    private final ResourceLoader resourceLoader;

    private static final String TEST_CASE_RESOURCE = "classpath:/test/cases/test-case.txt";

    @Data
    @Builder
    private static class Command {
        private enum CommandType {WITHDRAW, DEPOSIT}

        private CommandType commandType;
        private BigDecimal amount;
        private Currency currency;
        private Map<Currency, BigDecimal> currentBalances;
        private boolean error;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void test() throws IOException {
        log.info("Test started {}", config);

        final InputStream inputStream = resourceLoader.getResource(TEST_CASE_RESOURCE).getInputStream();

        final Map<Currency, BigDecimal> currentBalances = Stream.of(Currency.values())
            .collect(Collectors.toMap(Function.identity(), currency -> BigDecimal.ZERO));

        final List<Command> commands = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while (!StringUtils.isEmpty(line = br.readLine())) {
                if (line.startsWith("//")) continue;
                final String[] strings = line.split("\\s");
                final Command.CommandType commandType = Command.CommandType.valueOf(strings[0].toUpperCase());
                final BigDecimal amount = new BigDecimal(strings[1]).setScale(2, RoundingMode.HALF_UP);
                final Currency currency = Currency.valueOf(strings[2].toUpperCase());
                final BigDecimal currentBalance = currentBalances.get(currency);
                final boolean isWithdraw = Command.CommandType.WITHDRAW.equals(commandType);
                final boolean isError = isWithdraw && currentBalance.compareTo(amount) < 0;
                if (!isError) {
                    if (isWithdraw) {
                        currentBalances.put(currency, currentBalance.subtract(amount));
                    } else {
                        currentBalances.put(currency, currentBalance.add(amount));
                    }
                }
                commands.add(Command.builder()
                    .commandType(commandType)
                    .amount(amount)
                    .currentBalances(ImmutableMap.copyOf(currentBalances))
                    .currency(currency)
                    .error(isError)
                    .build());
            }
        }

        commands.forEach(command -> {
            try {
                log.info("{} {} {}", command.commandType, command.amount, command.currency);
                if (Command.CommandType.DEPOSIT.equals(command.commandType)) {
                    depositService.addFunds(AddFundsRequest.newBuilder()
                        .setUserId(1)
                        .setAmount(command.amount.toString())
                        .setCurrency(command.currency)
                        .build());
                } else {
                    withdrawService.withdraw(WithdrawRequest.newBuilder()
                        .setUserId(1)
                        .setAmount(command.amount.toString())
                        .setCurrency(command.currency)
                        .build());
                }
            } catch (Exception e) {
                if (command.isError()) {
                    log.warn("insufficient funds");
                } else {
                    log.error("unknown error");
                }
            }

            Boolean balanceIsProper = checkBalance(command.currentBalances);

            if (!balanceIsProper) {
                log.error("-- Inappropriate balance!");
            }

        });

    }

    private Boolean checkBalance(final Map<Currency, BigDecimal> currentBalances) {
        try {
            Thread.currentThread().sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BalanceResponse balance = balanceService.balance(BalanceRequest.newBuilder().setUserId(1).build());
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

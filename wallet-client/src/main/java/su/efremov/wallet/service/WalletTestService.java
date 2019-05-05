package su.efremov.wallet.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class WalletTestService {

    private final WalletTestConfigService config;

    private final BalanceClientService balanceService;

    private final DepositClientService depositService;

    private final WithdrawClientService withdrawService;

    @EventListener(ApplicationReadyEvent.class)
    public void test() {
        log.info("Test started {}", config);
    }

}

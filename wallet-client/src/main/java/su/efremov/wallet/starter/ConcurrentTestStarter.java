package su.efremov.wallet.starter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import su.efremov.wallet.service.BalanceClientService;
import su.efremov.wallet.service.DepositClientService;
import su.efremov.wallet.service.WithdrawClientService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConcurrentTestStarter {

    private static final String CLASSPATH_TEST_ROUND_ROUND = "classpath:/test/round/round*.*";

    private final WalletTestConfig config;

    private final BalanceClientService balanceService;

    private final DepositClientService depositService;

    private final WithdrawClientService withdrawService;

    private final TestCaseParser parser;

    private final ResourcePatternResolver roundFiles;

    @EventListener(ApplicationReadyEvent.class)
    public void test() throws IOException {

        if (!config.isStartConcurrentTest()) {
            log.warn("Concurrent test passed");
            return;
        }

        log.info("Concurrent test started");

        List<List<WalletAction>> rounds = Stream.of(roundFiles.getResources(CLASSPATH_TEST_ROUND_ROUND))
            .map(resource -> {
                try {
                    return parser.parse(resource.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toList());


    }
}

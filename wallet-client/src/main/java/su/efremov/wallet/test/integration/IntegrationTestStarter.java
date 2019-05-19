package su.efremov.wallet.test.integration;

import java.io.IOException;
import java.util.Random;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import su.efremov.wallet.test.TestCaseParser;
import su.efremov.wallet.test.WalletActionPerformer;
import su.efremov.wallet.test.WalletTestConfig;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationTestStarter {

    private final WalletTestConfig config;

    private final TestCaseParser parser;

    private final WalletActionPerformer actionPerformer;

    @Value("classpath:/test/cases/test-case.txt")
    private Resource resourceFile;

    @EventListener(ApplicationReadyEvent.class)
    public void test() throws IOException {

        if (!config.isStartIntegrationTest()) {
            log.warn("Integration test passed");
            return;
        }

        final long userId = new Random().nextInt();

        log.info("Integration test started with userId {}", userId);

        parser.parse(resourceFile.getInputStream())
            .getActions()
            .forEach(walletAction -> actionPerformer.perform(walletAction, userId));

        log.info("Integration test done");

        System.exit(0);

    }


}

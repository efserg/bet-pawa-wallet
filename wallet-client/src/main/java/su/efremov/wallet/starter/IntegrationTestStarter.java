package su.efremov.wallet.starter;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

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

        log.info("Integration test started");

        parser.parse(resourceFile.getInputStream())
            .forEach(walletAction -> actionPerformer.perform(walletAction, config.getUserId()));

    }


}

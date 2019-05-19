package su.efremov.wallet.test.concurrent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import su.efremov.wallet.test.Round;
import su.efremov.wallet.test.TestCaseParser;
import su.efremov.wallet.test.WalletActionPerformer;
import su.efremov.wallet.test.WalletTestConfig;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConcurrentTestStarter {

    private static final String CLASSPATH_TEST_ROUND_ROUND = "classpath:/test/round/round*.*";

    private final WalletTestConfig config;

    private final TestCaseParser parser;

    private final WalletActionPerformer walletActionPerformer;

    private final ResourcePatternResolver roundFiles;

    @EventListener(ApplicationReadyEvent.class)
    public void test() throws IOException, InterruptedException {

        if (!config.isStartConcurrentTest()) {
            log.warn("Concurrent test passed");
            return;
        }

        log.info("Concurrent test started");

        final List<Round> rounds = Stream.of(roundFiles.getResources(CLASSPATH_TEST_ROUND_ROUND))
            .map(resource -> {
                try {
                    return parser.parse(resource.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toList());

        final int threadCount = config.getUsers() * config.getThreads();

        final List<WalletClient> walletClients = new ArrayList<>(threadCount);

        for (Integer i = 0; i < config.getUsers(); i++) {
            long userId = new Random().nextInt();
            for (Integer thread = 0; thread < config.getThreads(); thread++) {
                walletClients.add(WalletClient.builder().actionPerformer(walletActionPerformer)
                    .rounds(rounds)
                    .userId(userId)
                    .roundCount(config.getRounds())
                    .build());
            }
        }

        final ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        executor.invokeAll(walletClients);

        executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);

        log.info("Concurrent test done");

        System.exit(0);
    }
}

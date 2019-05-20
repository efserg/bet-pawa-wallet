package su.efremov.wallet.test;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Getter
@ToString
public class WalletTestConfig {

    @Value("${users}")
    private Integer users;

    @Value("${concurrent_threads_per_user}")
    private Integer threads;

    @Value("${rounds_per_thread}")
    private Integer rounds;

    @Value("${start_integration_test}")
    private boolean startIntegrationTest;

    @Value("${start_performance_test}")
    private boolean startConcurrentTest;

}

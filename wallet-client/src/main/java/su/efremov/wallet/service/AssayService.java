package su.efremov.wallet.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AssayService {

    @Value("${users}")
    private Integer users;

    @Value("${concurrent_threads_per_user}")
    private Integer threads;

    @Value("${rounds_per_thread}")
    private Integer rounds;

    @EventListener(ApplicationReadyEvent.class)
    public void makeAssay() {
        log.info("Test are starting with params\n\tusers = {},\n\tconcurrent_threads_per_user = {},\n\trounds_per_thread = {}", users, threads, rounds);
    }
}

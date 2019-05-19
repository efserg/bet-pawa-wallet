package su.efremov.wallet.test.concurrent;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import su.efremov.wallet.test.Round;
import su.efremov.wallet.test.WalletActionPerformer;

@Slf4j
@Builder
@AllArgsConstructor
public class WalletClient implements Callable<Void> {

    private final WalletActionPerformer actionPerformer;

    private final long userId;

    private final List<Round> rounds;

    private final int roundCount;

    @Override
    public Void call() throws Exception {

        new Random().ints(roundCount, 0, rounds.size())
            .mapToObj(rounds::get)
            .forEach(round -> round.getActions().forEach(walletAction -> actionPerformer.perform(walletAction, userId)));

        return null;
    }
}

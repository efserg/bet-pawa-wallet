package su.efremov.wallet.test;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Round {
    private final List<WalletAction> actions;
}

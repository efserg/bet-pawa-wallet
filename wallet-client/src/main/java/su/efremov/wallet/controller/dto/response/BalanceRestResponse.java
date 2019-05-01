package su.efremov.wallet.controller.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

import su.efremov.wallet.controller.dto.CurrencyEnum;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BalanceRestResponse {

    @Singular
    private List<CurrencyBalance> balances;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class CurrencyBalance {

        private CurrencyEnum currency;

        private BigDecimal amount;
    }

}

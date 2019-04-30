package su.efremov.wallet.controller.request;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AddFundsRestRequest {

    @PositiveOrZero
    @NotNull
    private Long userId;

    @PositiveOrZero
    @NotNull
    private BigDecimal amount;

    private CurrencyEnum currency;

}

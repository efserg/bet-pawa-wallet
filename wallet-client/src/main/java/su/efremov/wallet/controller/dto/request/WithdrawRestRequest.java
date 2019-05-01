package su.efremov.wallet.controller.dto.request;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import su.efremov.wallet.controller.dto.CurrencyEnum;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class WithdrawRestRequest {

    @PositiveOrZero
    @NotNull
    private BigDecimal amount;

    @NotNull
    private CurrencyEnum currency;

}

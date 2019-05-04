package su.efremov.wallet.domain;

import java.math.BigDecimal;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity(name = "balance")
@Immutable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
public class Balance {

    @EmbeddedId
    private BalanceId id;

    private BigDecimal amount;

}

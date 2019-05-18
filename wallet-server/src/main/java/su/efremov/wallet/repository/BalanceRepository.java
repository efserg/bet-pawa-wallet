package su.efremov.wallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.efremov.wallet.domain.Balance;
import su.efremov.wallet.domain.BalanceId;
import su.efremov.wallet.domain.CurrencyEnum;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, BalanceId> {

    Optional<Balance> findByIdUserIdAndIdCurrency(Long userId, CurrencyEnum currency);

    List<Balance> findByIdUserId(Long userId);

}

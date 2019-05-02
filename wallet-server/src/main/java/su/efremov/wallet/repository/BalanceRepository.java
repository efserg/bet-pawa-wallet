package su.efremov.wallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import su.efremov.bet.pawa.deposit.Currency;
import su.efremov.wallet.domain.Balance;
import su.efremov.wallet.domain.CurrencyEnum;

@Repository
public interface BalanceRepository extends CrudRepository<Balance, Long> {

    Optional<Balance> findByUserIdAndCurrency(Long userId, CurrencyEnum currency);

    List<Balance> findByUserId(Long userId);

}

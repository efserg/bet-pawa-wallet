package su.efremov.wallet.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import su.efremov.wallet.domain.Transaction;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
}

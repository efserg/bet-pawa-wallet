package su.efremov.wallet.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import su.efremov.wallet.domain.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}

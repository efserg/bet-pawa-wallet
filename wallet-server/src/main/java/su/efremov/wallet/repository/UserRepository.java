package su.efremov.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.efremov.wallet.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}

package ge.mmo.auth.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Query("select a from Account a where lower(a.username) = lower(:username)")
    Optional<Account> findByUsernameIgnoreCase(@Param("username") String username);

    @Query("select count(a) > 0 from Account a where lower(a.username) = lower(:username)")
    boolean existsByUsernameIgnoreCase(@Param("username") String username);
}

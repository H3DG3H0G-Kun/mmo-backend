package ge.mmo.world.character;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerCharacterRepository extends JpaRepository<PlayerCharacter, UUID> {

    List<PlayerCharacter> findByAccountIdOrderByCreatedAtAsc(UUID accountId);

    long countByAccountId(UUID accountId);

    @Query("select count(c) > 0 from PlayerCharacter c where lower(c.name) = lower(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);

    Optional<PlayerCharacter> findByIdAndAccountId(UUID id, UUID accountId);
}

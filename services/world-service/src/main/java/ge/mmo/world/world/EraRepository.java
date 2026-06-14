package ge.mmo.world.world;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EraRepository extends JpaRepository<Era, Integer> {

    Optional<Era> findByCode(String code);

    /** The starting eras open to new Watchers, earliest first. */
    List<Era> findByDefaultUnlockedTrueOrderByOrdinalAsc();
}

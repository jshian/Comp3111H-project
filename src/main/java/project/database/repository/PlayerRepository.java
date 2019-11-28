package project.database.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import project.Player;

import java.util.List;


/**
 * Interface for player repository.
 */
public interface PlayerRepository extends CrudRepository<Player,Integer> {

    /**
     * Find the players who have the top 10 scores.
     * @return The players who have the top 10 scores.
     */
    @Query(nativeQuery = true,value = "select * from player order by score desc limit 10")
    List<Player> findScoreTop10();
}

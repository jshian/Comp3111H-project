package project.database.repository;



import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import project.Player;

import java.util.List;


public interface PlayerRepository extends CrudRepository<Player,Integer> {

    @Query(nativeQuery = true,value = "select * from player order by score desc limit 10")
    List<Player> findScoreTop10();
}

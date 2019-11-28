package project.database.repository;



import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import project.database.entity.Player;

import java.util.List;


public interface PlayerRepository extends CrudRepository<Player,Integer> {


    //@Query("select u from Student u order by u.score desc limit 0,10")
    @Query(nativeQuery = true,value = "select * from player order by score desc limit 10")
    List<Player> findScoreTop10();
}

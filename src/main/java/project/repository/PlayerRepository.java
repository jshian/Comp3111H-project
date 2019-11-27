package project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

}

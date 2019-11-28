package project.database.service;




import project.Player;

import java.util.List;

public interface PlayerService {

    Player save(Player player);

    List<Player> findScoreTop10();
}

package project.database.service;



import project.database.entity.Player;

import java.util.List;

public interface PlayerService {

    Player save(Player player);

    List<Player> findScoreTop10();
}

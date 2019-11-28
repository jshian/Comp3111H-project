package project.database.service;


import project.Player;

import java.util.List;

/**
 * Interface for the player service.
 */
public interface PlayerService {

    /**
     * Returns the player who is game over and to be saved.
     * @param player the player who is game over and to be saved.
     * @return The player to be saved.
     */
    Player save(Player player);

    /**
     * Returns the players who have the top 10 scores.
     * @return The players who have the top 10 scores.
     */
    List<Player> findScoreTop10();
}

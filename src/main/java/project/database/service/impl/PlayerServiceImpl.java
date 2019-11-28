package project.database.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.Player;
import project.database.repository.PlayerRepository;
import project.database.service.PlayerService;

import java.util.List;


/**
 * Implement the function provided by the player service interface.
 */
@Service
public class PlayerServiceImpl implements PlayerService {
    /**
     * The player repository.
     */
    @Autowired
    private PlayerRepository playerRepository;

    /**
     * Returns the player who is game over and to be saved.
     * @param player the player who is game over and to be saved.
     * @return The player to be saved.
     */
    public Player save(Player player){
        return playerRepository.save(player);
    }

    /**
     * Returns the players who have the top 10 scores.
     * @return The players who have the top 10 scores.
     */
    @Override
    public List<Player> findScoreTop10() {
        return playerRepository.findScoreTop10();
    }

}

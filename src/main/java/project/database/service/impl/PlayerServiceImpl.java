package project.database.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.database.entity.Player;
import project.database.repository.PlayerRepository;
import project.database.service.PlayerService;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    public Player save(Player player){
        return playerRepository.save(player);
    }

    @Override
    public List<Player> findScoreTop10() {
        return playerRepository.findScoreTop10();
    }

}

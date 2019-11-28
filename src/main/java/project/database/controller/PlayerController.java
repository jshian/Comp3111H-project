package project.database.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.Player;
import project.database.service.PlayerService;

import java.util.List;

/**
 * The class control the activities of player services.
 */
@Controller
@RequestMapping(value = "/players")
public class PlayerController {

    /**
     * The player service.
     */
    @Autowired
    private PlayerService playerService;

    /**
     * Returns the player to be saved.
     * @param player The player to be saved.
     * @return The player to be saved.
     */
    @ResponseBody
    @PostMapping(value = "/add_post")
    public Player savePost(@RequestBody Player player){
        String name = player.getName();
        if(name==null||name.length()==0){
            throw new RuntimeException("name not null");
        }
        return playerService.save(player);
    }

    /**
     * Returns the players who have the top 10 scores.
     * @return The players who have the top 10 scores.
     */
    @ResponseBody
    @GetMapping(value = "/find_score_top10")
    public List<Player> find(){
        return playerService.findScoreTop10();
    }


    /**
     * Returns leaderboard which contains the top 10 scores.
     * @return the leaderboard.
     */
    @GetMapping("/table")
    public String table(){
        return "Leaderboard";
    }
}

package project.database.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.Player;
import project.database.service.PlayerService;

import java.util.List;

@RestController
@RequestMapping(value = "/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;



    @ResponseBody
    @PostMapping(value = "/add_post")
    public Player savePost(@RequestBody Player player){
        String name = player.getName();
        if(name==null||name.length()==0){
            throw new RuntimeException("name not null");
        }
        return playerService.save(player);
    }

    @ResponseBody
    @GetMapping(value = "/find_score_top10")
    public List<Player> find(){
        return playerService.findScoreTop10();
    }
}

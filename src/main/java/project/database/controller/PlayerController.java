package project.database.controller;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.Player;
import project.database.service.PlayerService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @ResponseBody
    @GetMapping(value = "/add")
    public Player save(HttpServletRequest request){
        String  name =request.getParameter("name");
        if(name==null||name.length()==0){
            throw new RuntimeException("name not null");
        }
        Player player =new Player();
//        player.setName(name);
        return playerService.save(player);
    }


    @ResponseBody
    @PostMapping(value = "/add_post")
    public Player savePost(@RequestBody Player player){
        String name = player.getName();
        if(name==null||name.length()==0){
            throw new RuntimeException("name not null");
        }
        // copy playerModel's properties value to player with name consistent
        return playerService.save(player);
    }
//    @ResponseBody
//    @PostMapping(value = "/add_post2")
//    public Player savePost2(@RequestBody PlayerModel playerModel){
//        String name = playerModel.getName();
//        if(name==null||name.length()==0){
//            throw new RuntimeException("name not null");
//        }
//        // copy playerModel's properties value to player with name consistent
//        Player player =new Player();
//        BeanUtils.copyProperties(playerModel,player);
//        return playerService.save(player);
//    }


    @ResponseBody
    @GetMapping(value = "/find_score_top10")
    public List<Player> find(){
        return playerService.findScoreTop10();
    }
}

package project.database.utils;


import org.springframework.web.client.RestTemplate;
import project.Player;

public class HttpUtils {

    public static void main(String[] args) {
        // client request server
        RestTemplate restTemplate = new RestTemplate();
        Player player =new Player();
//        player.setName("test");
//        player.setScore(1000);
        String url ="http://localhost:8080/players/add_post";
        Player result =  restTemplate.postForObject(url, player,Player.class);
        System.out.println(result);
    }
}

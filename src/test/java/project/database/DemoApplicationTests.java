package project.database;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import project.database.repository.PlayerRepository;
import project.database.entity.Player;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class DemoApplicationTests {

    @Autowired
    private Session session;

    @Autowired
    private Transaction transaction;
    @Autowired
    private PlayerRepository playerRepository;
    @Test
    public void test(){
        transaction.begin();
        Player player =new Player();
        player.setName("alan");
        player.setScore(1000000);
        session.save(player);
        transaction.commit();
    }
    @Test
    public void contextLoads() {
        Player player =new Player();
        player.setId(1);
        player.setName("test");
        playerRepository.save(player);
    }
}

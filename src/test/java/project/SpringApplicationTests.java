package project;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
class SpringApplicationTests {
    @Autowired
    private Session session;

    @Autowired
    private Transaction transaction;

    @Test
    void add() {
        transaction.begin();
        Player p = new Player("tk", 0);
        session.save(p);
        transaction.commit();
    }

    @Test
    void load() {
        transaction.begin();
        Query query = session.createQuery("from Player");
        // unchecked
        List<Player> list = query.list();
        for (Player p : list) {
            System.out.println(p.getName() + " " + p.getResourcesProperty());
        }
        transaction.commit();
    }

}

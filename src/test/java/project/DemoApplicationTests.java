package project;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class DemoApplicationTests {
    @Autowired
    private Session session;

    @Autowired
    private Transaction transaction;

    @Test
    void add(){
        transaction.begin();
        Player p = new Player("tk", 0);
        session.save(p);
        transaction.commit();
    }

}

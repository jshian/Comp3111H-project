package project;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import project.config.Config;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@SpringBootApplication
public class SpringApplication {

    static EntityManager emf = null;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = org.springframework.boot.SpringApplication.run(SpringApplication.class, args);
        Config hc = context.getBean(Config.class);
        emf = hc.getEntityManagerFactory().createEntityManager();
    }

    public static void test() {
        EntityTransaction tran = emf.getTransaction();
        tran.begin();
        Player p = new Player("tk", 0);
        emf.persist(p);
        tran.commit();

    }

    public static void close() {
        if (emf != null) {
            emf.close();
        }
    }
}

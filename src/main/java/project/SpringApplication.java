package project;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import project.config.HibernateConfig;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@SpringBootApplication
public class SpringApplication {

    static EntityManager emf = null;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = org.springframework.boot.SpringApplication.run(SpringApplication.class, args);
        HibernateConfig hc = context.getBean(HibernateConfig.class);
        emf = hc.getEntityManagerFactory().createEntityManager();
        EntityTransaction tran = emf.getTransaction();

    }

}

package project;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import project.config.Config;
import project.control.Manager;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@SpringBootApplication
public class SpringApplication {

    public static void main(String[] args) {
        if (Manager.getEntityManager() == null) {
            ConfigurableApplicationContext context = org.springframework.boot.SpringApplication.run(SpringApplication.class, args);
            Config hc = context.getBean(Config.class);
            Manager.setEntityManager(hc.getEntityManagerFactory().createEntityManager());
        }
    }
}

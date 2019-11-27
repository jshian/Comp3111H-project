package project;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import project.config.HibernateConfig;

@SpringBootApplication
public class SpringApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = org.springframework.boot.SpringApplication.run(SpringApplication.class, args);
        HibernateConfig hc = context.getBean(HibernateConfig.class);
        System.out.println(hc.getTransaction());
    }

}

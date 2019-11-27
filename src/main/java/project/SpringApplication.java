package project;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import project.config.HibernateConfig;

@SpringBootApplication
public class SpringApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = org.springframework.boot.SpringApplication.run(SpringApplication.class, args);
//        HibernateConfig hc = (HibernateConfig) context.getBean("HibernateConfig");
//        System.out.println(hc);
    }

}

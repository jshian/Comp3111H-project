package project.database.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
public class HibernateConfig {

    @Autowired
    private EntityManagerFactory entityManagerFactory;


    @Bean
    protected Session getSession() {
        return entityManagerFactory.unwrap(SessionFactory.class).openSession();
    }

    @Bean
    public Transaction getTransaction(){
        return getSession().getTransaction();
    }
}
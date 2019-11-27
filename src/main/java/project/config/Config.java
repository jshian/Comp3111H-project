package project.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
public class Config {

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    /**
     * access the session needed for hibernate.
     * @return the session needed for hibernate.
     */
    @Bean
    public Session getSession() {
        return entityManagerFactory.unwrap(SessionFactory.class).openSession();
    }

    /**
     * access the transaction needed for hibernate.
     * @return the transaction needed for hibernate.
     */
    @Bean
    public Transaction getTransaction(){
        return getSession().getTransaction();
    }
}
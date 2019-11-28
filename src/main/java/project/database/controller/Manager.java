package project.database.controller;

import project.arena.ArenaInstance;
import project.entity.*;
import project.query.ArenaObjectStorage;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Iterator;

/**
 * Manager that perform save/load game function.
 */
public class Manager {

    /**
     * the entityManagerFactory of manager.
     */
    private static EntityManagerFactory entityManagerFactory = null;

    /**
     * set the entityManagerFactory of manager.
     * @param entityManagerFactory the entityManagerFactory of manager.
     */
    public static void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        Manager.entityManagerFactory = entityManagerFactory;
    }

    /**
     * remove all arenaInstances in the database.
     */
    private static void removeAll() {
        if(entityManagerFactory == null) return;

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction tran = null;
        try {
            tran = entityManager.getTransaction();
            tran.begin();

            Query q1 = entityManager.createQuery("DELETE FROM ArenaInstance");
            Query q2 = entityManager.createQuery("DELETE FROM ArenaObject");
            Query q3 = entityManager.createQuery("DELETE FROM ArenaObjectPositionInfo");
            Query q4 = entityManager.createQuery("DELETE FROM ArenaObjectStorage");
            Query q5 = entityManager.createQuery("DELETE FROM Player");
            Query q6 = entityManager.createQuery("DELETE FROM StatusEffect");

            q1.executeUpdate();
            q2.executeUpdate();
            q3.executeUpdate();
            q4.executeUpdate();
            q5.executeUpdate();
            q6.executeUpdate();

            tran.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tran.rollback();
        } finally {
            entityManager.close();
        }
    }

    /**
     * save the arenaInstance to database.
     * @param arenaInstance the arenaInstance.
     */
    public static void save(ArenaInstance arenaInstance) {
        if(entityManagerFactory == null) return;
        //removeAll();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction tran = null;
        try {
            tran = entityManager.getTransaction();
            tran.begin();

            add(entityManager, arenaInstance.getPlayer());
            ArenaObjectStorage tempStorage = arenaInstance.getStorage();
            for (ArenaObject o : tempStorage.getTowers()) {
                System.out.println("tower");
                add(entityManager, o.getPositionInfo());
                add(entityManager, o);
            }
            for (ArenaObject o : tempStorage.getMonsters()) {
                add(entityManager, o.getPositionInfo());

                Iterator<StatusEffect> i = ((Monster) o).getStatusEffects();
                while (i.hasNext()) {
                    add(entityManager, i.next());
                }
                for (ArenaObjectPositionInfo pos : ((Monster) o).getTrail()) {
                    add(entityManager, pos);
                }
                add(entityManager, o);
            }
            for (ArenaObject o : tempStorage.getProjectiles()) {
                add(entityManager, o.getPositionInfo());
                add(entityManager, o);
            }
            add(entityManager, tempStorage);
            add(entityManager, arenaInstance);

            tran.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tran.rollback();
        } finally {
            entityManager.close();
        }
    }

    private static void add(EntityManager entityManager, Object o) throws Exception {
        if (entityManager.contains(o))
            entityManager.merge(o);
        else
            entityManager.persist(o);
    }

    /**
     * load an arenaInstance from the database.
     * @return an arenaInstance from the database.
     */
    public static ArenaInstance load() {
        if(entityManagerFactory == null) return null;

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction tran = null;
        ArenaInstance a = null;
        try {
            tran = entityManager.getTransaction();
            tran.begin();

            CriteriaBuilder qb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> cq = qb.createQuery(Long.class);
            cq.select(qb.count(cq.from(ArenaInstance.class)));
            Long numOfRow = entityManager.createQuery(cq).getSingleResult();

            if (numOfRow > 0) {
                String sql = "SELECT t FROM ArenaInstance t order by t.id desc";
                Query query = entityManager.createQuery(sql);
                a = (ArenaInstance) query.setMaxResults(1).getResultList().get(0);
            }

            tran.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tran.rollback();
        } finally {
            entityManager.close();
        }

        return a;
    }

}

package project.control;

import project.Player;
import project.SpringApplication;
import project.arena.ArenaInstance;
import project.entity.*;
import project.query.ArenaObjectStorage;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Iterator;
import java.util.List;

public class Manager {

    static EntityManager entityManager = null;

    public static EntityManager getEntityManager() {
        return entityManager;
    }

    public static void setEntityManager(EntityManager entityManager) {
        Manager.entityManager = entityManager;
    }

    private static void removeAll() {
        if(entityManager == null) return;
        try {
            EntityTransaction tran = entityManager.getTransaction();
            tran.begin();

            Query q1 = entityManager.createQuery("DELETE FROM ArenaInstance");
            Query q2 = entityManager.createQuery("DELETE FROM ArenaObject");
            Query q3 = entityManager.createQuery("DELETE FROM ArenaObjectPositionInfo");
            Query q4 = entityManager.createQuery("DELETE FROM ArenaObjectStorage");
            Query q5 = entityManager.createQuery("DELETE FROM Player");
            Query q6 = entityManager.createQuery("DELETE FROM StatusEffect");

//            Query q7 = entityManager.createQuery("DELETE FROM MonsterStatusEffects");
//            Query q8 = entityManager.createQuery("DELETE FROM ArenaObjectStorageMonsters");
//            Query q9 = entityManager.createQuery("DELETE FROM ArenaObjectStorageProjectiles");
//            Query q10 = entityManager.createQuery("DELETE FROM ArenaObjectStorageTowers");
//            Query q11 = entityManager.createQuery("DELETE FROM MonsterTrail");

            q1.executeUpdate();
            q2.executeUpdate();
            q3.executeUpdate();
            q4.executeUpdate();
            q5.executeUpdate();
            q6.executeUpdate();

//            q7.executeUpdate();
//            q8.executeUpdate();
//            q9.executeUpdate();
//            q10.executeUpdate();
//            q11.executeUpdate();

            tran.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save(ArenaInstance arenaInstance) {
        if(entityManager == null) return;

        removeAll();
        try {
            EntityTransaction tran = entityManager.getTransaction();
            tran.begin();

            entityManager.persist(arenaInstance.getPlayer());
            ArenaObjectStorage tempStorage = arenaInstance.getStorage();
            for (ArenaObject o : tempStorage.getTowers()) {
                entityManager.persist(o.getPositionInfo());
                entityManager.persist(o);
            }
            for (ArenaObject o : tempStorage.getMonsters()) {
                entityManager.persist(o.getPositionInfo());

                Iterator<StatusEffect> i = ((Monster) o).getStatusEffects();
                while (i.hasNext()) {
                    entityManager.persist(i.next());
                }
                for (ArenaObjectPositionInfo pos : ((Monster) o).getTrail()) {
                    entityManager.persist(pos);
                }
                entityManager.persist(o);
            }
            for (ArenaObject o : tempStorage.getProjectiles()) {
                entityManager.persist(o.getPositionInfo());
                entityManager.persist(o);
            }
            entityManager.persist(tempStorage);
            entityManager.persist(arenaInstance);

            tran.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArenaInstance load() {
        if(entityManager == null) return null;

        ArenaInstance a = null;
        try {
            EntityTransaction tran = entityManager.getTransaction();
            tran.begin();

            CriteriaBuilder qb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> cq = qb.createQuery(Long.class);
            cq.select(qb.count(cq.from(ArenaInstance.class)));
            Long numOfRow = entityManager.createQuery(cq).getSingleResult();

            if (numOfRow > 0) {
                String sql = "SELECT t FROM ArenaInstance t";
                Query query = entityManager.createQuery(sql);
                a = (ArenaInstance) query.setMaxResults(1).getResultList().get(0);
            }

            tran.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return a;
    }
}

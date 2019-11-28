package project.query;

import java.util.LinkedList;
import java.util.Random;

import project.control.ArenaManager;
import project.entity.ArenaObjectFactory;
import project.entity.Monster;
import project.entity.Projectile;
import project.entity.Tower;
import project.entity.ArenaObjectFactory.MonsterType;
import project.entity.ArenaObjectFactory.TowerType;
import project.query.ArenaObjectStorage.StoredType;

/**
 * Helper interface to facilitate mass addition and removal of objects from
 * {@link ArenaObjectStorage}.
 */
interface ArenaObjectStorageHelper {

    /**
     * Adds a random {@link Tower} to a random location on the arena on the creator's behalf.
     * @param creator The object which creates the tower.
     * @return The added tower.
     */
    public static Tower addTower(Object creator) {
        Random rng = new Random();

        Tower t = ArenaObjectFactory.createTower(creator,
            TowerType.values()[rng.nextInt(TowerType.values().length)],
            (short) rng.nextInt(ArenaManager.ARENA_WIDTH + 1),
            (short) rng.nextInt(ArenaManager.ARENA_HEIGHT + 1)
        );
        System.out.println(String.format("Added tower: x = %d, y = %d", t.getX(), t.getY()));

        return t;
    }

    /**
     * Adds a random {@link Monster} to a random location on the arena on the creator's behalf.
     * @param creator The object which creates the tower.
     * @return The added monster.
     */
    public static Monster addMonster(Object creator) {
        Random rng = new Random();

        Monster m = ArenaObjectFactory.createMonster(creator,
            MonsterType.values()[rng.nextInt(MonsterType.values().length)],
            (short) rng.nextInt(ArenaManager.ARENA_WIDTH + 1),
            (short) rng.nextInt(ArenaManager.ARENA_HEIGHT + 1), 1
        );
        System.out.println(String.format("Added monster: x = %d, y = %d", m.getX(), m.getY()));

        return m;
    }

    /**
     * Adds a random {@link Projectile} to a random location on the arena on the creator's behalf.
     * @param creator The object which creates the tower.
     * @param tower The tower to link the projectile to.
     * @param monster The monster to link the projectile to.
     * @return The added projectile.
     */
    public static Projectile addProjectile(Object creator, Tower tower, Monster monster) {
        Random rng = new Random();

        Projectile p = ArenaObjectFactory.createProjectile(creator, tower, monster,
            (short) rng.nextInt(ArenaManager.ARENA_WIDTH + 1),
            (short) rng.nextInt(ArenaManager.ARENA_HEIGHT + 1));
        System.out.println(String.format("Added projectile: x = %d, y = %d", p.getX(), p.getY()));

        return p;
    }

    /**
     * Moves a random {@link Tower}, {@link Monster} and {@link Projectile} (if they exist) to a random position on the arena on the mover's behalf.
     * @param mover The object which moves the object.
     * @return The added tower.
     */
    @SuppressWarnings("unchecked")
    public static void moveObjects(Object mover) {
        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();
        LinkedList<Tower> towers = (LinkedList<Tower>) storage.getIndexFor(StoredType.TOWER);
        LinkedList<Monster> monsters = (LinkedList<Monster>) storage.getIndexFor(StoredType.MONSTER);
        LinkedList<Projectile> projectiles = (LinkedList<Projectile>) storage.getIndexFor(StoredType.PROJECTILE);

        Random rng = new Random();

        if (!towers.isEmpty()) {
            Tower t = towers.get(rng.nextInt(towers.size()));
            System.out.println(String.format("Moving tower: x = %d, y = %d", t.getX(), t.getY()));
            t.moveObject(mover, (short) rng.nextInt(ArenaManager.ARENA_WIDTH + 1), (short) rng.nextInt(ArenaManager.ARENA_HEIGHT + 1));
            System.out.println(String.format("Moved tower: x = %d, y = %d", t.getX(), t.getY()));
        }
        if (!monsters.isEmpty()) {
            Monster m = monsters.get(rng.nextInt(monsters.size()));
            System.out.println(String.format("Moving monster: x = %d, y = %d", m.getX(), m.getY()));
            m.moveObject(mover, (short) rng.nextInt(ArenaManager.ARENA_WIDTH + 1), (short) rng.nextInt(ArenaManager.ARENA_HEIGHT + 1));
            System.out.println(String.format("Moved monster: x = %d, y = %d", m.getX(), m.getY()));
        }
        if (!projectiles.isEmpty()) {
            Projectile p = projectiles.get(rng.nextInt(projectiles.size()));
            System.out.println(String.format("Moving projectile: x = %d, y = %d", p.getX(), p.getY()));
            p.moveObject(mover, (short) rng.nextInt(ArenaManager.ARENA_WIDTH + 1), (short) rng.nextInt(ArenaManager.ARENA_HEIGHT + 1));
            System.out.println(String.format("Moved projectile: x = %d, y = %d", p.getX(), p.getY()));
        }
    }

    /**
     * Adds a random {@link Tower} to a specific location on the arena on the creator's behalf.
     * @param creator The object which creates the tower.
     * @param x The x-coordinate of the tower.
     * @param y The y-coordinate of the tower.
     * @return The added tower.
     */
    public static Tower addTower(Object creator, short x, short y) {
        Random rng = new Random();

        Tower t = ArenaObjectFactory.createTower(creator,
            TowerType.values()[rng.nextInt(TowerType.values().length)], x, y
        );
        System.out.println(String.format("Added tower: x = %d, y = %d", t.getX(), t.getY()));

        return t;
    }

    /**
     * Adds a random {@link Monster} to a specific location on the arena on the creator's behalf.
     * @param creator The object which creates the monster.
     * @param x The x-coordinate of the monster.
     * @param y The y-coordinate of the monster.
     * @return The added monster.
     */
    public static Monster addMonster(Object creator, short x, short y) {
        Random rng = new Random();

        Monster m = ArenaObjectFactory.createMonster(creator,
            MonsterType.values()[rng.nextInt(MonsterType.values().length)], x, y, 1
        );
        System.out.println(String.format("Added monster: x = %d, y = %d", m.getX(), m.getY()));

        return m;
    }

    /**
     * Adds a random {@link Projectile} to a specific location on the arena on the creator's behalf.
     * @param creator The object which creates the projectile.
     * @param tower The tower to link the projectile to.
     * @param monster The monster to link the projectile to.
     * @param x The x-coordinate of the projectile.
     * @param y The y-coordinate of the projectile.
     * @return The added tower.
     */
    public static Projectile addProjectile(Object creator, Tower tower, Monster monster, short x, short y) {
        Projectile p = ArenaObjectFactory.createProjectile(creator, tower, monster, x, y);
        System.out.println(String.format("Added projectile: x = %d, y = %d", p.getX(), p.getY()));

        return p;
    }

    /**
     * Moves a random {@link Tower}, {@link Monster} and {@link Projectile} (if they exist) to a specific position on the arena on the mover's behalf.
     * @param mover The object which moves the object.
     * @param x The x-coordinate of the new position.
     * @param y The y-coordinate of the new position.
     * @return The added tower.
     */
    @SuppressWarnings("unchecked")
    public static void moveObjects(Object mover, short x, short y) {
        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();
        LinkedList<Tower> towers = (LinkedList<Tower>) storage.getIndexFor(StoredType.TOWER);
        LinkedList<Monster> monsters = (LinkedList<Monster>) storage.getIndexFor(StoredType.MONSTER);
        LinkedList<Projectile> projectiles = (LinkedList<Projectile>) storage.getIndexFor(StoredType.PROJECTILE);
        Random rng = new Random();

        if (!towers.isEmpty()) {
            Tower t = towers.get(rng.nextInt(towers.size()));
            System.out.println(String.format("Moving tower: x = %d, y = %d", t.getX(), t.getY()));
            t.moveObject(mover, x, y);
            System.out.println(String.format("Moved tower: x = %d, y = %d", t.getX(), t.getY()));
        }
        if (!monsters.isEmpty()) {
            Monster m = monsters.get(rng.nextInt(monsters.size()));
            System.out.println(String.format("Moving monster: x = %d, y = %d", m.getX(), m.getY()));
            m.moveObject(mover, x, y);
            System.out.println(String.format("Moved monster: x = %d, y = %d", m.getX(), m.getY()));
        }
        if (!projectiles.isEmpty()) {
            Projectile p = projectiles.get(rng.nextInt(projectiles.size()));
            System.out.println(String.format("Moving projectile: x = %d, y = %d", p.getX(), p.getY()));
            p.moveObject(mover, x, y);
            System.out.println(String.format("Moved projectile: x = %d, y = %d", p.getX(), p.getY()));
        }
    }
}
package project.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.Random;

import org.junit.Test;

import project.JavaFXTester;
import project.control.ArenaManager;
import project.entity.ArenaObject;
import project.entity.ArenaObjectFactory;
import project.entity.Monster;
import project.entity.Projectile;
import project.entity.Tower;
import project.query.ArenaObjectStorage;
import project.query.ArenaObjectStorage.SortOption;
import project.query.ArenaObjectStorage.StoredComparableType;
import project.query.ArenaObjectStorage.StoredType;

/**
 * Tests the {@link ArenaObjectStorage} and, associatively, the {@link ArenaObjectFactory} class.
 */
public class ArenaObjectStorageTest extends JavaFXTester {
    // The objects to be tested
    private LinkedList<Tower> towers = new LinkedList<>();
    private LinkedList<Projectile> projectiles = new LinkedList<>();
    private LinkedList<Monster> monsters = new LinkedList<>();
    
    // Number of random test cases
    private static int NUM_RANDOM_ACTIONS = 1000;
    
    private void log() {
        System.out.println("--- CURRENT OBJECTS IN STORAGE ---");
        for (Tower t : towers) {
            System.out.println(String.format("Tower: x = %d, y = %d", t.getX(), t.getY()));
        }
        for (Projectile p : projectiles) {
            System.out.println(String.format("Projectile: x = %d, y = %d", p.getX(), p.getY()));
        }
        for (Monster m : monsters) {
            System.out.println(String.format("Monster: x = %d, y = %d", m.getX(), m.getY()));
        }
    }

    private void addObjects() {
        Tower t = ArenaObjectStorageHelper.addTower(this);
        towers.add(t);

        Monster m = ArenaObjectStorageHelper.addMonster(this);
        monsters.add(m);

        projectiles.add(ArenaObjectStorageHelper.addProjectile(this, t, m));
    }

    private void removeObjects() {
        Random rng = new Random();

        if (!towers.isEmpty()) {
            Tower t = towers.remove(rng.nextInt(towers.size()));
            System.out.println(String.format("Removed tower: x = %d, y = %d", t.getX(), t.getY()));
            ArenaObjectFactory.removeObject(this, t);
        }
        if (!monsters.isEmpty()) {
            Monster m = monsters.remove(rng.nextInt(monsters.size()));
            System.out.println(String.format("Removed monster: x = %d, y = %d", m.getX(), m.getY()));
            ArenaObjectFactory.removeObject(this, m);
        }
        if (!projectiles.isEmpty()){
            Projectile p = projectiles.remove(rng.nextInt(projectiles.size()));
            System.out.println(String.format("Removed projectile: x = %d, y = %d", p.getX(), p.getY()));
            ArenaObjectFactory.removeObject(this, p);
        }
    }

    private void moveObjects() {
        ArenaObjectStorageHelper.moveObjects(this);
    }

    private void addObjects(short x, short y) {
        Tower t = ArenaObjectStorageHelper.addTower(this, x, y);
        towers.add(t);

        Monster m = ArenaObjectStorageHelper.addMonster(this, x, y);
        monsters.add(m);

        projectiles.add(ArenaObjectStorageHelper.addProjectile(this, t, m, x, y));
    }

    private void moveObjects(short x, short y) {
        ArenaObjectStorageHelper.moveObjects(this, x, y);
    }

    private void doRandomAction() {
        Random rng = new Random();
        double randomNumber = rng.nextDouble();

        if (randomNumber <= 0.5f) {
            moveObjects();
        } else if (randomNumber <= 0.8f) {
            addObjects();
        } else {
            removeObjects();
        }

        log();
    }

    private int getExpectedXIndexCount(int xCoor) {
        LinkedList<ArenaObject> objects = new LinkedList<>(towers);
        objects.addAll(projectiles);
        objects.addAll(monsters);

        int count = 0;
        for (ArenaObject o : objects) {
            if (o.getX() == xCoor) count++;
        }

        return count;
    }

    private int getExpectedYIndexCount(int yCoor) {
        LinkedList<ArenaObject> objects = new LinkedList<>(towers);
        objects.addAll(projectiles);
        objects.addAll(monsters);

        int count = 0;
        for (ArenaObject o : objects) {
            if (o.getY() == yCoor) count++;
        }

        return count;
    }

    private void checkXYIndex(ArenaObjectStorage storage) {
        LinkedList<ArenaObject> objects = new LinkedList<>(towers);
        objects.addAll(projectiles);
        objects.addAll(monsters);

        for (ArenaObject o : objects) {
            assertEquals(getExpectedXIndexCount(o.getX()), storage.getXIndex().get(o.getX()).size());
            assertEquals(getExpectedYIndexCount(o.getY()), storage.getYIndex().get(o.getY()).size());
        }
    }

    private void checkTypeIndex(ArenaObjectStorage storage) {
        assertEquals(towers.size(), storage.getIndexFor(StoredType.TOWER).size());
        assertEquals(monsters.size(), storage.getIndexFor(StoredType.MONSTER).size());
        assertEquals(projectiles.size(), storage.getIndexFor(StoredType.PROJECTILE).size());

        assertEquals(monsters.size(), storage.getSortedIndexFor(StoredComparableType.MONSTER, SortOption.ASCENDING).size());
        assertEquals(monsters.size(), storage.getSortedIndexFor(StoredComparableType.MONSTER, SortOption.DESCENDING).size());
    }

    @Test
    public void test() {
        ArenaObjectStorageHelper.disableScalarFieldUpdates();

        checkTypeIndex(ArenaManager.getActiveObjectStorage());
        checkXYIndex(ArenaManager.getActiveObjectStorage());

        addObjects(ZERO, ZERO);
        checkTypeIndex(ArenaManager.getActiveObjectStorage());
        checkXYIndex(ArenaManager.getActiveObjectStorage());

        addObjects(ZERO, ArenaManager.ARENA_HEIGHT);
        checkTypeIndex(ArenaManager.getActiveObjectStorage());
        checkXYIndex(ArenaManager.getActiveObjectStorage());

        addObjects(ArenaManager.ARENA_WIDTH, ZERO);
        checkTypeIndex(ArenaManager.getActiveObjectStorage());
        checkXYIndex(ArenaManager.getActiveObjectStorage());

        addObjects(ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT);
        checkTypeIndex(ArenaManager.getActiveObjectStorage());
        checkXYIndex(ArenaManager.getActiveObjectStorage());

        moveObjects(ZERO, ZERO);
        checkTypeIndex(ArenaManager.getActiveObjectStorage());
        checkXYIndex(ArenaManager.getActiveObjectStorage());

        moveObjects(ZERO, ArenaManager.ARENA_HEIGHT);
        checkTypeIndex(ArenaManager.getActiveObjectStorage());
        checkXYIndex(ArenaManager.getActiveObjectStorage());

        moveObjects(ArenaManager.ARENA_WIDTH, ZERO);
        checkTypeIndex(ArenaManager.getActiveObjectStorage());
        checkXYIndex(ArenaManager.getActiveObjectStorage());

        moveObjects(ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT);
        checkTypeIndex(ArenaManager.getActiveObjectStorage());
        checkXYIndex(ArenaManager.getActiveObjectStorage());

        for (int n = 0; n < NUM_RANDOM_ACTIONS; n++) {
            doRandomAction();
            System.out.println("Testing...");
            checkTypeIndex(ArenaManager.getActiveObjectStorage());
            checkXYIndex(ArenaManager.getActiveObjectStorage());
        }

        while (!towers.isEmpty() || !monsters.isEmpty() | !projectiles.isEmpty()) {
            removeObjects();
            System.out.println("Testing...");
            checkTypeIndex(ArenaManager.getActiveObjectStorage());
            checkXYIndex(ArenaManager.getActiveObjectStorage());
        }

        for (int n = 0; n < NUM_RANDOM_ACTIONS; n++) {
            addObjects();
        }
        ArenaManager.getActiveObjectStorage().clear();
        for (int x = 0; x <= ArenaManager.ARENA_WIDTH; x++) {
            assertTrue(ArenaManager.getActiveObjectStorage().getXIndex().get(x).isEmpty());
        }
        for (int y = 0; y <= ArenaManager.ARENA_WIDTH; y++) {
            assertTrue(ArenaManager.getActiveObjectStorage().getYIndex().get(y).isEmpty());
        }
        for (StoredType type : StoredType.values()) {
            assertTrue(ArenaManager.getActiveObjectStorage().getIndexFor(type).isEmpty());
        }
        for (StoredComparableType type : StoredComparableType.values()) {
            assertTrue(ArenaManager.getActiveObjectStorage().getSortedIndexFor(type, SortOption.ASCENDING).isEmpty());
            assertTrue(ArenaManager.getActiveObjectStorage().getSortedIndexFor(type, SortOption.DESCENDING).isEmpty());
        }
    }
}
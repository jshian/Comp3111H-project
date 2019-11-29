package project.entity;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.Random;

import org.junit.Test;

import project.JavaFXTester;
import project.control.ArenaManager;
import project.entity.ArenaObjectFactory.MonsterType;
import project.entity.ArenaObjectFactory.TowerType;
import project.util.FieldVisualizer;

/**
 * Tests the {@link Fox} class.
 */
public class FoxTest extends JavaFXTester {

    private void generatePathfindingFox() {
        {
            Fox f = (Fox) ArenaObjectFactory.createMonster(this, MonsterType.FOX, (short) 20, (short) 20, 1);
            f.setHealth(Double.POSITIVE_INFINITY);
        }
        {
            Fox f = (Fox) ArenaObjectFactory.createMonster(this, MonsterType.FOX, (short) 160, (short) 40, 1);
            f.setHealth(Double.POSITIVE_INFINITY);
        }
        {
            Fox f = (Fox) ArenaObjectFactory.createMonster(this, MonsterType.FOX, (short) 200, (short) 10, 1);
            f.setHealth(Double.POSITIVE_INFINITY);
            f.speed = 1;
        }
    }

    private void visualize() {
        try {
            FieldVisualizer.visualizeArenaScalarField(Float.class, ArenaManager.getActiveScalarFieldRegister().MONSTER_ATTACKS_TO_END);
        } catch (Exception e) {
            System.err.println("Failed to visualize scalar field, but it's OK");
        }
    }

    @Test
    public void testPathfinding_basicTowers() {
        for (short xPos = 1; xPos < ArenaManager.getMaxHorizontalGrids() - 1; xPos++) {
            addTowerToGrid(TowerType.BASIC, xPos, (short) 1);
            visualize();
        }
        generatePathfindingFox();
        simulateGameNoSpawning(1);
    }

    @Test
    public void testPathfinding_catapult() {        
        for (short xPos = 1; xPos < ArenaManager.getMaxHorizontalGrids() - 1; xPos++) {
            addTowerToGrid(TowerType.CATAPULT, xPos, (short) 1);
            visualize();
        }
        generatePathfindingFox();
        simulateGameNoSpawning(1);
    }

    @Test
    public void testPathfinding_ice() {        
        for (short xPos = 1; xPos < ArenaManager.getMaxHorizontalGrids() - 1; xPos++) {
            addTowerToGrid(TowerType.ICE, xPos, (short) 1);
            visualize();
        }
        generatePathfindingFox();
        simulateGameNoSpawning(1);
    }

    @Test
    public void testPathfinding_laser() {        
        for (short xPos = 1; xPos < ArenaManager.getMaxHorizontalGrids() - 1; xPos++) {
            addTowerToGrid(TowerType.LASER, xPos, (short) 1);
            visualize();
        }
        generatePathfindingFox();
        simulateGameNoSpawning(1);
    }

    @Test
    public void testStats() {
        Random rng = new Random();
        for (int i = 1; i < 100000; i += rng.nextDouble() * 1000) {
            LinkedList<Monster> monsters = new LinkedList<>();
            double foxBaseSpeed = 0;

            for (MonsterType type : MonsterType.values()) {
                monsters.add(ArenaObjectFactory.createMonster(this, type, ZERO, ZERO, i));
                if (type == MonsterType.FOX) foxBaseSpeed = monsters.peekLast().baseSpeed;
            }
            
            for (Monster m : monsters) {
                assertTrue(String.format("Fox base speed is not greatest for %s: i = %d", m.getDisplayName(), i), m.baseSpeed <= foxBaseSpeed);
            }
        }
    }
}
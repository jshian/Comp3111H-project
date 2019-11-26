package project.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Iterator;

import project.controller.ArenaManager;
import project.JavaFXTester;
import project.entity.ArenaObjectFactory.MonsterType;
import project.entity.ArenaObjectFactory.TowerType;

/**
 * Tests the derived classes of {@link Monster} including {@link Fox}, {@link Penguin} and {@link Unicorn}.
 */
public class MonsterTester extends JavaFXTester {

    static final double MAX_ERROR = 0.0001;

    @Test
    public void testInvalidBaseClassConstructor() {
        expectedException.expect(IllegalArgumentException.class);
        addMonsterToArena(MonsterType.UNICORN, (short) 1, (short) 1, 0, false); // Difficulty must be at least 1
    }

    @Test
    public void testBaseClassMethods() {
        short x = 15, y = 25;

        Monster m = addMonsterToArena(MonsterType.FOX, x, y, 1, false);
        assertEquals(m.getX(), x);
        assertEquals(m.getY(), y);
        assertNotNull(m.getTrail());
        assertTrue(m.getTrail().isEmpty());

        // Test location
        m.updatePosition((short) 40, (short) 50);
        assertEquals(m.getX(), 40);
        assertEquals(m.getY(), 50);
        m.updatePosition((short) 3, (short) 2);
        assertEquals(m.getX(), 3);
        assertEquals(m.getY(), 2);

        // Test health
        assertTrue(m.getHealth() > 0);
        m.health.set(23.45);
        assertEquals(m.getHealth(), 23.45, MAX_ERROR);
        m.takeDamage(23);
        assertEquals(m.getHealth(), 0.45, MAX_ERROR);
        m.takeDamage(10);
        assertEquals(m.getHealth(), -9.55, MAX_ERROR);
        m.takeDamage(-999);
        assertEquals(m.getHealth(), -9.55, MAX_ERROR); // Negative damage taken should do nothing
        m.health.set(50);
        assertEquals(m.getHealth(), 50, MAX_ERROR);

        // Test status effect
        StatusEffect slowEffect = new StatusEffect(StatusEffect.EffectType.Slow, 2);

        double originalSpeed = m.getSpeed();
        m.addStatusEffect(slowEffect);
        assertTrue(m.statusEffects.size() == 1 && m.statusEffects.peek() == slowEffect);
        assertTrue(originalSpeed == m.getSpeed()); // Monster should be pending slow

        Iterator<StatusEffect> iterator = m.getStatusEffects();

        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        assertTrue(count == 1);

        simulateNextFrame();
        assertFalse(m.getTrail().isEmpty()); // Monster should have moved
        assertTrue(originalSpeed > m.getSpeed()); // Monster should start to be be slowed

        simulateNextFrame();
        assertTrue(originalSpeed > m.getSpeed()); // Monster should still be slowed

        simulateNextFrame();
        assertTrue(originalSpeed == m.getSpeed()); // Monster should no longer be slowed

        simulateNextFrame();
        assertTrue(originalSpeed == m.getSpeed()); // Monster should not be slowed
    }

    @Test
    public void testFox() {
        short x = 18, y = 23;

        Fox f = (Fox) addMonsterToArena(MonsterType.FOX, x, y, 1, false);
        assertTrue(f.getHealth() > 0); // Health on generation should be greater than zero
        removeObjectFromArena(f);

        // Test pathfinding...
        // Case 1: Line of towers at yPos = 1

        // Case 1a: Line of BasicTowers
        for (short xPos = 0; xPos < ArenaManager.getMaxHorizontalGrids(); xPos++) {
            simulateBuildTower(TowerType.BASIC, xPos, (short) 1);
        }
        addMonsterToArena(MonsterType.FOX, x, y, 1000, true);
        addMonsterToArena(MonsterType.FOX, (short) 160, (short) 0, 100, true);
        simulateGameNoSpawning(4);

        for (short xPos = 0; xPos < ArenaManager.getMaxHorizontalGrids(); xPos++) {
            simulateBuildTower(TowerType.BASIC, xPos, (short) 1);
        }
        addMonsterToArena(MonsterType.FOX, (short) 200, (short) 0, 1, true);
        simulateGameNoSpawning(4);

        // Case 1b: Line of Catapults
        for (short xPos = 0; xPos < ArenaManager.getMaxHorizontalGrids(); xPos++) {
            simulateBuildTower(TowerType.CATAPULT, xPos, (short) 1);
        }
        addMonsterToArena(MonsterType.FOX, x, y, 1000, true);
        addMonsterToArena(MonsterType.FOX, (short) 160, (short) 0, 100, true);
        simulateGameNoSpawning(4);

        for (short xPos = 0; xPos < ArenaManager.getMaxHorizontalGrids(); xPos++) {
            simulateBuildTower(TowerType.CATAPULT, xPos, (short) 1);
        }
        addMonsterToArena(MonsterType.FOX, (short) 200, (short) 0, 1, true);
        simulateGameNoSpawning(4);
    }
}
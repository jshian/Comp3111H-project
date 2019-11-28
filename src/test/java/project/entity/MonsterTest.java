package project.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Iterator;
import java.util.Random;

import project.control.ArenaManager;
import project.JavaFXTester;
import project.entity.ArenaObjectFactory.MonsterType;
import project.entity.ArenaObjectFactory.TowerType;
import project.event.EventHandler;
import project.event.eventargs.ArenaObjectEventArgs;
import project.event.eventargs.EventArgs;

/**
 * Tests the derived classes of {@link Monster} including {@link Fox}, {@link Penguin} and {@link Unicorn}.
 */
public class MonsterTest extends JavaFXTester {

    static final double MAX_ERROR = 0.0001;

    Monster trackedMonster;
    boolean trackedMonsterHasDied;
    EventHandler<ArenaObjectEventArgs> deathEvent = (sender, args) -> {
        if (args.subject == trackedMonster) {
            trackedMonsterHasDied = true;
        }
    };

    @Test
    public void testInvalidBaseClassConstructor() {
        expectedException.expect(IllegalArgumentException.class);
        ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 1, (short) 1, 0); // Difficulty must be at least 1
    }

    @Test
    public void testBaseClassMethods() {
        short x = 15, y = 25;

        {
            // Test constructor
            Monster m = ArenaObjectFactory.createMonster(this, MonsterType.FOX, x, y, 1);
            assertEquals(m.getX(), x);
            assertEquals(m.getY(), y);
            assertNotNull(m.getTrail());
            assertTrue(m.getTrail().isEmpty());
            assertEquals(m.getHealth(), m.healthProperty.get(), MAX_ERROR);
            assertEquals(m.getHealth(), m.maxHealth, MAX_ERROR);
            assertEquals(m.getSpeed(), m.speed, MAX_ERROR);
            assertEquals(m.getSpeed(), m.baseSpeed, MAX_ERROR);

            // Test location
            m.moveObject(this, (short) 40, (short) 50);
            assertEquals(m.getX(), 40);
            assertEquals(m.getY(), 50);
            m.moveObject(this, (short) 3, (short) 2);
            assertEquals(m.getX(), 3);
            assertEquals(m.getY(), 2);

            // Test health
            trackedMonster = m;
            trackedMonsterHasDied = false;
            ArenaManager.getActiveEventRegister().ARENA_OBJECT_REMOVE.subscribe(deathEvent);

            m.healthProperty.set(23.45);
            assertEquals(m.getHealth(), 23.45, MAX_ERROR);
            assertFalse(trackedMonsterHasDied);
            m.takeDamage(23, this);
            assertEquals(m.getHealth(), 0.45, MAX_ERROR);
            assertFalse(trackedMonsterHasDied);
            m.takeDamage(10, this);
            assertEquals(m.getHealth(), -9.55, MAX_ERROR);
            assertTrue(trackedMonsterHasDied);
            m.takeDamage(-999, this);
            assertEquals(m.getHealth(), -9.55, MAX_ERROR); // Negative damage taken should do nothing
            assertTrue(trackedMonsterHasDied);
            m.healthProperty.set(50);
            assertEquals(m.getHealth(), 50, MAX_ERROR);
            assertTrue(trackedMonsterHasDied); // Dead monster is no longer on the arena
        }
        
        {
            Monster m = ArenaObjectFactory.createMonster(this, MonsterType.FOX, x, y, 1);

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
    
            m.onNextFrame.handleEvent(this, new EventArgs());
            assertFalse(m.getTrail().isEmpty()); // Monster should have moved
            assertTrue(originalSpeed > m.getSpeed()); // Monster should start to be be slowed
    
            m.onNextFrame.handleEvent(this, new EventArgs());
            assertTrue(originalSpeed > m.getSpeed()); // Monster should still be slowed
    
            m.onNextFrame.handleEvent(this, new EventArgs());
            assertTrue(originalSpeed == m.getSpeed()); // Monster should no longer be slowed
    
            m.onNextFrame.handleEvent(this, new EventArgs());
            assertTrue(originalSpeed == m.getSpeed()); // Monster should not be slowed
        }
    }

    @Test
    public void testFox() {
        short x = 18, y = 23;

        {
            Fox f = (Fox) ArenaObjectFactory.createMonster(this, MonsterType.FOX, x, y, 1);
            assertTrue(f.getHealth() > 0); // Health on generation should be greater than zero
            ArenaObjectFactory.removeObject(this, f);
        }

        // Test pathfinding...
        // Case 1: Line of towers at yPos = 1

        // Case 1a: Line of BasicTowers
        for (short xPos = 0; xPos < ArenaManager.getMaxHorizontalGrids(); xPos++) {
            simulateBuildTower(TowerType.BASIC, xPos, (short) 1);
        }
        {
            Fox f = (Fox) ArenaObjectFactory.createMonster(this, MonsterType.FOX, x, y, 1000);
            f.healthProperty.set(Double.POSITIVE_INFINITY);
        }
        {
            Fox f = (Fox) ArenaObjectFactory.createMonster(this, MonsterType.FOX, (short) 160, (short) 0, 100);
            f.healthProperty.set(Double.POSITIVE_INFINITY);
        }
        simulateGameNoSpawning(4);

        for (short xPos = 0; xPos < ArenaManager.getMaxHorizontalGrids(); xPos++) {
            simulateBuildTower(TowerType.BASIC, xPos, (short) 1);
        }
        {
            Fox f = (Fox) ArenaObjectFactory.createMonster(this, MonsterType.FOX, (short) 200, (short) 0, 1);
            f.healthProperty.set(Double.POSITIVE_INFINITY);
        }
        simulateGameNoSpawning(4);

        // Case 1b: Line of Catapults
        for (short xPos = 0; xPos < ArenaManager.getMaxHorizontalGrids(); xPos++) {
            simulateBuildTower(TowerType.CATAPULT, xPos, (short) 1);
        }
        {
            Fox f = (Fox) ArenaObjectFactory.createMonster(this, MonsterType.FOX, x, y, 1000);
            f.healthProperty.set(Double.POSITIVE_INFINITY);
        }
        {
            Fox f = (Fox) ArenaObjectFactory.createMonster(this, MonsterType.FOX, (short) 160, (short) 0, 100);
            f.healthProperty.set(Double.POSITIVE_INFINITY);
        }
        simulateGameNoSpawning(4);

        for (short xPos = 0; xPos < ArenaManager.getMaxHorizontalGrids(); xPos++) {
            simulateBuildTower(TowerType.CATAPULT, xPos, (short) 1);
        }
        {
            Fox f = (Fox) ArenaObjectFactory.createMonster(this, MonsterType.FOX, (short) 200, (short) 0, 1);
            f.healthProperty.set(Double.POSITIVE_INFINITY);
        }
        simulateGameNoSpawning(4);
    }

    @Test
    public void testPenguin() {
        short x = 345, y = 79;

        Penguin p = (Penguin) ArenaObjectFactory.createMonster(this, MonsterType.PENGUIN, x, y, 1);

        double maxHealth = p.maxHealth;
        p.onNextFrame.handleEvent(this, new EventArgs());
        assertEquals(p.getHealth(), maxHealth, MAX_ERROR); // Health should not go beyond max health

        double newHealth = maxHealth / 2;
        p.healthProperty.set(newHealth);
        p.onNextFrame.handleEvent(this, new EventArgs());
        assertTrue(p.getHealth() > newHealth); // Health should regenerate
        newHealth = p.getHealth();
        p.onNextFrame.handleEvent(this, new EventArgs());
        assertTrue(p.getHealth() > newHealth); // Health should regenerate
    }

    @Test
    public void testStats() {
        short x = 0, y = 0;
        Random rng = new Random();

        for (int i = 1; i < 10000; i += rng.nextDouble() * 100) {
            Fox f = (Fox) ArenaObjectFactory.createMonster(this, MonsterType.FOX, x, y, i);
            Penguin p = (Penguin) ArenaObjectFactory.createMonster(this, MonsterType.PENGUIN, x, y, i);
            Unicorn u = (Unicorn) ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, x, y, i);

            // Stats must be greater than zero
            assertTrue("Speed test failed for i = " + i, f.getSpeed() > 0 && p.getSpeed() > 0 && u.getSpeed() > 0);
            assertTrue("Health test failed for i = " + i, f.getHealth() > 0 && p.getHealth() > 0 && u.getHealth() > 0);

            // Fox is the fastest
            assertTrue("Speed test failed for i = " + i, f.baseSpeed > p.baseSpeed && f.baseSpeed > u.baseSpeed);

            // Unicorn has the most health
            assertTrue("Health test failed for i = " + i, u.maxHealth > f.maxHealth && u.maxHealth > p.maxHealth);

            ArenaObjectFactory.removeObject(this, f);
            ArenaObjectFactory.removeObject(this, p);
            ArenaObjectFactory.removeObject(this, u);
        }
    }
}
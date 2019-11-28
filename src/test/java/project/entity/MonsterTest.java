package project.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import project.control.ArenaManager;
import project.JavaFXTester;
import project.entity.ArenaObjectFactory.MonsterType;
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
        for (MonsterType type : MonsterType.values()) {
            boolean correct = false;

            try {
                ArenaObjectFactory.createMonster(this, type, ZERO, ZERO, 0); // Difficulty must be at least 1
            } catch (IllegalArgumentException e) {
                correct = true;
            }

            if (!correct) fail("An incorrect exception was thrown");
        }
    }

    @Test
    public void testMove() {
        final short NUM_TEST_CASES_PER_TYPE = 50;

        Random rng = new Random();
        for (MonsterType type : MonsterType.values()) {
            Monster m = ArenaObjectFactory.createMonster(this, type, ZERO, ZERO, 1);
    
            short prevX = ZERO;
            short prevY = ZERO;
            for (int n = 0; n < NUM_TEST_CASES_PER_TYPE; n++) {
                short x = (short) rng.nextInt(ArenaManager.ARENA_WIDTH + 1);
                short y = (short) rng.nextInt(ArenaManager.ARENA_HEIGHT + 1);
    
                m.moveObject(this, x, y);
                System.out.println(String.format("Moved monster from (%d, %d) to (%d, %d)", prevX, prevY, x, y));
                assertEquals(String.format("x is incorrect: expected %d but got %d", x, m.getX()), x, m.getX());
                assertEquals(String.format("y is incorrect: expected %d but got %d", x, m.getY()), y, m.getY());
    
                prevX = x; prevY = y;
            }
        }
    }

    @Test
    public void testTakeDamage() {
        for (MonsterType type : MonsterType.values()) {
            Monster m = ArenaObjectFactory.createMonster(this, type, ZERO, ZERO, 1);
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
    }

    @Test
    public void testStatusEffects() {
        for (MonsterType type : MonsterType.values()) {
            Monster m = ArenaObjectFactory.createMonster(this, type, ZERO, ZERO, 1);

            StatusEffect slowEffect = new StatusEffect(StatusEffect.EffectType.Slow, 2);

            double originalSpeed = m.getSpeed();
            m.addStatusEffect(slowEffect);
            assertTrue(m.statusEffects.size() == 1 && ((LinkedList<StatusEffect>)m.statusEffects).peek() == slowEffect);
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
    public void testStats() {
        Random rng = new Random();
        for (int i = 1; i < 100000; i += rng.nextDouble() * 1000) {
            LinkedList<Monster> monsters = new LinkedList<>();
            short x = (short) rng.nextInt(ArenaManager.ARENA_WIDTH + 1);
            short y = (short) rng.nextInt(ArenaManager.ARENA_HEIGHT + 1);

            for (MonsterType type : MonsterType.values()) {
                monsters.add(ArenaObjectFactory.createMonster(this, type, x, y, i));
            }
            
            for (Monster m : monsters) {
                assertEquals(String.format("x is incorrect: expected %d but got %d", x, m.getX()), x, m.getX());
                assertEquals(String.format("y is incorrect: expected %d but got %d", x, m.getY()), y, m.getY());
                assertNotNull(String.format("Trail is null for new monster at (%d, %d)", x, y), m.getTrail());
                assertTrue(String.format("Trail is not empty for new monster at (%d, %d)", x, y), m.getTrail().isEmpty());
                assertTrue(String.format("Speed is negative for %s: i = %d", m.getDisplayName(), i), m.getSpeed() > 0);
                assertTrue(String.format("Health is negative for %s: i = %d", m.getDisplayName(), i), m.getHealth() > 0);
                assertTrue(String.format("Max health is negative for %s: i = %d", m.getDisplayName(), i), m.maxHealth > 0);
                assertEquals(String.format("Health is not equal to max health for %s: i = %d", m.getDisplayName(), i), m.getHealth(), m.maxHealth, MAX_ERROR);
                assertEquals(String.format("Speed is not equal to base speed for %s: i = %d", m.getDisplayName(), i), m.getSpeed(), m.baseSpeed, MAX_ERROR);
            }
        }
    }
}
package project.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import project.JavaFXTester;
import project.entity.ArenaObjectFactory.MonsterType;
import project.event.eventargs.EventArgs;

/**
 * Tests the {@link Penguin} class.
 */
public class PenguinTest extends JavaFXTester {

    static final double MAX_ERROR = 0.0001;

    @Test
    public void testRegeneration() {
        Penguin p = (Penguin) ArenaObjectFactory.createMonster(this, MonsterType.PENGUIN, ZERO, ZERO, 1);

        double newHealth = p.maxHealth / 2;
        p.healthProperty.set(newHealth);
        p.onNextFrame.handleEvent(this, new EventArgs());
        assertTrue(p.getHealth() > newHealth); // Health should regenerate
        newHealth = p.getHealth();
        p.onNextFrame.handleEvent(this, new EventArgs());
        assertTrue(p.getHealth() > newHealth); // Health should regenerate

        p.healthProperty.set(p.maxHealth);
        p.onNextFrame.handleEvent(this, new EventArgs());
        assertEquals(p.getHealth(), p.maxHealth, MAX_ERROR); // Health should not regenerate beyond max health

        p.healthProperty.set(p.maxHealth + 10);
        p.onNextFrame.handleEvent(this, new EventArgs());
        assertEquals(p.getHealth(), p.maxHealth + 10, MAX_ERROR); // Health should not regenerate beyond max health, but nor should it be forced back down to max health
    }
}
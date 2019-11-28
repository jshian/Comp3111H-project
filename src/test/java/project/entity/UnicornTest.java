package project.entity;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.Random;

import org.junit.Test;

import project.JavaFXTester;
import project.entity.ArenaObjectFactory.MonsterType;

/**
 * Tests the {@link Unicorn} class.
 */
public class UnicornTest extends JavaFXTester {
    @Test
    public void testStats() {
        Random rng = new Random();
        for (int i = 1; i < 100000; i += rng.nextDouble() * 1000) {
            LinkedList<Monster> monsters = new LinkedList<>();
            double unicornMaxHealth = 0;

            for (MonsterType type : MonsterType.values()) {
                monsters.add(ArenaObjectFactory.createMonster(this, type, ZERO, ZERO, i));
                if (type == MonsterType.UNICORN) unicornMaxHealth = monsters.peekLast().maxHealth;
            }
            
            for (Monster m : monsters) {
                assertTrue(String.format("Unicorn max health is not greatest for %s: i = %d", m.getDisplayName(), i), m.maxHealth <= unicornMaxHealth);
            }
        }
    }
}
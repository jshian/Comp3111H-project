package project.arena.monsters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.LinkedList;

import org.junit.Test;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import project.DeepCopyTester;
import project.JavaFXTester;
import project.UIController;
import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.Grid;
import project.arena.Arena.TowerType;

/**
 * Tests the derived classes of {@link Monster} including {@link Fox}, {@link Penguin} and {@link Unicorn}.
 */
public class MonsterTester extends JavaFXTester {

    static final double MAX_ERROR = 0.0001;
    
	public void assertIllegalArgumentException_constructorTestMonster(Arena arena, Coordinates start, Coordinates end, ImageView iv, double difficulty) {
		try {
			Monster m = new TestMonster(arena, start, end, iv, difficulty);
			fail(String.format("The constructor with parameters (Arena, Coordinates, Coordinates, ImageView, %.2f) should have thrown an exception.", difficulty));
		} catch (IllegalArgumentException e) {
			
		}
	}
    
    @Test
    public void testBaseClassMethods() {
        Arena a1 = new Arena(new Label(), new AnchorPane());
        Coordinates start = new Coordinates((short) 15, (short) 25);
        Coordinates end = new Coordinates((short) 100, (short) 70);
        ImageView iv = new ImageView(new Image("/collision.png"));
        StatusEffect slowEffect = new StatusEffect(StatusEffect.EffectType.Slow, 2);        

        // Test regular constructor
        assertIllegalArgumentException_constructorTestMonster(a1, start, end, iv, 0); // Difficulty must be at least 1
        
        Monster m1 = new TestMonster(a1, start, end, iv, 1);
        assertSame(m1.getImageView(), iv);
        assertEquals(m1.getX(), 15);
        assertEquals(m1.getY(), 25);
        assertNotNull(m1.getHpLabel());
        assertNotNull(m1.getPrevCoordinates());
        assertTrue(m1.getPrevCoordinates().isEmpty());

        // Test location
        m1.setLocation((short) 40, (short) 50);
        assertEquals(m1.getX(), 40);
        assertEquals(m1.getY(), 50);
        m1.setLocation(new Coordinates((short) 3, (short) 2));
        assertEquals(m1.getX(), 3);
        assertEquals(m1.getY(), 2);
        
        // Test health
        assertFalse(m1.hasDied()); // Health on generation should be greater than zero
        m1.setHealth(23.45);
        assertEquals(m1.getHealth(), 23.45, MAX_ERROR);
        assertFalse(m1.hasDied());
        m1.takeDamage(23);
        assertEquals(m1.getHealth(), 0.45, MAX_ERROR);
        assertFalse(m1.hasDied());
        m1.takeDamage(10);
        assertEquals(m1.getHealth(), -9.55, MAX_ERROR);
        assertTrue(m1.hasDied());
        m1.takeDamage(-999);
        assertEquals(m1.getHealth(), -9.55, MAX_ERROR); // Negative damage taken should do nothing
        assertTrue(m1.hasDied());
        m1.setHealth(50);
        assertEquals(m1.getHealth(), 50, MAX_ERROR);
        assertFalse(m1.hasDied());

        // Test status effect
        double originalSpeed = m1.getSpeed();
        m1.addStatusEffect(slowEffect);
        assertTrue(originalSpeed == m1.getSpeed()); // Monster should be pending slow

        m1.nextFrame();
        assertFalse(m1.getPrevCoordinates().isEmpty()); // Monster should have moved
        assertTrue(originalSpeed > m1.getSpeed()); // Monster should start to be be slowed

        m1.nextFrame();
        assertTrue(originalSpeed > m1.getSpeed()); // Monster should still be slowed

        m1.nextFrame();
        assertTrue(originalSpeed == m1.getSpeed()); // Monster should no longer be slowed

        m1.nextFrame();
        assertTrue(originalSpeed == m1.getSpeed()); // Monster should not be slowed
    
        // Test copy constructor
        Arena a2 = new Arena(new Label(), new AnchorPane());
        Monster m2 = new TestMonster(a2, (TestMonster)m1);
        assertEquals(m2.arena, a2);

        DeepCopyTester.testDeepCopy(m1, m2);
    }
    
	public void assertIllegalArgumentException_constructorFox(Arena arena, Coordinates start, Coordinates end, ImageView iv, double difficulty) {
		try {
			Fox m = new Fox(arena, start, end, iv, difficulty);
			fail(String.format("The constructor with parameters (Arena, Coordinates, Coordinates, ImageView, %.2f) should have thrown an exception.", difficulty));
		} catch (IllegalArgumentException e) {
			
		}
	}

    @Test
    public void testFox() {
        Arena a1 = new Arena(new Label(), new AnchorPane());
        Coordinates start = new Coordinates((short) 18, (short) 23);
        Coordinates end = new Coordinates((short) 480, (short) 480);
        ImageView iv = new ImageView(new Image("/fox.png"));

        // Test regular constructor
        assertIllegalArgumentException_constructorFox(a1, start, end, iv, 0); // Difficulty must be at least 1

        Fox f1 = new Fox(a1, start, end, iv, 1);
        assertFalse(f1.hasDied()); // Health on generation should be greater than zero

        // Test Pathfinding...
        
        // Case 1a: Line of BasicTowers at y = 1
        LinkedList<Grid> gridsToPlaceTower = new LinkedList<>();
        for (short i = 1; i < UIController.MAX_H_NUM_GRID - 1; i++) {
            gridsToPlaceTower.add(new Grid(i, (short) 1));
        }
        for (Grid grid : gridsToPlaceTower) {
            a1.buildTower(grid.getCenter(), new ImageView("/basicTower.png"), TowerType.BasicTower);
        }
        /*
        f1.setHealth(Double.POSITIVE_INFINITY); // So it can't die
        a1.addObject(f1);
        while (f1.findNextCoordinates() != null) {
            a1.nextFrame();
            // Thread.sleep(1000);
        }*/

        // Test copy constructor
        Arena a2 = new Arena(new Label(), new AnchorPane());
        Fox f2 = new Fox(a2, f1);
        assertEquals(f2.arena, a2);

        DeepCopyTester.testDeepCopy(f1, f2);

    }
}
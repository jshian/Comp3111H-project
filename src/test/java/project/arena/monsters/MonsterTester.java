package project.arena.monsters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import project.DeepCopyTester;
import project.JavaFXTester;
import project.arena.Arena;
import project.arena.Coordinates;

/**
 * Tests the derived classes of {@link Monster} including {@link Fox}, {@link Penguin} and {@link Unicorn}.
 */
public class MonsterTester extends JavaFXTester {

    static final double MAX_ERROR = 0.0001;
    
    @Test
    public void testBaseClass() {
        Arena a1 = new Arena(new Label(), new AnchorPane());
        Coordinates start = new Coordinates((short) 15, (short) 25);
        Coordinates end = new Coordinates((short) 100, (short) 70);
        ImageView iv = new ImageView(new Image("/collision.png"));
        StatusEffect slowEffect = new StatusEffect(StatusEffect.EffectType.Slow, 2);

        Fox f1 = new Fox(a1, start, end, iv, 1);
        assertSame(f1.getImageView(), iv);
        assertEquals(f1.getX(), 15);
        assertEquals(f1.getY(), 25);
        assertNotNull(f1.getHpLabel());
        assertNotNull(f1.getPrevCoordinates());
        assertTrue(f1.getPrevCoordinates().isEmpty());

        assertFalse(f1.hasDied()); // Health on generation should be greater than zero
        f1.setHealth(23.45);
        assertEquals(f1.getHealth(), 23.45, MAX_ERROR);
        assertFalse(f1.hasDied());
        f1.takeDamage(23);
        assertEquals(f1.getHealth(), 0.45, MAX_ERROR);
        assertFalse(f1.hasDied());
        f1.takeDamage(10);
        assertEquals(f1.getHealth(), -9.55, MAX_ERROR);
        assertTrue(f1.hasDied());
        f1.takeDamage(-999);
        assertEquals(f1.getHealth(), -9.55, MAX_ERROR); // Negative damage taken should do nothing
        assertTrue(f1.hasDied());
        f1.setHealth(50);
        assertEquals(f1.getHealth(), 50, MAX_ERROR);
        assertFalse(f1.hasDied());

        double originalSpeed = f1.getSpeed();
        f1.addStatusEffect(slowEffect);
        assertTrue(originalSpeed == f1.getSpeed()); // Monster should be pending slow

        f1.nextFrame();
        assertFalse(f1.getPrevCoordinates().isEmpty()); // Monster should have moved
        assertTrue(originalSpeed > f1.getSpeed()); // Monster should start to be be slowed

        f1.nextFrame();
        assertTrue(originalSpeed > f1.getSpeed()); // Monster should still be slowed

        f1.nextFrame();
        assertTrue(originalSpeed == f1.getSpeed()); // Monster should no longer be slowed
    
        Arena a2 = new Arena(new Label(), new AnchorPane());
        Fox f2 = new Fox(a2, f1);
        assertEquals(f2.arena, a2);

        DeepCopyTester.testDeepCopy(f1, f2);
    }

    @Test
    public void testFox() {
        Arena a1 = new Arena(new Label(), new AnchorPane());
        Coordinates start = new Coordinates((short) 18, (short) 23);
        Coordinates end = new Coordinates((short) 34, (short) 64);
        ImageView iv = new ImageView(new Image("/collision.png"));
        StatusEffect slowEffect = new StatusEffect(StatusEffect.EffectType.Slow, 2);

        Fox f1 = new Fox(a1, start, end, iv, 235);
        assertFalse(f1.hasDied()); // Health on generation should be greater than zero
        
    }
}
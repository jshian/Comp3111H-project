package project.arena.towers;

import java.util.LinkedList;
import java.util.PriorityQueue;

import org.junit.Assert;
import org.junit.Test;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import project.Geometry;
import project.JavaFXTester;
import project.arena.ArenaInstance;
import project.arena.Coordinates;
import project.arena.ArenaInstance;
import project.arena.monsters.Monster;
import project.arena.monsters.Unicorn;

/**
 * Tests the {@link Catapult} class.
 */
public class CatapultTest extends JavaFXTester {

    @Test
    public void selectMonster() {
        ArenaInstance arena = new ArenaInstance(new Label(), new AnchorPane());
        Image img = new Image("/unicorn.png");
        Coordinates destination = new Coordinates((short) 470, (short) 10);
        PriorityQueue<Monster> testList = new PriorityQueue<>();
        LinkedList<ArenaObject> selectList = new LinkedList<>();
        LinkedList<Monster> answerList = new LinkedList<>();
        Coordinates target;

        //test shooting range valid
        Catapult catapult = new Catapult(arena, new Coordinates((short) 370,(short) 10));
        Monster m1 = new Unicorn(arena, new Coordinates((short) 110, (short) 110), destination, new ImageView(img), 1);testList.add(m1);arena.addObject(m1);
        Monster m2 = new Unicorn(arena, new Coordinates((short) 130, (short) 110), destination, new ImageView(img), 1);testList.add(m2);arena.addObject(m2);
        Monster m3 = new Unicorn(arena, new Coordinates((short) 450, (short) 10), destination, new ImageView(img), 1);testList.add(m3);arena.addObject(m3);
        target=catapult.selectMonster(testList,selectList);
        answerList.add(m3);
        Assert.assertEquals(selectList,answerList);
        for (ArenaObject m :selectList) {
            Assert.assertTrue(Geometry.isInCircle( m.getX(), m.getY(), target.getX(), target.getY(),25));
        }
        selectList.clear();

        //test shoot monster with nearest but not most monsters
        Monster m4 = new Unicorn(arena, new Coordinates((short) 330, (short) 10), destination, new ImageView(img), 1);testList.add(m4);arena.addObject(m4);
        Monster m5 = new Unicorn(arena, new Coordinates((short) 330, (short) 10), destination, new ImageView(img), 1);testList.add(m5);arena.addObject(m5);
        target=catapult.selectMonster(testList,selectList);
        Assert.assertEquals(selectList,answerList);
        for (ArenaObject m :selectList) {
            Assert.assertTrue(Geometry.isInCircle( m.getX(), m.getY(), target.getX(), target.getY(),25));
        }
        selectList.clear();

        //test shoot monster with same nearest but most monster
        Monster m6 = new Unicorn(arena, new Coordinates((short) 430, (short) 10), destination, new ImageView(img), 1);testList.add(m6);arena.addObject(m6);
        Monster m7 = new Unicorn(arena, new Coordinates((short) 470, (short) 50), destination, new ImageView(img), 1);testList.add(m7);arena.addObject(m7);
        target=catapult.selectMonster(testList,selectList);
        answerList.add(m6);
        Assert.assertEquals(selectList,answerList);
        for (ArenaObject m :selectList) {
            Assert.assertTrue(Geometry.isInCircle(m.getX(), m.getY(), target.getX(), target.getY(), 25));
        }
        selectList.clear();

    }
}
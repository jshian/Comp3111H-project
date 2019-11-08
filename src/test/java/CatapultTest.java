import org.junit.Assert;
import org.junit.Test;

import project.*;
import project.monsters.*;
import project.towers.Catapult;
import java.util.LinkedList;
import java.util.PriorityQueue;

import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;

import static org.junit.Assert.*;

public class CatapultTest {

    @Test
    public void selectMonster() {
        Arena arena = new Arena(new Label(), new AnchorPane());
        Image img = new Image("/unicorn.png");
        ImageView iv = new ImageView(img);
        Coordinates destination = new Coordinates(440,0);
        PriorityQueue<Monster> testList = new PriorityQueue<>();
        LinkedList<Monster> selectList = new LinkedList<>();
        LinkedList<Monster> answerList = new LinkedList<>();
        Coordinates target;

        //test shooting range valid
        Catapult catapult = new Catapult(arena, new Coordinates(300,300));
        Monster m1 = new Unicorn(arena, new Coordinates(100, 100), destination, iv, 1);testList.add(m1);
        Monster m2 = new Unicorn(arena, new Coordinates(101, 101), destination, iv, 1);testList.add(m2);
        Monster m3 = new Unicorn(arena, new Coordinates(400, 10), destination, iv, 1);testList.add(m3);
        target=catapult.selectMonster(testList,selectList);
        answerList.add(m3);
        Assert.assertEquals(selectList,answerList);
        for (Monster m :selectList) {
            Assert.assertTrue(target.isInCircle(m,25));
        }
        selectList.clear();

        //test shoot monster with nearest but not most monsters
        Monster m4 = new Unicorn(arena, new Coordinates(201, 201), destination, iv, 1);testList.add(m4);
        Monster m5 = new Unicorn(arena, new Coordinates(200, 200), destination, iv, 1);testList.add(m5);
        target=catapult.selectMonster(testList,selectList);
        Assert.assertEquals(selectList,answerList);
        for (Monster m :selectList) {
            Assert.assertTrue(target.isInCircle(m,25));
        }
        selectList.clear();

        //test shoot monster with nearest and most monster
        Monster m6 = new Unicorn(arena, new Coordinates(401, 11), destination, iv, 1);testList.add(m6);
        Monster m7 = new Unicorn(arena, new Coordinates(440, 40), destination, iv, 1);testList.add(m7);
        target=catapult.selectMonster(testList,selectList);
        answerList.add(m6);
        Assert.assertEquals(selectList,answerList);
        for (Monster m :selectList) {
            Assert.assertTrue(target.isInCircle(m,25));
        }
        selectList.clear();

    }
}
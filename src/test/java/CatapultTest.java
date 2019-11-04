import org.junit.Assert;
import org.junit.Test;
import project.Coordinates;
import project.monsters.Monster;
import project.monsters.Unicorn;
import project.towers.Catapult;

import java.util.LinkedList;
import java.util.Random;

import static org.junit.Assert.*;

public class CatapultTest {

    @Test
    public void selectMonster() {
        Coordinates destination = new Coordinates(440,0);
        LinkedList<Monster> testList = new LinkedList<>();
        LinkedList<Monster> selectList = new LinkedList<>();
        LinkedList<Monster> answerList = new LinkedList<>();
        Coordinates target;
        //test shooting range valid
        Catapult catapult = new Catapult(new Coordinates(300,300));
        Monster m1 = new Unicorn(1,new Coordinates(100,100),destination);testList.add(m1);
        Monster m2 = new Unicorn(1,new Coordinates(101,101),destination);testList.add(m2);
        Monster m3 = new Unicorn(1,new Coordinates(400,10),destination);testList.add(m3);
        target=catapult.selectMonster(testList,selectList);
        answerList.add(m3);
        Assert.assertEquals(selectList,answerList);

        //test shoot most monster with nearest
        Monster m4 = new Unicorn(1,new Coordinates(401,10),destination);testList.add(m4);
        Monster m5 = new Unicorn(1,new Coordinates(200,200),destination);testList.add(m5);
        target=catapult.selectMonster(testList,selectList);
        answerList.add(m4);
        Assert.assertEquals(selectList,answerList);


    }
}
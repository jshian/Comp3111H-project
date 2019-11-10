import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;

import org.testfx.framework.junit.ApplicationTest;
import project.*;
import project.monsters.*;
import project.towers.Catapult;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;
import sample.MyController;

import static org.junit.Assert.*;

public class CatapultTest extends ApplicationTest {

	private Scene s;

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Tower Defence");
        s = new Scene(root, 600, 480);
        primaryStage.setScene(s);
        primaryStage.show();
        MyController appController = loader.getController();
        appController.createArena();
	}

    @Test
    public void selectMonster() {
        Arena arena = new Arena(new Label(), new AnchorPane());
        Image img = new Image("/unicorn.png");
        ImageView iv = new ImageView(img);
        Coordinates destination = new Coordinates(440,0);
        PriorityQueue<Monster> testList = new PriorityQueue<>();
        LinkedList<Arena.ExistsInArena> selectList = new LinkedList<>();
        LinkedList<Monster> answerList = new LinkedList<>();
        Coordinates target;

        //test shooting range valid
        Catapult catapult = new Catapult(arena, new Coordinates(300,0));
        Monster m1 = new Unicorn(arena, new Coordinates(100, 100), destination, iv, 1);testList.add(m1);arena.addObject(m1);
        Monster m2 = new Unicorn(arena, new Coordinates(101, 101), destination, iv, 1);testList.add(m2);arena.addObject(m2);
        Monster m3 = new Unicorn(arena, new Coordinates(400, 0), destination, iv, 1);testList.add(m3);arena.addObject(m3);
        target=catapult.selectMonster(testList,selectList);
        if(selectList.size()==0) System.out.println("wtf");
       // selectList = arena.findObjectsInRange(target, 25, EnumSet.of(Arena.TypeFilter.Monster));
        answerList.add(m3);
        Assert.assertEquals(selectList,answerList);
        for (Arena.ExistsInArena m :selectList) {
            Assert.assertTrue(Geometry.isInCircle( m.getX(), m.getY(), target.getX(), target.getY(),25));
        }
        selectList.clear();

        //test shoot monster with nearest but not most monsters
        Monster m4 = new Unicorn(arena, new Coordinates(201, 100), destination, iv, 1);testList.add(m4);arena.addObject(m4);
        Monster m5 = new Unicorn(arena, new Coordinates(200, 100), destination, iv, 1);testList.add(m5);arena.addObject(m5);
        target=catapult.selectMonster(testList,selectList);
        //selectList = arena.findObjectsInRange(target, 25, EnumSet.of(Arena.TypeFilter.Monster));
        Assert.assertEquals(selectList,answerList);
        for (Arena.ExistsInArena m :selectList) {
            Assert.assertTrue(Geometry.isInCircle( m.getX(), m.getY(), target.getX(), target.getY(),25));
        }
        selectList.clear();

        //test shoot monster with same nearest but most monster
        Monster m6 = new Unicorn(arena, new Coordinates(401, 0), destination, iv, 1);testList.add(m6);arena.addObject(m6);
        Monster m7 = new Unicorn(arena, new Coordinates(440, 40), destination, iv, 1);testList.add(m7);arena.addObject(m7);
        target=catapult.selectMonster(testList,selectList);
       // selectList = arena.findObjectsInRange(target, 25, EnumSet.of(Arena.TypeFilter.Monster));
        answerList.add(m6);
        Assert.assertEquals(selectList,answerList);
        for (Arena.ExistsInArena m :selectList) {
            Assert.assertTrue(Geometry.isInCircle(m.getX(), m.getY(), target.getX(), target.getY(), 25));
        }
        selectList.clear();

    }
}
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import project.Geometry;
import project.UIController;
import project.arena.*;
import project.arena.monsters.*;
import project.arena.towers.*;

import java.util.LinkedList;
import java.util.PriorityQueue;

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
        UIController appController = loader.getController();
        appController.createArena();
	}

    @Test
    public void selectMonster() {
        Arena arena = new Arena(new Label(), new AnchorPane());
        Image img = new Image("/unicorn.png");
        Coordinates destination = new Coordinates((short) 470, (short) 10);
        PriorityQueue<Monster> testList = new PriorityQueue<>();
        LinkedList<ExistsInArena> selectList = new LinkedList<>();
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
        for (ExistsInArena m :selectList) {
            Assert.assertTrue(Geometry.isInCircle( m.getX(), m.getY(), target.getX(), target.getY(),25));
        }
        selectList.clear();

        //test shoot monster with nearest but not most monsters
        Monster m4 = new Unicorn(arena, new Coordinates((short) 330, (short) 10), destination, new ImageView(img), 1);testList.add(m4);arena.addObject(m4);
        Monster m5 = new Unicorn(arena, new Coordinates((short) 330, (short) 10), destination, new ImageView(img), 1);testList.add(m5);arena.addObject(m5);
        target=catapult.selectMonster(testList,selectList);
        Assert.assertEquals(selectList,answerList);
        for (ExistsInArena m :selectList) {
            Assert.assertTrue(Geometry.isInCircle( m.getX(), m.getY(), target.getX(), target.getY(),25));
        }
        selectList.clear();

        //test shoot monster with same nearest but most monster
        Monster m6 = new Unicorn(arena, new Coordinates((short) 430, (short) 10), destination, new ImageView(img), 1);testList.add(m6);arena.addObject(m6);
        Monster m7 = new Unicorn(arena, new Coordinates((short) 470, (short) 50), destination, new ImageView(img), 1);testList.add(m7);arena.addObject(m7);
        target=catapult.selectMonster(testList,selectList);
        answerList.add(m6);
        Assert.assertEquals(selectList,answerList);
        for (ExistsInArena m :selectList) {
            Assert.assertTrue(Geometry.isInCircle(m.getX(), m.getY(), target.getX(), target.getY(), 25));
        }
        selectList.clear();

    }
}
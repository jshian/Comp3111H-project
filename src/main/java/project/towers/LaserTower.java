package project.towers;

import javafx.scene.image.ImageView;
import project.*;
import project.monsters.Monster;

import java.util.LinkedList;

public class LaserTower extends Tower{
    // State
    private int consume;

    /**
     * Constructor for LaserTower class.
     */
    public LaserTower(Coordinates coordinate){
        super(coordinate);
        this.attackPower = 30;
        this.buildingCost = 20;
        this.shootingRange = 50;
        this.consume = 2;
    }

    public LaserTower(Coordinates coordinates, ImageView imageView) {
        super(coordinates, imageView);
        this.attackPower = 30;
        this.buildingCost = 20;
        this.consume = 2;
    }

    /**
     * Laser tower consume player's resource to attack monster.
     * @param player The player who build the tower.
     */
    public void consumeResource(Player player){
        player.spendResources(consume);
    }

    @Override
    public void attackMonster(Monster monster){

        Coordinates currentPt = new Coordinates(getX(), getY());
        Coordinates edgePt = currentPt.findEdgePt(monster);
        currentPt.drawLine(edgePt);
        int tX = getX();
        int tY = getY();
        int mX = monster.getX();
        int mY = monster.getY();

        LinkedList<Monster> monsters = Arena.getMonsters();
        for (Monster m:monsters) {
            for (int x = tX, y = tY; x> mX && y> mY; x+=(mX - tX)*0.01,y+=(mY - tY)*0.01)
                if ((new Coordinates(x,y)).isInCircle(m,3))
                    m.setHealth((int)(m.getHealth()-this.attackPower));
        }
    }

    @Override
    public boolean upgrade(int resource){
        if(resource >= 10){
            this.attackPower+=5;
            return true;
        }
        return false;
    }
}

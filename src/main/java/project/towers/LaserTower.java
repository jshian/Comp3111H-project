package project.towers;

import javafx.scene.image.ImageView;
import project.*;
import project.monsters.Monster;

import java.util.LinkedList;

public class LaserTower extends Tower{
    // State
    private int consume;

    /**
     * Default constructor for LaserTower class.
     */
    public LaserTower(){
        super();
        this.attackPower = 30;
        this.buildingCost = 20;
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
        Coordinates edgePt = this.coordinates.findEdgePt(monster);
        edgePt.drawLine(this);
        LinkedList<Monster> monsters = Arena.getMonsters();
        for (Monster m:monsters) {
            if(this.coordinates.isInArea(monster,m)){
                m.setHealth((int)(monster.getHealth()-this.attackPower));
            }
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

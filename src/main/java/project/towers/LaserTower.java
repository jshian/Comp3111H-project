package project.towers;

import project.*;
import project.monsters.Monster;

import java.util.LinkedList;

public class LaserTower extends Tower{
    // State
    private int consume;

    /**
     * Constructor for LaserTower class.
     * @param arena The arena in which the lazer tower exists.
     */
    public LaserTower(Arena arena){
        super(arena);
        this.attackPower = 30;
        this.buildingCost = 20;
        this.consume = 2;
    }


    /**
     * Lazer tower consume player's resource to attack monster.
     * @param player The player who build the tower.
     */
    public void consumeResource(Player player){
        player.decreaseRecource(consume);
    }

    @Override
    public void attackMonster(Monster monster){
        Coordinates mCor = monster.getCoordinates();
        Coordinates tCor = getCoordinates();
        Coordinates edgePt = tCor.findEdgePt(mCor);
        tCor.drawLine(edgePt);
        LinkedList<Monster> monsters = monster.getCoordinates().getArena().getMonsters();
        for (Monster m:monsters) {
            if(tCor.isInArea(mCor,m.getCoordinates())){
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

package project.towers;

import project.*;

public class LaserTower extends Tower{
    // State
    private int consume;

    /**
     * Constructor for LaserTower class.
     * @param arena The arena in which the lazer tower exists.
     */
    public LaserTower(Arena arena){
        super(arena);
        super.attackPower = 30;
        super.buildingCost = 20;
        this.consume = 2;
    }

    /**
     * Draw a line from the lazer tower to certain position.
     * @param cor The coordinate of the target.
     */
    public void drawLine(Coordinates cor){

    }

    /**
     * Lazer tower consume player's resource to attack monster.
     * @param resource The resources that owned by player.
     */
    public void consumeResource(int resource){
        resource-=consume;
    }

    /**
     * Destroy the towers which the lazer has passed through.
     * @param cor The coordinate of the target.
     */
    public void destroyTower(Coordinates cor){

    }

    @Override
    public boolean upgrade(int resource){
        if(resource >= 10){
            attackPower+=5;
            return true;
        }
        return false;
    }
}

package project.towers;

import project.*;

public class IceTower extends BasicTower{
    // State
    private int slowDown;

    /**
     * Constructor for IceTower class.
     * @param arena The arena in which the ice tower exists.
     */
    public IceTower(Arena arena){
        super(arena);
        super.buildingCost=20;
        this.slowDown = 10;
    }

    @Override
    public boolean upgrade(int resource){
        if(resource >= 20){
            slowDown+=5;
            return true;
        }
        return false;
    }
}

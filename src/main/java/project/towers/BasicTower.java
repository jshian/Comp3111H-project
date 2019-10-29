package project.towers;

import org.apache.commons.lang3.NotImplementedException;

import project.*;

public class BasicTower implements Arena.ExistsInArena {
    // Position
    private Coordinates coordinates;

    // States
    protected int attackPower, buildingCost, shootingRange;

    protected BasicTower(){

    }

    /**
     * Constructor for BasicTower class.
     * @param arena The arena in which the basic tower exists.
     */
    public BasicTower(Arena arena){
        this.attackPower = 10;
        this.buildingCost = 10;
        this.shootingRange = 65;
        this.coordinates = new Coordinates(arena);
    }

    // Inferface implementation
    public Runnable refreshDisplay() { throw new NotImplementedException("TODO"); }
    public Coordinates getCoordinates() { return coordinates; }

    /**
     * Upgrade the tower.
     * @param resource The resources needed for tower to upgrade.
     * @return ture if upgrade is successful, otherwise false.
     */
    public boolean upgrade(int resource){
        if(resource >= 10){
            attackPower+=5;
            return true;
        }
        return false;
    }

    public boolean canShoot(Coordinates monster){
        return coordinates.distanceFrom(monster) <= shootingRange * shootingRange;
    }

    /**
     * Accesses the attack power of tower.
     * @return The attack power of tower.
     */
    public int getAttackPower() {
        return attackPower;
    }

    /**
     * Accesses the building cost of tower.
     * @return The building cost of tower.
     */
    public int getBuildingCost() {
        return buildingCost;
    }

    /**Accesses the shooting range of tower.
     * @return The shooting range of tower.
     */
    public int getShootingRange() {
        return shootingRange;
    }



}

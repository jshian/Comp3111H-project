package project.towers;

import org.apache.commons.lang3.NotImplementedException;
import project.*;
import project.monsters.Monster;

public abstract class Tower implements Arena.ExistsInArena {
    // Position
    private Coordinates coordinates;

    // States
    protected int attackPower, buildingCost, shootingRange;

    /**
     * Constructor for Tower class.
     * @param arena The arena in which the basic tower exists.
     */
    public Tower(Arena arena){
        this.coordinates = new Coordinates(arena);
    }

    // Inferface implementation
    public void refreshDisplay() { throw new NotImplementedException("TODO"); }
    public Coordinates getCoordinates() { return coordinates; }


    /**
     * Upgrade the tower for adding the power, slow duration, reload time etc
     * @param resource The resources needed for tower to upgrade.
     * @return True if upgrade is successful, otherwise false.
     */
    public abstract boolean upgrade(int resource);

    /**
     * Decrease the health, speed etc of the attacked monster
     * @param monster The monster closest to the destination was attacked
     */
    public abstract void attackMonster(Monster monster);
    /**
     * @param monster the coordinate of monster who to be shoot
     * @return True if it is in the shooting range otherwise false
     */
    public boolean canShoot(Coordinates monster){
        return coordinates.diagonalDistanceFrom(monster) <= shootingRange * shootingRange;
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

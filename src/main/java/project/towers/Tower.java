package project.towers;

import project.Arena.ExistsInArena;

import org.apache.commons.lang3.NotImplementedException;

import javafx.scene.image.ImageView;
import project.*;
import project.monsters.Monster;

public abstract class Tower implements ExistsInArena {
    // UI
    private ImageView imageView;
    
    // Position
    protected Coordinates coordinates;

    // States
    protected int attackPower, buildingCost, shootingRange;

    /**
     * Constructor for Tower class.
     * @param coordinate The coordinate of tower.
     */
    public Tower(Coordinates coordinate){
        this.coordinates = coordinate;
    }

    // Inferface implementation
    public ImageView getImageView() { return imageView; }
    public Coordinates getCoordinates() { return coordinates; }
    public void refreshDisplay() { throw new NotImplementedException("TODO"); }


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
    protected abstract void attackMonster(Monster monster);
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

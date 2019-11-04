package project.towers;

import project.Arena.ExistsInArena;

import org.apache.commons.lang3.NotImplementedException;

import javafx.scene.image.ImageView;
import project.*;
import project.monsters.Monster;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

public abstract class Tower implements ExistsInArena {
    /**
     * The ImageView that displays the monster.
     */
    @Transient
    protected ImageView imageView;

    /**
     * Represents the position of the monster.
     */
    @NotNull
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

    public Tower(Coordinates coordinates, ImageView imageView) {
        this.coordinates = coordinates;
        this.imageView = imageView;
    }

    // Inferface implementation
    public ImageView getImageView() { return imageView; }
    public int getX() { return coordinates.getX(); }
    public int getY() { return coordinates.getY(); }
    public void refreshDisplay() { throw new NotImplementedException("TODO"); }
    public void setLocation(int x, int y) { coordinates = new Coordinates(x, y); }

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
     * @param monster the monster who to be shoot
     * @return True if it is in the shooting range otherwise false
     */
    public boolean canShoot(Monster monster){
        return coordinates.diagonalDistanceFrom(monster) <= shootingRange ;
    }

    /**
     * @param coordinate the coordinate that to be shoot
     * @return True if it is in the shooting range otherwise false
     */
    public boolean canShoot(Coordinates coordinate){
        return coordinates.diagonalDistanceFrom(coordinate) <= shootingRange ;
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

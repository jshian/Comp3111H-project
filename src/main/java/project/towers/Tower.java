package project.towers;

import project.Arena.ExistsInArena;
import org.apache.commons.lang3.NotImplementedException;
import javafx.scene.image.ImageView;
import project.*;
import project.monsters.Monster;
import project.projectiles.Projectile;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * Towers is added by player to stop monster moving to the end zone.
 */
public abstract class Tower implements ExistsInArena {
    /**
     * ID for storage using Java Persistence API
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    /**
     * The ImageView that displays the monster.
     */
    @Transient
    protected ImageView imageView;

    /**
     * Represents the position of the tower.
     */
    @NotNull
    protected Coordinates coordinates;

    /**
     * The maximum attack power of the tower.
     */
    @NotNull
    protected int maxAttackPower;

    /**
     * The current attack power of the tower. It cannot go beyond {@link #maxAttackPower}.
     */
    @NotNull
    protected int attackPower;

    /**
     * The maximum building cost of the tower.
     */
    @NotNull
    protected  int maxBuildingCost;

    /**
     * The current building cost of the tower. It cannot go beyond {@link #maxBuildingCost}.
     */
    @NotNull
    protected  int buildingCost;

    /**
     * The maximum shooting range of the tower.
     */
    @NotNull
    protected  int maxShootingRange;

    /**
     * The current shooting range of the tower. It cannot go beyond {@link #maxShootingRange}.
     */
    @NotNull
    protected int shootingRange;

    /**
     * The current shooting limit of the tower. It cannot go beyond {@link #maxShootingRange}.
     */
    @NotNull
    protected int shootLimit;

    /**
     * The attack speed of tower for how many px per frame
     */
    @NotNull
    protected int attackSpeed;

    /**
     * The reload time for tower after it attack monsters.
     */
    protected int reload;

    /**
     * The counter used to count the reload time.
     */
    protected int counter;

    /**
     * Constructor for Tower class.
     * @param coordinates The coordinate of tower.
     */
    public Tower(Coordinates coordinates){
        this.coordinates = coordinates;
        this.reload = 2;
    }

    /**
     * Constructor for Tower class.
     * @param coordinates The coordinate of tower.
     * @param imageView The image view of the tower.
     */
    public Tower(Coordinates coordinates, ImageView imageView) {
        this.coordinates = coordinates;
        this.imageView = imageView;
        this.reload=2;
    }

    // Inferface implementation
    public ImageView getImageView() { return imageView; }
    public int getX() { return coordinates.getX(); }
    public int getY() { return coordinates.getY(); }
    public void refreshDisplay() { throw new NotImplementedException("TODO"); }
    public void setLocation(int x, int y) { coordinates = new Coordinates(x, y); }

    /**
     * Upgrade the tower by adding the power, slow duration, reload time etc.
     * @param resource The resources needed for tower to upgrade.
     * @return True if upgrade is successful, otherwise false.
     */
    public abstract boolean upgrade(int resource);

    /**
     * Attack the monster closest to destination and in shooting range.
     * @return The projectile of tower attack, return null if cannot shoot any monster.
     */
    protected abstract Projectile attackMonster();

    /**
     * To determine whether the monster is in shooting range or not.
     * @param monster the monster who to be shoot.
     * @return True if it is in the shooting range otherwise false.
     */
    public boolean canShoot(Monster monster){
        return coordinates.diagonalDistanceFrom(monster) <= shootingRange ;
    }

    /**
     * To determine whether the coordinate is in the shooting range or not.
     * @param coordinate the coordinate that to be shoot.
     * @return True if it is in the shooting range otherwise false.
     */
    public boolean canShoot(Coordinates coordinate){
        return this.coordinates.diagonalDistanceFrom(coordinate) <= shootingRange ;
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


    public boolean isReload(){
        if(this.counter==0){
            this.counter=this.reload;
            return false;
        }else this.counter--;
        return true;
    }

    /**Accesses the information of tower.
     * @return the information of tower.
     */
    public abstract String getInformation();


}

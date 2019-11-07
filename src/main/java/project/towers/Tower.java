package project.towers;

import project.Arena.ExistsInArena;
import org.apache.commons.lang3.NotImplementedException;
import org.checkerframework.checker.nullness.qual.NonNull;

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
     * The Arena that this tower is attached to.
     */
    @Transient
    protected final Arena arena;

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
    protected int maxBuildingCost;

    /**
     * The current building cost of the tower. It cannot go beyond {@link #maxBuildingCost}.
     */
    @NotNull
    protected int buildingCost;

    /**
     * The maximum shooting range of the tower.
     */
    @NotNull
    protected int maxShootingRange;

    /**
     * The current shooting range of the tower. It cannot go beyond {@link #maxShootingRange}.
     */
    @NotNull
    protected int shootingRange;

    /**
     * The current shooting limit of the tower. It cannot go beyond {@link #maxShootingRange}.
     */
    @NotNull
    protected int shootLimit = 0;

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
     * The resources needed to upgrade the tower
     */
    protected int upgradeCost;

    /**
     * Does the tower attacked before the second attack.
     */
    protected boolean hasAttack;

    /**
     * Constructor for Tower class.
     * @param coordinates The coordinates of the tower.
     */
    public Tower(Arena arena, Coordinates coordinates){
        this.arena = arena;
        this.coordinates = coordinates;
        this.reload = 2;
        this.counter = 0;
    }

    /**
     * Constructor for Tower class.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinates of the tower.
     * @param imageView The image view of the tower.
     */
    public Tower(Arena arena, Coordinates coordinates, ImageView imageView) {
        this.arena = arena;
        this.coordinates = coordinates;
        this.imageView = imageView;
        this.coordinates.bindByImage(this.imageView);
        this.reload = 2;
        this.counter = 0;
    }

    // Interface implementation
    public ImageView getImageView() { return imageView; }
    public int getX() { return coordinates.getX(); }
    public int getY() { return coordinates.getY(); }
    public int getShootLimit() { return shootLimit; }
    public int getUpgradeCost() { return upgradeCost; }
    public void setLocation(int x, int y) { coordinates = new Coordinates(x, y); }
    public void setLocation(@NonNull Coordinates coordinates) { this.coordinates = new Coordinates(coordinates); }

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
        return Geometry.findEuclideanDistance(this.getX(), this.getY(), monster.getX(), monster.getY()) <= shootingRange;
    }

    /**
     * To determine whether the coordinates is in the shooting range or not.
     * @param coordinate the coordinates that to be shoot.
     * @return True if it is in the shooting range otherwise false.
     */
    public boolean canShoot(Coordinates coordinates){
        return Geometry.findEuclideanDistance(this.getX(), this.getY(), coordinates.getX(), coordinates.getY()) <= shootingRange;
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

    /**
     * Tower has the reload time to do next attack
     * @return whether the tower is reloading or not.
     */
    public boolean isReload(){
        if(hasAttack){
            if(this.counter==0){
                this.counter=this.reload;
                this.hasAttack = false;
                return false;
            }else this.counter--;
            return true;
        }
        return false;
    }

    /**Accesses the information of tower.
     * @return the information of tower.
     */
    public abstract String getInformation();


}

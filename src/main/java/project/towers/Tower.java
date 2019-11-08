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
    protected int maxAttackPower = 100;

    /**
     * The current attack power of the tower. It cannot go beyond {@link #maxAttackPower}.
     */
    @NotNull
    protected int attackPower;

    /**
     * The current building cost of the tower.
     */
    @NotNull
    protected int buildingCost;

    /**
     * The maximum shooting range of the tower.
     */
    @NotNull
    protected int maxShootingRange;

    /**
     * The current shooting limit of the tower. It cannot go beyond {@link #maxShootingRange}.
     */
    @NotNull
    protected int minShootingRange = 0;

    /**
     * The attack speed of tower for how many px per frame
     */
    @NotNull
    protected int attackSpeed = 5;

    /**
     * The reload time for tower after it attack monsters.
     */
    protected int reload = 5;

    /**
     * The counter used to count the reload time.
     */
    protected int counter = 0;

    /**
     * The resources needed to upgrade the tower
     */
    protected int upgradeCost = 10;

    /**
     * Does the tower attacked before the second attack.
     */
    protected boolean hasAttack = false;

    /**
     * Constructor for Tower class.
     * @param coordinates The coordinates of the tower.
     */
    public Tower(Arena arena, Coordinates coordinates){
        this.arena = arena;
        this.coordinates = coordinates;
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
    }

    // Interface implementation
    public ImageView getImageView() { return imageView; }
    public int getX() { return coordinates.getX(); }
    public int getY() { return coordinates.getY(); }
    public void setLocation(int x, int y) { this.coordinates.update(x, y); }
    public void setLocation(@NonNull Coordinates coordinates) { this.coordinates.update(coordinates); }

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
    public abstract Projectile attackMonster();

    /**
     * To determine whether the monster is in shooting range or not.
     * @param monster the monster who to be shoot.
     * @return True if it is in the shooting range otherwise false.
     */
    public boolean canShoot(Monster monster){
        double euclideanDistance = Geometry.findEuclideanDistance(getX(), getY(), monster.getX(), monster.getY());
        return euclideanDistance <= maxShootingRange && euclideanDistance>=minShootingRange;
    }

    /**
     * To determine whether the coordinates is in the shooting range or not.
     * @param coordinate the coordinates that to be shoot.
     * @return True if it is in the shooting range otherwise false.
     */
    public boolean canShoot(Coordinates coordinate){
        double euclideanDistance = Geometry.findEuclideanDistance(getX(), getY(), coordinate.getX(), coordinate.getY());
        return euclideanDistance <= maxShootingRange && euclideanDistance>=minShootingRange;
    }

    /**
     * Accesses the attack power of the tower.
     * @return The attack power of the tower.
     */
    public int getAttackPower() {
        return attackPower;
    }

    /**
     * Accesses the building cost of the tower.
     * @return The building cost of the tower.
     */
    public int getBuildingCost() {
        return buildingCost;
    }

    /**
     * Accesses the maximum shooting range of the tower.
     * @return The maximum shooting range of the tower.
     */
    public int getMaxShootingRange() {
        return maxShootingRange;
    }

    /**
     * Accesses the minimum shooting range of the tower.
     * @return The minimum shooting range of the tower.
     */
    public int getMinShootingRange() {
        return minShootingRange;
    }


    /**
     * Accesses the attack speed of the tower.
     * @return The attack speed of the tower.
     */
    public int getAttackSpeed() {
        return attackSpeed;
    }

    /**
     * Accesses the reload time of the tower
     * @return The reload time of the tower.
     */
    public int getReload() {
        return reload;
    }

    /**
     * Accesses the upgrade cost of the tower.
     * @return The upgrade cost of the tower.
     */
    public int getUpgradeCost() {
        return upgradeCost;
    }

    /**
     * Tower has the reload time to do next attack
     * @return whether the tower is reloading or not.
     */
    public boolean isReload(){
        if(hasAttack){
            if(this.counter==0){
                this.counter = this.reload;
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

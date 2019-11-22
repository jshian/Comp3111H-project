package project.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.Geometry;
import project.Player;
import project.arena.Arena;

import java.util.LinkedList;

/**
 * Towers is added by player to stop monster moving to the end zone.
 */
@Entity
public abstract class Tower extends ArenaObject {
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
    protected ImageView imageView = null;

    /**
     * The Arena that this tower is attached to.
     */
    @NotNull
    @ManyToOne
    protected final Arena arena;

    /**
     * Represents the position of the tower.
     */
    @NotNull
    @OneToOne
    protected Coordinates coordinates;

    /**
     * The maximum attack power of the tower.
     */
    protected final int maxAttackPower = 100;

    /**
     * The current attack power of the tower. It cannot go beyond {@link #maxAttackPower}.
     */
    protected int attackPower = 1;

    /**
     * The cumulative building cost of the tower, which increases as the tower is upgraded.
     */
    protected int buildValue = findInitialBuildingCost();

    /**
     * Finds the initial building cost of the tower.
     * @return The initial building cost of the tower.
     */
    public static int findInitialBuildingCost() {
        return 1;
    }

    /**
     * The maximum shooting range of the tower.
     */
    protected short maxShootingRange = 1;

    /**
     * The current shooting limit of the tower. It cannot go beyond {@link #maxShootingRange}.
     */
    protected short minShootingRange = 0;

    /**
     * The speed of projectiles shot by the tower for how many px per frame
     */
    protected int projectileSpeed = 5;

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
        this.coordinates = new Coordinates(coordinates);
        this.imageView = imageView;

        this.coordinates.bindByImage(this.imageView);
    }

    /**
     * Constructor for making a copy of Tower class that is linked to another arena.
     * @param arena The arena to link this object to.
     * @param other The object to copy from.
     */
    public Tower(Arena arena, Tower other) {
        this.imageView = new ImageView(other.imageView.getImage());
        this.arena = arena;
        this.coordinates = new Coordinates(other.coordinates);
        this.attackPower = other.attackPower;
        this.buildValue = other.buildValue;
        this.maxShootingRange = other.maxShootingRange;
        this.minShootingRange = other.minShootingRange;
        this.projectileSpeed = other.projectileSpeed;
        this.reload = other.reload;
        this.counter = other.counter;
        this.upgradeCost = other.upgradeCost;
        this.hasAttack = other.hasAttack;

        this.coordinates.bindByImage(this.imageView);
    }

    /**
     * Creates a deep copy of the tower that is linked to another arena.
     * @param arena The arena to link this object to.
     * @return A deep copy of the tower.
     */
    public abstract Tower deepCopy(Arena arena);

    // Interface implementation
    /**
     * {@inheritDoc}
     */
    @Override
    public final ImageView getImageView() { return imageView; }

    /**
     * {@inheritDoc}
     */
    @Override
    public final short getX() { return coordinates.getX(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final short getY() { return coordinates.getY(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setLocation(short x, short y) { this.coordinates.update(x, y); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setLocation(Coordinates coordinates) { this.coordinates.update(coordinates); }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nextFrame() {
        if (hasAttack) {
            if (this.counter == 0) {
                this.hasAttack = false;
            } else {
                this.counter--;
            }
        }
    }

    /**
     * Gets the class name of the tower.
     * @return The class name of the tower.
     */
    protected abstract String getClassName();

    /**
     * Determines whether the tower can be upgraded.
     * @param player The player who upgrades the tower.
     * @return Whether the tower can be upgraded.
     */
    public boolean canUpgrade(Player player) {
        return player.hasResources(upgradeCost);
    }

    /**
     * Attempts to upgrade the tower.
     * @param player The player who upgrades the tower.
     * @return Whether the upgrade is successful.
     */
    public final boolean tryUpgrade(Player player) {
        if (canUpgrade(player)) {
            System.out.println(String.format("%s is being upgraded", getClassName()));
            player.spendResources(upgradeCost);
            upgrade();
            return true;
        }
        System.out.println(String.format("not enough resource to upgrade %s", getClassName()));
        return false;
    }

    /**
     * Upgrades the tower.
     */
    protected void upgrade() {
        buildValue += upgradeCost;
    }

    /**
     * Generates a projectile that attacks the target of the tower.
     * @return The projectile that attacks the target of the tower, or <code>null</code> if either there is no valid target or the tower is reloading.
     */
    public abstract @Nullable Projectile generateProjectile();

    /**
     * Determines whether the specified monster is a valid target to be shot at.
     * @param monster The monster.
     * @return Whether the monster is a valid target to be shot at.
     */
    protected boolean isValidTarget(Monster monster) {
        if (monster.hasDied()) return false;
        LinkedList<Coordinates> prevCoordinates = monster.getPrevCoordinates();
        if (prevCoordinates.size() == 0)
            if (isInRange(new Coordinates(monster.getX(),monster.getY()))) return true;
        for (Coordinates c : prevCoordinates) {
            if (isInRange(c)) return true;
        }
        
        return false;
    }

    /**
     * Determines whether the specified pixel is in shooting range.
     * @param coordinates The coordinates of the pixel.
     * @return Whether the specified pixel is in shooting range.
     */
    protected final boolean isInRange(Coordinates coordinates) {
        double euclideanDistance = Geometry.findEuclideanDistance(getX(), getY(), coordinates.getX(), coordinates.getY());
        return euclideanDistance <= maxShootingRange && euclideanDistance >= minShootingRange;
    }

    /**
     * Accesses the attack power of the tower.
     * @return The attack power of the tower.
     */
    public final int getAttackPower() { return attackPower; }

    /**
     * Accesses the cumulative building cost of the tower.
     * @return The cumulative building cost of the tower.
     */
    public final int getBuildValue() { return buildValue; }

    /**
     * Accesses the maximum shooting range of the tower.
     * @return The maximum shooting range of the tower.
     */
    public final short getMaxShootingRange() { return maxShootingRange; }

    /**
     * Accesses the minimum shooting range of the tower.
     * @return The minimum shooting range of the tower.
     */
    public final short getMinShootingRange() { return minShootingRange; }

    /**
     * Accesses the projectile speed of the tower.
     * @return The projectile speed of the tower.
     */
    public final int getProjectileSpeed() { return projectileSpeed; }

    /**
     * Accesses the reload time of the tower.
     * @return The reload time of the tower.
     */
    public final int getReload() { return reload; }

    /**
     * Accesses the upgrade cost of the tower.
     * @return The upgrade cost of the tower.
     */
    public final int getUpgradeCost() { return upgradeCost; }

    /**
     * Tower has the reload time to do next attack.
     * @return whether the tower is reloading or not.
     */
    public final boolean isReload() { return counter > 0; }

    /**
     * Accesses the information of tower.
     * @return the information of tower.
     */
    public String getInformation() {
        return String.format("Attack Power: %d\nReload Time: %d\nRange: [%d , %d]\nUpgrade Cost: %d\nBuild Value: %d", this.attackPower,
            this.reload,this.minShootingRange,this.maxShootingRange,this.upgradeCost,this.buildValue);
    }
    
}

package project.entity;

import java.util.PriorityQueue;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import project.arena.ArenaEventRegister;
import project.control.ArenaManager;
import project.event.eventargs.ArenaTowerEventArgs;
import project.query.ArenaObjectRingSortedSelector;
import project.query.ArenaObjectStorage.SortOption;
import project.query.ArenaObjectStorage.StoredComparableType;
import project.util.Geometry;

/**
 * Towers is added by player to stop monster moving to the end zone.
 */
@Entity
public abstract class Tower extends ArenaObject implements InformativeObject {
    /**
     * ID for storage using Java Persistence API
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    /**
     * The maximum attack power of the tower.
     */
    protected final int maxAttackPower = 100;

    /**
     * The current attack power of the tower. It cannot go beyond {@link #maxAttackPower}.
     */
    protected int attackPower = 1;

    /**
     * The minimum shooting range of the tower.
     */
    protected short minRange = 0;

    /**
     * The maximum shooting range of the tower.
     */
    protected short maxRange = 1;

    /**
     * The speed of projectiles shot by the tower for how many px per frame
     */
    protected int projectileSpeed = 5;

    /**
     * The reload time for tower after it attack monsters.
     */
    protected int reload = 5;

    /**
     * The minimum allowable reload time for tower.
     */
    protected int minReload = 2;

    /**
     * The counter used to count the reload time.
     */
    protected int counter = 0;

    /**
     * The cumulative building cost of the tower, which increases as the tower is upgraded.
     */
    protected int buildValue;

    /**
     * Returns the initial building cost of the tower.
     * @return The initial building cost of the tower.
     */
    public static int getBuildingCost() {
        return 1;
    }

    /**
     * The resources needed to upgrade the tower
     */
    protected int upgradeCost = 10;

    // Define onNextFrame before constructor
    {
        onNextFrame = (sender, args) -> {
            ArenaObjectRingSortedSelector<Monster> selector = new ArenaObjectRingSortedSelector<>(getX(), getY(), minRange, maxRange);
            PriorityQueue<Monster> validTargets = storage.getSortedQueryResult(selector, StoredComparableType.MONSTER, SortOption.ASCENDING);

            if (!validTargets.isEmpty()) {
                if (counter <= 0) {
                    shoot(validTargets);
                    counter = reload;
                }
                
                counter--;
            }
        };
    }

    /**
     * Constructs a newly allocated {@link Tower} object.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     */
    public Tower(short x, short y) {
        super(x, y);

        buildValue = getBuildingCost();
    }

    /**
     * Returns the current attack power of the tower.
     * @return The current attack power of the tower.
     */
    public int getAttackPower() { return attackPower; }

    /**
     * Returns the minimum shooting range of the tower.
     * @return The minimum shooting range of the tower.
     */
    public short getMinRange() { return minRange; }

    /**
     * Returns the maximum shooting range of the tower.
     * @return The maximum shooting range of the tower.
     */
    public short getMaxRange() { return maxRange; }

    /**
     * Returns whether a specified point is within range of the tower.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @return Whether the specified point is within range of the tower.
     */
    protected boolean isInRange(short x, short y) {
        double euclideanDistance = Geometry.findEuclideanDistance(getX(), getY(), x, y);
        return minRange <= euclideanDistance && euclideanDistance <= maxRange;
    }

    /**
     * Returns the projectile speed of the tower.
     * @return The projectile speed of the tower.
     */
    public int getProjectileSpeed() { return projectileSpeed; }

    /**
     * Returns the reload time of the tower.
     * @return The reload time of the tower.
     */
    public int getReload() { return reload; }

    /**
     * Returns the cumulative building cost of the tower.
     * @return The cumulative building cost of the tower.
     */
    public int getBuildValue() { return buildValue; }

    /**
     * Returns the upgrade cost of the tower.
     * @return The upgrade cost of the tower.
     */
    public int getUpgradeCost() { return upgradeCost; }

    /**
     * Returns whether the tower can be upgraded.
     * @return Whether the tower can be upgraded.
     */
    public boolean canUpgrade() {
        // Whether the player has enough resources
        return ArenaManager.getActivePlayer().hasResources(upgradeCost);
    }

    /**
     * Attempts to upgrade the tower.
     * @return Whether the tower was successfully upgraded.
     */
    public boolean tryUpgrade() {
        if (canUpgrade()) {
            ArenaEventRegister register = ArenaManager.getActiveEventRegister();

            register.ARENA_TOWER_UPGRADE_START.invoke(this,
                    new ArenaTowerEventArgs() {
                        { subject = Tower.this; }
                    }
            );

            ArenaManager.getActivePlayer().spendResources(upgradeCost);
            System.out.println(String.format("%s is being upgraded", getDisplayName()));
            upgrade();

            register.ARENA_TOWER_UPGRADE_END.invoke(this,
                    new ArenaTowerEventArgs() {
                        { subject = Tower.this; }
                    }
            );

            return true;
        } else {
            System.out.println(String.format("not enough resource to upgrade %s", getDisplayName()));
            return false;
        }
    }

    /**
     * Upgrades the tower.
     */
    protected void upgrade() {
        buildValue += upgradeCost;
    }

    /**
     * Shoots a projectile.
     * @param validTargets The valid targets that the tower can shoot at.
     */
    protected void shoot(PriorityQueue<Monster> validTargets) {
        // Target the monster with the shortest path to end zone
        ArenaObjectFactory.createProjectile(this, this, validTargets.peek(), (short) 0, (short) 0);
    }

    @Override
    public String getDisplayName() { return getClass().getSimpleName(); }
    
    @Override
    public String getDisplayDetails() {
        return String.format("Attack Power: %d\nReload Time: %d\nRange: [%d , %d]\nUpgrade Cost: %d\nBuild Value: %d", this.attackPower,
        this.reload,this.minRange,this.maxRange,this.upgradeCost,this.buildValue);
    }
}

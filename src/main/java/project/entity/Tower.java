package project.entity;

import java.util.PriorityQueue;

import javafx.scene.image.ImageView;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import project.controller.ArenaEventRegister;
import project.controller.ArenaManager;
import project.event.EventHandler;
import project.event.eventargs.ArenaTowerEventArgs;
import project.event.eventargs.BooleanResultEventArgs;
import project.query.ArenaObjectRingSortedSelector;
import project.query.ArenaObjectStorage;
import project.query.ArenaObjectStorage.SortOption;
import project.query.ArenaObjectStorage.StoredComparableType;

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
    protected int buildValue = getBuildingCost();

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
                    // Target the monster with the shortest path to end zone
                    generateProjectile(validTargets.peek());
                    counter = reload;
                }
            }

            counter--;
        };
    }

    /**
     * The method invoked when a tower is being checked whether it can be upgraded.
     */
    protected EventHandler<ArenaTowerEventArgs> onCheckUpgradeTower = (sender, args) -> {
        if (args.subject != this) return;

        // Whether the player has enough resources
        boolean canUpgrade = ArenaManager.getActivePlayer().hasResources(upgradeCost);

        ArenaManager.getActiveEventRegister().ARENA_TOWER_UPGRADE_CHECK_RESULT.invoke(this,
                new BooleanResultEventArgs() {
                    {
                        recipient = sender;
                        result = canUpgrade;
                    }
                }
        );
    };

    /**
     * The method invoked when a tower is being confirmed for upgrade.
     * <p>
     * Note: If the upgrade is successful, resources will be automatically deducted from the currently active player.
     * Either way, the player will not be directly notified of the result.
     */
    protected EventHandler<ArenaTowerEventArgs> onConfirmUpgradeTower = (sender, args) -> {
        if (args.subject != this) return;

        // Only successful if the player has enough resources
        if (ArenaManager.getActivePlayer().hasResources(upgradeCost)) {
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
        }

        System.out.println(String.format("not enough resource to upgrade %s", getDisplayName()));
    };

    /**
     * Constructs a newly allocated {@link Tower} object and adds it to the {@link ArenaObjectStorage}.
     * @param storage The storage to add the object to.
     * @param imageView The ImageView to bound the object to.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     */
    public Tower(ArenaObjectStorage storage, ImageView imageView, short x, short y) {
        super(storage, imageView, x, y);

        ArenaEventRegister register = ArenaManager.getActiveEventRegister();
        register.ARENA_TOWER_UPGRADE_CHECK.subscribe(onCheckUpgradeTower);
        register.ARENA_TOWER_UPGRADE_CONFIRM.subscribe(onConfirmUpgradeTower);
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
     * Returns the projectile speed of the tower.
     * @return The projectile speed of the tower.
     */
    public final int getProjectileSpeed() { return projectileSpeed; }

    /**
     * Returns the reload time of the tower.
     * @return The reload time of the tower.
     */
    public final int getReload() { return reload; }

    /**
     * Returns the cumulative building cost of the tower.
     * @return The cumulative building cost of the tower.
     */
    public final int getBuildValue() { return buildValue; }

    /**
     * Returns the upgrade cost of the tower.
     * @return The upgrade cost of the tower.
     */
    public final int getUpgradeCost() { return upgradeCost; }

    /**
     * Upgrades the tower.
     */
    protected void upgrade() {
        buildValue += upgradeCost;
    }

    /**
     * Generates a new projectile based on the tower and primary target.
     * 
     * Note that creating a new {@link ArenaObject} will automatically add it to the {@link ArenaObjectStorage}.
     * 
     * @param primaryTarget The target that the projectile will hit.
     */
    protected abstract void generateProjectile(Monster primaryTarget);

    @Override
    public String getDisplayName() { return getClass().getSimpleName(); }
    
    @Override
    public String getDisplayDetails() {
        return String.format("Attack Power: %d\nReload Time: %d\nRange: [%d , %d]\nUpgrade Cost: %d\nBuild Value: %d", this.attackPower,
        this.reload,this.minRange,this.maxRange,this.upgradeCost,this.buildValue);
    }
}

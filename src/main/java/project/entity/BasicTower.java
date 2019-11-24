package project.entity;

import javafx.scene.image.ImageView;

import javax.persistence.Entity;

import project.query.ArenaObjectStorage;

/**
 * Basic tower has no any special ability.
 */
@Entity
public class BasicTower extends Tower {

    /**
     * Returns the initial building cost of the tower.
     * @return The initial building cost of the tower.
     */
    public static int getBuildingCost() {
        return 10;
    }

    /**
     * Constructs a newly allocated {@link BasicTower} object and adds it to the {@link ArenaObjectStorage}.
     * @param storage The storage to add the object to.
     * @param imageView The ImageView to bound the object to.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     */
    public BasicTower(ArenaObjectStorage storage, ImageView imageView, short x, short y) {
        super(storage, imageView, x, y);
        this.attackPower = 10;
        this.maxRange = 65;
        this.projectileSpeed = 5;
        this.buildValue = getBuildingCost();
        this.upgradeCost = 10;
    }

    @Override
    protected void upgrade() {
        super.upgrade();
        if (this.attackPower + 5 >= maxAttackPower) {
            this.attackPower = maxAttackPower;
        } else {
            this.attackPower += 5;
        }
    }

    @Override
    public void generateProjectile(Monster primaryTarget) {
        new BasicProjectile(storage, imageView, this, primaryTarget, (short) 0, (short) 0);
    }
}

package project.arena.towers;

import javax.persistence.Entity;

import javafx.scene.image.Image;
import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.UIController;
import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.monsters.Monster;
import project.arena.projectiles.BasicProjectile;
import project.arena.projectiles.Projectile;

/**
 * Basic tower has no any special ability.
 */
@Entity
public class BasicTower extends Tower {

    /**
     * Constructor of basic tower.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinate of basic tower.
     */
    public BasicTower(@NonNull Arena arena, @NonNull Coordinates coordinates){
        super(arena, coordinates);
        this.attackPower = 10;
        this.buildingCost = 10;
        this.maxShootingRange = 65;
        this.attackSpeed = 5;
        this.upgradeCost = 10;
        this.imageView = new ImageView(new Image("/basicTower.png", UIController.GRID_WIDTH, UIController.GRID_HEIGHT, true, true));
        this.coordinates.bindByImage(this.imageView);
    }

    /**
     * Constructor of basic tower.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinates of the tower.
     * @param imageView The image view of the tower.
     */
    public BasicTower(@NonNull Arena arena, @NonNull Coordinates coordinates, ImageView imageView) {
        super(arena, coordinates, imageView);
        this.attackPower = 10;
        this.buildingCost = 10;
        this.maxShootingRange = 65;
        this.attackSpeed = 5;
        this.upgradeCost = 10;
    }

    /**
     * @see Tower#Tower(Arena, Tower)
     */
    public BasicTower(@NonNull Arena arena, @NonNull BasicTower other) {
        super(arena, other);
    }

    @Override
    public BasicTower deepCopy(@NonNull Arena arena) {
        return new BasicTower(arena, this);
    }

    @Override
    protected String getClassName() { return "Basic Tower"; }

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
    public Projectile generateProjectile(){
        if(!isReload()) {
//            PriorityQueue<Monster> monsters = arena.getMonsters();
            for (Monster m : arena.getMonsters()) {
                if (isValidTarget(m)) {
                    this.hasAttack = true;
                    this.counter = this.reload;
                    return new BasicProjectile(arena, coordinates, m, attackSpeed, attackPower);
                }
            }
        }
        return null;
    }

    /**
     * Accesses the information of tower.
     * @return the information of tower.
     */
    @Override
    public String getInformation() {
        return String.format("attack power: %d\nupgrade cost: %d\nshooting range: [%d , %d]", this.attackPower,
                this.upgradeCost, this.minShootingRange,this.maxShootingRange);
    }
}

package project.field;

import java.util.EnumSet;
import java.util.LinkedList;

import project.controller.ArenaEventManager;
import project.controller.ArenaManager;
import project.entity.Tower;
import project.event.EventHandler;
import project.event.eventargs.ArenaObjectEventArgs;
import project.event.eventargs.ArenaTowerUpgradeArgs;
import project.event.eventsets.ArenaObjectIOEvent;
import project.event.eventsets.ArenaTowerChangeEvent;
import project.query.ArenaObjectGridSelector;
import project.query.ArenaObjectStorage;
import project.query.ArenaObjectStorage.StoredType;

/**
 * A scalar field where the value on each point equals the minimum distance
 * travelled from that point to the end zone.
 */
public final class MonsterAttacksToEndField extends ArenaScalarField<Float> {

    private class TowerAttacksPerFrameField extends ArenaScalarField<Float> {

        /**
         * Increments the value at each point on the scalar field within a defined hollow circle.
         * @param amount The increment amount.
         * @param centerX The center x-coordinate of the circle.
         * @param centerY The center y-coordinate of the circle.
         * @param minRadius The minimum radius of the circle.
         * @param maxRadius The maximum radius of the circle.
         */
        private void incrementHollowCircle(Float amount, short centerX, short centerY, short minRadius, short maxRadius) {
            short leftX = (short) (centerX - maxRadius);
            short topY = (short) (centerY - maxRadius);
    
            short effectiveWidth = (short) (maxRadius - Math.max(0, - leftX) - Math.max(0, leftX + maxRadius - ArenaManager.ARENA_WIDTH));
            short effectiveHeight = (short) (maxRadius - Math.max(0, - topY) - Math.max(0, topY + maxRadius - ArenaManager.ARENA_HEIGHT));
    
            short startX = (short) Math.max(0, leftX);
            short endX_intermediate = (short) (startX + maxRadius - minRadius);
            short startX_intermediate = (short) (endX_intermediate + minRadius + minRadius);
            short endX = (short) (startX + effectiveWidth);
    
            short startY = (short) Math.max(0, topY);
            short endY_intermediate = (short) (startY + maxRadius - minRadius);
            short startY_intermediate = (short) (endY_intermediate + minRadius + minRadius);
            short endY = (short) (startY + effectiveHeight);
    
            // Determine outer loop to minimize code jumping.
            if (effectiveWidth < effectiveHeight) {
                for (short x = startX; x <= endX; x++) {
                    if (x > endX_intermediate) x = startX_intermediate;
    
                    short deltaX = (short) (x - centerX);
    
                    short flooredMaxSqrt = (short) Math.sqrt(maxRadius * maxRadius - deltaX * deltaX);
                    short inner_startY = (short) (centerY - flooredMaxSqrt);
                    short inner_endY = (short) (centerY + flooredMaxSqrt);
    
                    short flooredMinSqrt = (short) Math.sqrt(minRadius * minRadius - deltaX * deltaX);
                    short inner_endY_intermediate = (short) (centerY - flooredMinSqrt);
                    short inner_startY_intermediate = (short) (centerY + flooredMinSqrt);
    
                    for (short y = inner_startY; y <= inner_endY; y++) {
                        if (y > inner_endY_intermediate) y = inner_startY_intermediate;

                        setValueAt(x, y, getValueAt(x, y) + amount);
                    }
                }
            } else {
                for (short y = startY; y <= endY; y++) {
                    if (y > endY_intermediate) y = startY_intermediate;
    
                    short deltaY = (short) (y - centerY);
    
                    short flooredMaxSqrt = (short) Math.sqrt(maxRadius * maxRadius - deltaY * deltaY);
                    short inner_startX = (short) (centerX - flooredMaxSqrt);
                    short inner_endX = (short) (centerX + flooredMaxSqrt);
    
                    short flooredMinSqrt = (short) Math.sqrt(minRadius * minRadius - deltaY * deltaY);
                    short inner_endX_intermediate = (short) (centerX - flooredMinSqrt);
                    short inner_startX_intermediate = (short) (centerX + flooredMinSqrt);
    
                    for (short x = inner_startX; x <= inner_endX; x++) {
                        if (x > inner_endX_intermediate) x = inner_startX_intermediate;

                        setValueAt(x, y, getValueAt(x, y) + amount);
                    }
                }
            }
        }
    }

    private TowerAttacksPerFrameField towerAttacksPerFrameField = new TowerAttacksPerFrameField();

    /**
     * The method invoked when an {@link ArenaObject} is added.
     */
    private EventHandler<ArenaObjectEventArgs> onAddObject = (sender, args) -> {
        if (args.subject instanceof Tower) {
            Tower tower = (Tower) args.subject;
            towerAttacksPerFrameField.incrementHollowCircle(1f / tower.getReload(), tower.getX(), tower.getY(), tower.getMinShootingRange(), tower.getMaxShootingRange());

            recalculate();
        }
    };

    /**
     * The method invoked when an {@link ArenaObject} is removed.
     */
    private EventHandler<ArenaObjectEventArgs> onRemoveObject = (sender, args) -> {
        if (args.subject instanceof Tower) {
            Tower tower = (Tower) args.subject;
            towerAttacksPerFrameField.incrementHollowCircle(-1f / tower.getReload(), tower.getX(), tower.getY(), tower.getMinShootingRange(), tower.getMaxShootingRange());
            
            recalculate();
        }
    };

    /**
     * The method invoked when a {@link Tower} is upgraded.
     */
    private EventHandler<ArenaTowerUpgradeArgs> onTowerUpgrade = (sender, args) -> {
        Tower originalTower = args.originalTower;
        towerAttacksPerFrameField.incrementHollowCircle(-1f / originalTower.getReload(), originalTower.getX(), originalTower.getY(), originalTower.getMinShootingRange(), originalTower.getMaxShootingRange());

        Tower newTower = args.newTower;
        towerAttacksPerFrameField.incrementHollowCircle(1f / newTower.getReload(), newTower.getX(), newTower.getY(), newTower.getMinShootingRange(), newTower.getMaxShootingRange());

        recalculate();
    };

    /**
     * Constructs a newly allocated {@link MonsterAttacksToEndField} object.
     */
    public MonsterAttacksToEndField() {
        ArenaEventManager manager = ArenaManager.getActiveEventManager();
        manager.OBJECT_IO.subscribe(ArenaObjectIOEvent.ADD, onAddObject);
        manager.OBJECT_IO.subscribe(ArenaObjectIOEvent.REMOVE, onRemoveObject);
        manager.TOWER_CHANGE.subscribe(ArenaTowerChangeEvent.UPGRADE, onTowerUpgrade);
    }

    /**
     * Recalculates the entire scalar field.
     */
    private void recalculate() {
        final float MOVEMENT_COST = 0.001f; // To avoid getting stuck
        
        ArenaObjectStorage storage = ArenaManager.getActiveArenaInstance().getArenaObjectStorage();

        // Reset values
        setAll(Float.POSITIVE_INFINITY);

        // Calculate distance
    	LinkedList<ScalarFieldPoint> openSet = new LinkedList<>();
        openSet.add(new ScalarFieldPoint(ArenaManager.END_X, ArenaManager.END_Y));

        setValueAt(ArenaManager.END_X, ArenaManager.END_Y, 0f);
    	while (!openSet.isEmpty()) {
            ScalarFieldPoint current = openSet.poll();
            short currentX = current.getX();
            short currentY = current.getY();
    		// Monsters can only travel horizontally or vertically
    		for (ScalarFieldPoint neighbour : getTaxicabNeighbours(currentX, currentY)) {
                short neighbourX = neighbour.getX();
                short neighbourY = neighbour.getY();

                // Monsters can only go to grids that do not contain a Tower
                ArenaObjectGridSelector selector = new ArenaObjectGridSelector(neighbourX, neighbourY);
                if (storage.getQueryResult(selector, EnumSet.of(StoredType.TOWER)).isEmpty()) {
        			float newCost = getValueAt(currentX, currentY) + towerAttacksPerFrameField.getValueAt(neighbourX, neighbourY) + MOVEMENT_COST;
        			if (getValueAt(neighbourX, neighbourY) > newCost ) {
                        setValueAt(neighbourX, neighbourY, newCost);
        				openSet.add(new ScalarFieldPoint(neighbourX, neighbourY));
        			}
    			}
    		}
        }
    }
}
package project.field;

import java.util.EnumSet;
import java.util.LinkedList;

import project.arena.ArenaEventRegister;
import project.arena.ArenaInstance;
import project.control.ArenaManager;
import project.entity.Tower;
import project.event.EventHandler;
import project.event.eventargs.ArenaObjectEventArgs;
import project.event.eventargs.ArenaTowerEventArgs;
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
         * Increments the value at each point on the scalar field within a defined circular ring.
         * @param amount The increment amount.
         * @param centerX The center x-coordinate of the ring.
         * @param centerY The center y-coordinate of the ring.
         * @param minRadius The minimum radius of the ring.
         * @param maxRadius The maximum radius of the ring.
         */
        private void incrementRing(Float amount, short centerX, short centerY, short minRadius, short maxRadius) {
            short leftX = (short) (centerX - maxRadius);
            short topY = (short) (centerY - maxRadius);
    
            short effectiveWidth = (short) (maxRadius - Math.max(0, - leftX) - Math.max(0, leftX + maxRadius - ArenaManager.ARENA_WIDTH));
            short effectiveHeight = (short) (maxRadius - Math.max(0, - topY) - Math.max(0, topY + maxRadius - ArenaManager.ARENA_HEIGHT));
    
            short startX = (short) Math.max(0, leftX);
            short endX_intermediate = (short) Math.min(startX + effectiveWidth, startX + maxRadius - minRadius);
            short startX_intermediate = (short) Math.min(startX + effectiveWidth, endX_intermediate + minRadius + minRadius);
            short endX = (short) (startX + effectiveWidth);
    
            short startY = (short) Math.max(0, topY);
            short endY_intermediate = (short) Math.min(startY + effectiveHeight, startY + maxRadius - minRadius);
            short startY_intermediate = (short) Math.min(startY + effectiveHeight, endY_intermediate + minRadius + minRadius);
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
    
                    for (short y = inner_startY; y <= inner_endY_intermediate; y++) {
                        setValueAt(x, y, getValueAt(x, y) + amount);
                    }

                    for (short y = inner_startY_intermediate; y <= inner_endY; y++) {
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
    
                    for (short x = inner_startX; x <= inner_endX_intermediate; x++) {
                        setValueAt(x, y, getValueAt(x, y) + amount);
                    }

                    for (short x = inner_startX_intermediate; x <= inner_endX; x++) {
                        setValueAt(x, y, getValueAt(x, y) + amount);
                    }
                }
            }
        }

        private TowerAttacksPerFrameField(ArenaInstance arenaInstance) {
            super(arenaInstance);
            setAll(0f);
        }
    }

    private TowerAttacksPerFrameField towerAttacksPerFrameField;

    /**
     * The method invoked when an {@link ArenaObject} is being added.
     */
    private EventHandler<ArenaObjectEventArgs> onAddObject = (sender, args) -> {
        if (args.subject instanceof Tower) {
            Tower tower = (Tower) args.subject;
            towerAttacksPerFrameField.incrementRing(
                    1f / tower.getReload(),
                    tower.getX(),
                    tower.getY(),
                    tower.getMinRange(),
                    tower.getMaxRange()
            );

            recalculate(ArenaManager.getActiveObjectStorage());
        }
    };

    /**
     * The method invoked when an {@link ArenaObject} is being removed.
     */
    private EventHandler<ArenaObjectEventArgs> onRemoveObject = (sender, args) -> {
        if (args.subject instanceof Tower) {
            Tower tower = (Tower) args.subject;
            towerAttacksPerFrameField.incrementRing(
                    -1f / tower.getReload(),
                    tower.getX(),
                    tower.getY(),
                    tower.getMinRange(),
                    tower.getMaxRange()
            );
            
            recalculate(ArenaManager.getActiveObjectStorage());
        }
    };

    /**
     * The method invoked when an {@link ArenaObject} is scheduled to be moved.
     */
    private EventHandler<ArenaObjectEventArgs> onStartMoveObject = (sender, args) -> {
        if (args.subject instanceof Tower) {
            Tower tower = (Tower) args.subject;
            towerAttacksPerFrameField.incrementRing(
                    -1f / tower.getReload(),
                    tower.getX(),
                    tower.getY(),
                    tower.getMinRange(),
                    tower.getMaxRange()
            );
        }
    };

    /**
     * The method invoked when an {@link ArenaObject} has been moved.
     */
    private EventHandler<ArenaObjectEventArgs> onEndMoveObject = (sender, args) -> {
        if (args.subject instanceof Tower) {
            Tower tower = (Tower) args.subject;
            towerAttacksPerFrameField.incrementRing(
                    1f / tower.getReload(),
                    tower.getX(),
                    tower.getY(),
                    tower.getMinRange(),
                    tower.getMaxRange()
            );

            recalculate(ArenaManager.getActiveObjectStorage());
        }
    };

    /**
     * The method invoked when a {@link Tower} is scheduled to be upgraded.
     */
    private EventHandler<ArenaTowerEventArgs> onStartUpgradeTower = (sender, args) -> {
        Tower tower = args.subject;
        towerAttacksPerFrameField.incrementRing(
                -1f / tower.getReload(),
                tower.getX(),
                tower.getY(),
                tower.getMinRange(),
                tower.getMaxRange()
        );
    };

    /**
     * The method invoked when a {@link Tower} has been upgraded.
     */
    private EventHandler<ArenaTowerEventArgs> onEndUpgradeTower = (sender, args) -> {
        Tower tower = args.subject;
        towerAttacksPerFrameField.incrementRing(
                1f / tower.getReload(),
                tower.getX(),
                tower.getY(),
                tower.getMinRange(),
                tower.getMaxRange()
        );

        recalculate(ArenaManager.getActiveObjectStorage());
    };

    /**
     * Constructs a newly allocated {@link MonsterAttacksToEndField} object and attaches it to an arena instance.
     * @param arenaInstance The arena instance.
     */
    public MonsterAttacksToEndField(ArenaInstance arenaInstance) {
        super(arenaInstance);

        towerAttacksPerFrameField = new TowerAttacksPerFrameField(arenaInstance);
        recalculate(arenaInstance.getStorage());

        ArenaEventRegister register = arenaInstance.getEventRegister();
        register.ARENA_OBJECT_ADD.subscribe(onAddObject);
        register.ARENA_OBJECT_REMOVE.subscribe(onRemoveObject);
        register.ARENA_OBJECT_MOVE_START.subscribe(onStartMoveObject);
        register.ARENA_OBJECT_MOVE_END.subscribe(onEndMoveObject);
        register.ARENA_TOWER_UPGRADE_START.subscribe(onStartUpgradeTower);
        register.ARENA_TOWER_UPGRADE_END.subscribe(onEndUpgradeTower);
    }

    /**
     * Recalculates the entire scalar field.
     * @param storage The storage to base the calculation on.
     */
    private void recalculate(ArenaObjectStorage storage) {
        final float MOVEMENT_COST = 0.001f; // To avoid getting stuck

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
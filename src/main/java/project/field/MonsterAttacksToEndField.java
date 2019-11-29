package project.field;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.PriorityQueue;

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
public final class MonsterAttacksToEndField implements ArenaScalarField<Float> {

    /**
     * The values of the scalar field.
     */
    protected float[][] values = new float[ArenaManager.ARENA_WIDTH + 1][ArenaManager.ARENA_HEIGHT + 1];

    /**
     * The scalar field representing the number of tower attacks per frame against a monster at that location.
     */
    protected class TowerAttacksPerFrameField implements ArenaScalarField<Float> {

        protected float[][] values = new float[ArenaManager.ARENA_WIDTH + 1][ArenaManager.ARENA_HEIGHT + 1];

        /**
         * {@inheritDoc}
         */
        @Override
        public Float getValueAt(short x, short y) {
            return this.values[x][y];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setValueAt(short x, short y, Float value) {
            this.values[x][y] = value;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setAll(Float value) {
            for (int x = 0; x < ArenaManager.ARENA_WIDTH; x++) {
                Arrays.fill(this.values[x], value);
            }
        }

        /**
         * Increments the value at each point on the scalar field within a defined circular ring.
         * @param amount The increment amount.
         * @param centerX The center x-coordinate of the ring.
         * @param centerY The center y-coordinate of the ring.
         * @param minRadius The minimum radius of the ring.
         * @param maxRadius The maximum radius of the ring.
         */
        private void incrementRing(Float amount, short centerX, short centerY, short minRadius, short maxRadius) 
        {
            assert minRadius >= 0 && maxRadius >= 0 && minRadius <= maxRadius;

            short startX = (short) Math.max(0, centerX - maxRadius);
            short endX = (short) Math.min(ArenaManager.ARENA_WIDTH, centerX + maxRadius);
            short effectiveWidth = (short) (endX - startX);
    
            short startY = (short) Math.max(0, centerY - maxRadius);
            short endY = (short) Math.min(ArenaManager.ARENA_HEIGHT, centerY + maxRadius);
            short effectiveHeight = (short) (endY - startY);
    
            short endX_intermediate = (short) Math.max(0, Math.min(endX, centerX - minRadius));
            short startX_intermediate = (short) Math.max(0, Math.min(endX_intermediate, centerX + minRadius));
            if (endX_intermediate == startX_intermediate) startX_intermediate++;
    
            short endY_intermediate = (short) Math.max(0, Math.min(endY, centerY - minRadius));
            short startY_intermediate = (short) Math.max(0, Math.min(endY_intermediate, centerY + minRadius));
            if (endY_intermediate == startY_intermediate) startY_intermediate++;
    
            // Determine outer loop to reduce calculations
            if (effectiveWidth < effectiveHeight) {
                for (short x = startX; x <= endX_intermediate; x++) {    
                    short deltaX = (short) (x - centerX);
    
                    short flooredMaxSqrt = (short) Math.sqrt(maxRadius * maxRadius - deltaX * deltaX);
                    short inner_startY = (short) Math.max(0, centerY - flooredMaxSqrt);
                    short inner_endY = (short) Math.max(0, Math.min(ArenaManager.ARENA_HEIGHT, centerY + flooredMaxSqrt));
    
                    short flooredMinSqrt = (short) Math.sqrt(minRadius * minRadius - deltaX * deltaX);
                    short inner_endY_intermediate = (short) Math.max(0, centerY - flooredMinSqrt);
                    short inner_startY_intermediate = (short) Math.max(0, Math.min(ArenaManager.ARENA_HEIGHT, centerY + flooredMinSqrt));
                    if (inner_endY_intermediate == inner_startY_intermediate) inner_startY_intermediate++;

                    for (short y = inner_startY; y <= inner_endY_intermediate; y++) {
                        setValueAt(x, y, getValueAt(x, y) + amount);
                    }

                    for (short y = inner_startY_intermediate; y <= inner_endY; y++) {
                        setValueAt(x, y, getValueAt(x, y) + amount);
                    }
                }

                for (short x = startX_intermediate; x <= endX; x++) {    
                    short deltaX = (short) (x - centerX);
    
                    short flooredMaxSqrt = (short) Math.sqrt(maxRadius * maxRadius - deltaX * deltaX);
                    short inner_startY = (short) Math.max(0, centerY - flooredMaxSqrt);
                    short inner_endY = (short) Math.max(0, Math.min(ArenaManager.ARENA_HEIGHT, centerY + flooredMaxSqrt));
    
                    short flooredMinSqrt = (short) Math.sqrt(minRadius * minRadius - deltaX * deltaX);
                    short inner_endY_intermediate = (short) Math.max(0, centerY - flooredMinSqrt);
                    short inner_startY_intermediate = (short) Math.max(0, Math.min(ArenaManager.ARENA_HEIGHT, centerY + flooredMinSqrt));
                    if (inner_endY_intermediate == inner_startY_intermediate) inner_startY_intermediate++;

                    for (short y = inner_startY; y <= inner_endY_intermediate; y++) {
                        setValueAt(x, y, getValueAt(x, y) + amount);
                    }

                    for (short y = inner_startY_intermediate; y <= inner_endY; y++) {
                        setValueAt(x, y, getValueAt(x, y) + amount);
                    }
                }
            } else {
                for (short y = startY; y <= endY_intermediate; y++) {
                    short deltaY = (short) (y - centerY);
    
                    short flooredMaxSqrt = (short) Math.sqrt(maxRadius * maxRadius - deltaY * deltaY);
                    short inner_startX = (short) Math.max(0, centerX - flooredMaxSqrt);
                    short inner_endX = (short) Math.max(0, Math.min(ArenaManager.ARENA_WIDTH, centerX + flooredMaxSqrt));
    
                    short flooredMinSqrt = (short) Math.sqrt(minRadius * minRadius - deltaY * deltaY);
                    short inner_endX_intermediate = (short) Math.max(0, centerX - flooredMinSqrt);
                    short inner_startX_intermediate = (short) Math.max(0, Math.min(ArenaManager.ARENA_WIDTH, centerX + flooredMinSqrt));
                    if (inner_endX_intermediate == inner_startX_intermediate) inner_startX_intermediate++;

                    for (short x = inner_startX; x <= inner_endX_intermediate; x++) {
                        setValueAt(x, y, getValueAt(x, y) + amount);
                    }

                    for (short x = inner_startX_intermediate; x <= inner_endX; x++) {
                        setValueAt(x, y, getValueAt(x, y) + amount);
                    }
                }
                for (short y = startY_intermediate; y <= endY; y++) {
                    short deltaY = (short) (y - centerY);
    
                    short flooredMaxSqrt = (short) Math.sqrt(maxRadius * maxRadius - deltaY * deltaY);
                    short inner_startX = (short) Math.max(0, centerX - flooredMaxSqrt);
                    short inner_endX = (short) Math.min(ArenaManager.ARENA_WIDTH, centerX + flooredMaxSqrt);
    
                    short flooredMinSqrt = (short) Math.sqrt(minRadius * minRadius - deltaY * deltaY);
                    short inner_endX_intermediate = (short) Math.max(0, centerX - flooredMinSqrt);
                    short inner_startX_intermediate = (short) Math.min(ArenaManager.ARENA_WIDTH, centerX + flooredMinSqrt);
                    if (inner_endX_intermediate == inner_startX_intermediate) inner_startX_intermediate++;

                    for (short x = inner_startX; x <= inner_endX_intermediate; x++) {
                        setValueAt(x, y, getValueAt(x, y) + amount);
                    }

                    for (short x = inner_startX_intermediate; x <= inner_endX; x++) {
                        setValueAt(x, y, getValueAt(x, y) + amount);
                    }
                }
            }
        }
    }

    private TowerAttacksPerFrameField towerAttacksPerFrameField = new TowerAttacksPerFrameField();

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
        recalculate(arenaInstance.getStorage());

        ArenaEventRegister register = arenaInstance.getEventRegister();
        register.ARENA_OBJECT_ADD.subscribe(onAddObject);
        register.ARENA_OBJECT_REMOVE.subscribe(onRemoveObject);
        register.ARENA_OBJECT_MOVE_START.subscribe(onStartMoveObject);
        register.ARENA_OBJECT_MOVE_END.subscribe(onEndMoveObject);
        register.ARENA_TOWER_UPGRADE_START.subscribe(onStartUpgradeTower);
        register.ARENA_TOWER_UPGRADE_END.subscribe(onEndUpgradeTower);
    }

    @Override
    public Float getValueAt(short x, short y) {
        return this.values[x][y];
    }
    
    @Override
    public void setValueAt(short x, short y, Float value) {
        this.values[x][y] = value;
    }

    @Override
    public void setAll(Float value) {
        for (int x = 0; x < ArenaManager.ARENA_WIDTH + 1; x++) {
            Arrays.fill(this.values[x], value);
        }
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
    	PriorityQueue<ScalarFieldPoint> openSet = new PriorityQueue<>((o1, o2) -> Float.compare(getValueAt(o1.getX(), o1.getY()), getValueAt(o2.getX(), o2.getY())));
        openSet.add(new ScalarFieldPoint(ArenaManager.END_X, ArenaManager.END_Y));

        setValueAt(ArenaManager.END_X, ArenaManager.END_Y, 0f);
    	while (!openSet.isEmpty()) {
            ScalarFieldPoint current = openSet.poll();
            short currentX = current.getX();
            short currentY = current.getY();
    		// Monsters can only travel horizontally or vertically
    		for (ScalarFieldPoint neighbour : ArenaScalarField.getTaxicabNeighbours(currentX, currentY)) {
                short neighbourX = neighbour.getX();
                short neighbourY = neighbour.getY();

                // Monsters can only go to grids that do not contain a Tower
                ArenaObjectGridSelector selector = new ArenaObjectGridSelector(neighbourX, neighbourY);
                if (storage.getQueryResult(selector, EnumSet.of(StoredType.TOWER)).isEmpty()) {
        			float newCost = getValueAt(currentX, currentY) + towerAttacksPerFrameField.getValueAt(neighbourX, neighbourY) + MOVEMENT_COST;
        			if (getValueAt(neighbourX, neighbourY) > newCost) {
                        setValueAt(neighbourX, neighbourY, newCost);
        				openSet.add(new ScalarFieldPoint(neighbourX, neighbourY));
        			}
    			}
    		}
        }
    }
}
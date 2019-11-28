package project.query;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;

import project.control.ArenaManager;
import project.entity.ArenaObject;
import project.query.ArenaObjectStorage.StoredType;

/**
 * A class that selects the {@link ArenaObject}s inside a defined circular ring (including the boundary) within the arena.
 */
public class ArenaObjectRingSelector implements ArenaObjectSelector {

    /**
     * Center x-coordinate of the ring.
     */
    protected final short centerX;

    /**
     * Center y-coordinate of the ring.
     */
    protected final short centerY;

    /**
     * Minimum radius of the ring.
     */
    protected final short minRadius;

    /**
     * Minimum radius of the ring.
     */
    protected final short maxRadius;

    /**
     * The effective minimum x-coordinate of the selection based on the boundary of the arena.
     */
    protected final short startX;

    /**
     * The effective maximum x-coordinate of the selection based on the boundary of the arena.
     */
    protected final short endX;

    /**
     * The effective minimum y-coordinate of the selection based on the boundary of the arena.
     */
    protected final short startY;

    /**
     * The effective maximum y-coordinate of the selection based on the boundary of the arena.
     */
    protected final short endY;

    /**
     * The effective width of the selection based on the boundary of the arena.
     */
    protected final short effectiveWidth;

    /**
     * The effective height of the selection based on the boundary of the arena.
     */
    protected final short effectiveHeight;

    /**
     * Constructs a newly allocated {@link ArenaObjectRingSelector} object.
     * @param centerX The center x-coordinate of the ring.
     * @param centerY The center y-coordinate of the ring.
     * @param minRadius The minimum radius of the ring, must be non-negative.
     * @param maxRadius The maximum radius of the ring, must be non-negative.
     */
    public ArenaObjectRingSelector(short centerX, short centerY, short minRadius, short maxRadius) {
        if (minRadius < 0) throw new IllegalArgumentException(String.format("The minRadius must be non-negative. Value: %d", minRadius));
        if (maxRadius < 0) throw new IllegalArgumentException(String.format("The maxRadius must be non-negative. Value: %d", maxRadius));
        if (minRadius > maxRadius) throw new IllegalArgumentException(String.format("The minRadius: %d should be not greater than maxRadius: %d", minRadius, maxRadius));

        this.centerX = centerX;
        this.centerY = centerY;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;

        if (centerX - maxRadius < 0) this.startX = 0;
        else if (centerX - maxRadius > ArenaManager.ARENA_WIDTH) this.startX = ArenaManager.ARENA_WIDTH;
        else this.startX = (short) (centerX - maxRadius);

        if (centerY - maxRadius < 0) this.startY = 0;
        else if (centerY - maxRadius > ArenaManager.ARENA_HEIGHT) this.startY = ArenaManager.ARENA_HEIGHT;
        else this.startY = (short) (centerY - maxRadius);

        if (centerX + maxRadius < 0) this.endX = 0;
        else if (centerX + maxRadius > ArenaManager.ARENA_WIDTH) this.endX = ArenaManager.ARENA_WIDTH;
        else this.endX = (short) (centerX + maxRadius);
        this.effectiveWidth = (short) (endX - startX);
        
        if (centerY + maxRadius < 0) this.endY = 0;
        else if (centerY + maxRadius > ArenaManager.ARENA_HEIGHT) this.endY = ArenaManager.ARENA_HEIGHT;
        else this.endY = (short) (centerY + maxRadius);
        this.effectiveHeight = (short) (endY - startY);
    }

    @Override
    public int estimateCost(ArenaObjectStorage storage) {
        // Out of bounds (== 0 means a point search)
        if (effectiveWidth < 0 || effectiveHeight < 0) return 0;

        // One access for the index, and one access for each object within the index.
        if (effectiveWidth < effectiveHeight) {
            return (int) (effectiveWidth * (1 + storage.getObjectsPerX()));
        } else {
            return (int) (effectiveHeight * (1 + storage.getObjectsPerY()));
        }
    }

    @Override
    public LinkedList<ArenaObject> select(ArenaObjectStorage storage,
            EnumSet<StoredType> types, LinkedList<ArenaObjectSelector> filters) {

        LinkedList<ArenaObject> result = new LinkedList<>();

        // Out of bounds (== 0 means a point search)
        if (effectiveWidth < 0 || effectiveHeight < 0) return result;

        // Additional filter that tests whether object is inside circle
        filters.add(new ArenaObjectPropertySelector<ArenaObject>(ArenaObject.class, o -> {
            return isInSelection(o);
        }));
        
        // Determine whether to access through x- or y- index based on number of accesses.
        if (effectiveWidth < effectiveHeight) {
            ArrayList<LinkedList<ArenaObject>> index = storage.getXIndex();

            for (short x = startX; x <= endX; x++) {
                for (ArenaObject o : index.get(x)) {
                    if (isInSelection(o)) {
                        if (isAllSatisfied(o, types, filters)) result.add(o);
                    }
                }
            }
        } else {
            ArrayList<LinkedList<ArenaObject>> index = storage.getYIndex();

            for (short y = startY; y <= endY; y++) {
                for (ArenaObject o : index.get(y)) {
                    if (isInSelection(o)) {
                        if (isAllSatisfied(o, types, filters)) result.add(o);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public boolean isInSelection(ArenaObject o) {
        short distX = (short) (o.getX() - centerX);
        short distY = (short) (o.getY() - centerY);

        int distSquared = distX * distX + distY * distY;

        return (distSquared >= minRadius * minRadius && distSquared <= maxRadius * maxRadius);
    }
} 
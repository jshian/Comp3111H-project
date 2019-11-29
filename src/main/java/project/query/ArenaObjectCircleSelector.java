package project.query;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import project.control.ArenaManager;
import project.entity.ArenaObject;
import project.query.ArenaObjectStorage.StoredType;

/**
 * A class that selects the {@link ArenaObject}s inside a defined circle (including the boundary) within the arena.
 */
public class ArenaObjectCircleSelector implements ArenaObjectSelector {

    /**
     * Center x-coordinate of the circle.
     */
    protected final short centerX;

    /**
     * Center y-coordinate of the circle.
     */
    protected final short centerY;

    /**
     * Radius of the circle.
     */
    protected final short radius;

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
     * Constructs a newly allocated {@link ArenaObjectCircleSelector} object.
     * @param centerX The center x-coordinate of the circle.
     * @param centerY The center y-coordinate of the circle.
     * @param radius The radius of the circle, must be non-negative.
     */
    public ArenaObjectCircleSelector(short centerX, short centerY, short radius) {
        if (radius < 0) throw new IllegalArgumentException(String.format("The radius must be non-negative. Value: %d", radius));

        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;

        if (centerX - radius < 0) this.startX = 0;
        else if (centerX - radius > ArenaManager.ARENA_WIDTH) this.startX = ArenaManager.ARENA_WIDTH;
        else this.startX = (short) (centerX - radius);

        if (centerY - radius < 0) this.startY = 0;
        else if (centerY - radius > ArenaManager.ARENA_HEIGHT) this.startY = ArenaManager.ARENA_HEIGHT;
        else this.startY = (short) (centerY - radius);

        if (centerX + radius < 0) this.endX = 0;
        else if (centerX + radius > ArenaManager.ARENA_WIDTH) this.endX = ArenaManager.ARENA_WIDTH;
        else this.endX = (short) (centerX + radius);
        this.effectiveWidth = (short) (endX - startX);
        
        if (centerY + radius < 0) this.endY = 0;
        else if (centerY + radius > ArenaManager.ARENA_HEIGHT) this.endY = ArenaManager.ARENA_HEIGHT;
        else this.endY = (short) (centerY + radius);
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
    public List<ArenaObject> select(ArenaObjectStorage storage,
            EnumSet<StoredType> types, List<ArenaObjectSelector> filters) {

        List<ArenaObject> result = new LinkedList<>();

        // Out of bounds (== 0 means a point search)
        if (effectiveWidth < 0 || effectiveHeight < 0) return result;

        // Additional filter that tests whether object is inside circle
        filters.add(new ArenaObjectPropertySelector<ArenaObject>(ArenaObject.class, o -> {
            return isInSelection(o);
        }));
        
        // Determine whether to access through x- or y- index based on number of accesses.
        if (effectiveWidth < effectiveHeight) {
            ArrayList<List<ArenaObject>> index = storage.getXIndex();

            for (short x = startX; x <= endX; x++) {
                for (ArenaObject o : index.get(x)) {
                    if (isInSelection(o)) {
                        if (isAllSatisfied(o, types, filters)) result.add(o);
                    }
                }
            }
        } else {
            ArrayList<List<ArenaObject>> index = storage.getYIndex();

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

        return (distX * distX + distY * distY <= radius * radius);
    }

    @Override
    public boolean isInSelectionByDefinition(ArenaObject o) {
        short distX = (short) (o.getX() - centerX);
        short distY = (short) (o.getY() - centerY);

        // Equation of circle
        return (distX * distX + distY * distY <= radius * radius);
    }
} 
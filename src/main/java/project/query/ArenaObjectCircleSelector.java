package project.query;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;

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
     * @param radius The radius of the circle.
     */
    public ArenaObjectCircleSelector(short centerX, short centerY, short radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;

        short leftX = (short) (centerX - radius);
        short topY = (short) (centerY - radius);

        this.effectiveWidth = (short) (radius - Math.max(0, - leftX) - Math.max(0, leftX + radius - ArenaManager.ARENA_WIDTH));
        this.effectiveHeight = (short) (radius - Math.max(0, - topY) - Math.max(0, topY + radius - ArenaManager.ARENA_HEIGHT));

        this.startX = (short) Math.max(0, leftX); assert startX >= 0;
        this.endX = (short) (startX + effectiveWidth); assert endX <= ArenaManager.ARENA_WIDTH;
        assert startX <= endX;

        this.startY = (short) Math.max(0, topY); assert startY >= 0;
        this.endY = (short) (startY + effectiveHeight); assert endX <= ArenaManager.ARENA_HEIGHT;
        assert startY <= endY;
    }

    @Override
    public float estimateSelectivity(ArenaObjectStorage storage) {
        // Out of bounds (== 0 means a point search)
        if (effectiveWidth < 0 || effectiveHeight < 0) return 0;

        // Ratio of area of circle inscribed inside a square over area of the square == PI / 4
        return ((float) Math.PI / 4 * ((effectiveWidth + 1) * (effectiveHeight + 1)) / ((ArenaManager.ARENA_WIDTH + 1) * (ArenaManager.ARENA_HEIGHT + 1)));
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
                    if (isAllSatisfied(o, types, filters)) result.add(o);
                }
            }
        } else {
            ArrayList<LinkedList<ArenaObject>> index = storage.getYIndex();

            for (short y = startY; y <= endY; y++) {
                for (ArenaObject o : index.get(y)) {
                    if (isAllSatisfied(o, types, filters)) result.add(o);
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
} 
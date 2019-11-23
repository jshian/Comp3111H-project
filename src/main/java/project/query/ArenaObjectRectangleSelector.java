package project.query;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import project.controller.ArenaManager;
import project.entity.ArenaComparableObject;
import project.entity.ArenaObject;
import project.query.ArenaObjectStorage.StoredComparableType;
import project.query.ArenaObjectStorage.SortOption;
import project.query.ArenaObjectStorage.StoredType;

/**
 * A class that selects the {@link ArenaObject}s inside a defined rectangle (including the boundary) within the arena.
 */
public class ArenaObjectRectangleSelector extends ArenaObjectSortedSelector {

    /**
     * Minimum x-coordinate of the rectangle.
     */
    protected final short leftX;

    /**
     * Minimum y-coordinate of the rectangle.
     */
    protected final short topY;

    /**
     * Length of the rectangle in the x-direction.
     */
    protected final short width;

    /**
     * Length of the rectangle in the y-direction.
     */
    protected final short height;

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
     * Constructs a newly allocated {@link ArenaObjectRectangleSelector} object.
     * @param leftX The minimum x-coordinate of the rectangle.
     * @param topY The minimum y-coordinate of the rectangle.
     * @param width The x-length of the rectangle.
     * @param height The y-length of the rectangle.
     */
    public ArenaObjectRectangleSelector(short leftX, short topY, short width, short height) {
        this.leftX = leftX;
        this.topY = topY;
        this.width = width;
        this.height = height;

        this.effectiveWidth = (short) (width - Math.max(0, - leftX) - Math.max(0, leftX + width - ArenaManager.ARENA_WIDTH));
        this.effectiveHeight = (short) (height - Math.max(0, - topY) - Math.max(0, topY + height - ArenaManager.ARENA_HEIGHT));

        this.startX = (short) Math.max(0, leftX); assert startX >= 0;
        this.endX = (short) (startX + effectiveWidth); assert endX <= ArenaManager.ARENA_WIDTH;
        assert startX <= endX;

        this.startY = (short) Math.max(0, topY); assert startY >= 0;
        this.endY = (short) (startY + effectiveHeight); assert endX <= ArenaManager.ARENA_HEIGHT;
        assert startY <= endY;
    }

    @Override
    float estimateSelectivity(ArenaObjectStorage storage) {
        // Out of bounds (== 0 means a point search)
        if (effectiveWidth < 0 || effectiveHeight < 0) return 0;

        return ((float) ((effectiveWidth + 1) * (effectiveHeight + 1))) / ((ArenaManager.ARENA_WIDTH + 1) * (ArenaManager.ARENA_HEIGHT + 1));
    }

    @Override
    LinkedList<ArenaObject> select(ArenaObjectStorage storage,
            EnumSet<StoredType> types, LinkedList<ArenaObjectSelector> filters) {

        LinkedList<ArenaObject> result = new LinkedList<>();

        // Out of bounds (== 0 means a point search)
        if (effectiveWidth < 0 || effectiveHeight < 0) return result;
        
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
    PriorityQueue<ArenaComparableObject> select(ArenaObjectStorage storage,
            EnumSet<StoredComparableType> types, LinkedList<ArenaObjectSortedSelector> filters, SortOption option) {
        
        PriorityQueue<ArenaComparableObject> result = createPriorityQueue(option);

        // Out of bounds (== 0 means a point search)
        if (effectiveWidth < 0 || effectiveHeight < 0) return result;

        if (effectiveWidth < effectiveHeight) {
            ArrayList<LinkedList<ArenaObject>> index = storage.getXIndex();

            for (short x = startX; x <= endX; x++) {
                for (ArenaObject o : index.get(x)) {
                    if (isComparableAndAllSatisfied(o, types, filters)) result.add((ArenaComparableObject) o);
                }
            }
        } else {
            ArrayList<LinkedList<ArenaObject>> index = storage.getYIndex();

            for (short y = startY; y <= endY; y++) {
                for (ArenaObject o : index.get(y)) {
                    if (isComparableAndAllSatisfied(o, types, filters)) result.add((ArenaComparableObject) o);
                }
            }
        }

        return result;
    }

    @Override
    boolean isInSelection(ArenaObject o) {
        short x = o.getX();
        if (x < startX || x > endX) return false;

        short y = o.getY();
        if (y < startY || y > endY) return false;

        return true;
    }
} 
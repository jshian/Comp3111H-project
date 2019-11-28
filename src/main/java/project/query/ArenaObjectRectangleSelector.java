package project.query;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;

import project.control.ArenaManager;
import project.entity.ArenaObject;
import project.query.ArenaObjectStorage.StoredType;

/**
 * A class that selects the {@link ArenaObject}s inside a defined rectangle (including the boundary) within the arena.
 */
public class ArenaObjectRectangleSelector implements ArenaObjectSelector {

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
     * @param width The x-length of the rectangle, must be non-negative.
     * @param height The y-length of the rectangle, must be non-negative.
     */
    public ArenaObjectRectangleSelector(short leftX, short topY, short width, short height) {
        if (width < 0) throw new IllegalArgumentException(String.format("The width must be non-negative. Value: %d", width));
        if (height < 0) throw new IllegalArgumentException(String.format("The height must be non-negative. Value: %d", height));

        this.leftX = leftX;
        if (leftX < 0) this.startX = 0;
        else if (leftX > ArenaManager.ARENA_WIDTH) this.startX = ArenaManager.ARENA_WIDTH;
        else this.startX = leftX;

        this.topY = topY;
        if (topY < 0) this.startY = 0;
        else if (topY > ArenaManager.ARENA_HEIGHT) this.startY = ArenaManager.ARENA_HEIGHT;
        else this.startY = topY;

        this.width = width;
        if (leftX + width < 0) this.endX = 0;
        else if (leftX + width > ArenaManager.ARENA_WIDTH) this.endX = ArenaManager.ARENA_WIDTH;
        else this.endX = (short) (leftX + width);
        this.effectiveWidth = (short) (endX - startX);

        this.height = height;
        if (topY + height < 0) this.endY = 0;
        else if (topY + height > ArenaManager.ARENA_HEIGHT) this.endY = ArenaManager.ARENA_HEIGHT;
        else this.endY = (short) (topY + height);
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
        
        if (effectiveWidth < effectiveHeight) {
            ArrayList<LinkedList<ArenaObject>> index = storage.getXIndex();

            for (short x = startX; x <= endX; x++) {
                for (ArenaObject o : index.get(x)) {
                    if (o.getY() < startY || o.getY() > endY) continue;
                    if (isAllSatisfied(o, types, filters)) result.add(o);
                }
            }
        } else {
            ArrayList<LinkedList<ArenaObject>> index = storage.getYIndex();

            for (short y = startY; y <= endY; y++) {
                for (ArenaObject o : index.get(y)) {
                    if (o.getX() < startX || o.getX() > endX) continue;
                    if (isAllSatisfied(o, types, filters)) result.add(o);
                }
            }
        }

        return result;
    }

    @Override
    public boolean isInSelection(ArenaObject o) {
        short x = o.getX();
        if (x < startX || x > endX) return false;

        short y = o.getY();
        if (y < startY || y > endY) return false;

        return true;
    }
} 
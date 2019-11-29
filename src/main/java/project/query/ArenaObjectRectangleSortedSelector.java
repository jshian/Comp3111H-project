package project.query;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import project.entity.ArenaObject;
import project.query.ArenaObjectStorage.StoredComparableType;
import project.query.ArenaObjectStorage.SortOption;

/**
 * A class that selects the {@link ArenaObject}s inside a defined rectangle (including the boundary) within the arena.
 * @param <T> The type of comparable {@link ArenaObject} that is selected.
 */
public class ArenaObjectRectangleSortedSelector<T extends ArenaObject & Comparable<T>>
        extends ArenaObjectRectangleSelector implements ArenaObjectSortedSelector<T> {

    /**
     * Constructs a newly allocated {@link ArenaObjectRectangleSortedSelector} object.
     * @param leftX The minimum x-coordinate of the rectangle.
     * @param topY The minimum y-coordinate of the rectangle.
     * @param width The x-length of the rectangle, must be non-negative.
     * @param height The y-length of the rectangle, must be non-negative.
     */
    public ArenaObjectRectangleSortedSelector(short leftX, short topY, short width, short height) {
        super(leftX, topY, width, height);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> select(ArenaObjectStorage storage,
            StoredComparableType type, List<ArenaObjectSortedSelector<T>> filters, SortOption option) {
        
        List<T> result = new LinkedList<>();

        // Out of bounds (== 0 means a point search)
        if (effectiveWidth < 0 || effectiveHeight < 0) return result;

        if (effectiveWidth < effectiveHeight) {
            ArrayList<List<ArenaObject>> index = storage.getXIndex();

            for (short x = startX; x <= endX; x++) {
                for (ArenaObject o : index.get(x)) {
                    if (o.getY() < startY || o.getY() > endY) continue;
                    if (isComparableAndAllSatisfied(o, type, filters)) result.add((T) o);
                }
            }
        } else {
            ArrayList<List<ArenaObject>> index = storage.getYIndex();

            for (short y = startY; y <= endY; y++) {
                for (ArenaObject o : index.get(y)) {
                    if (o.getX() < startX || o.getX() > endX) continue;
                    if (isComparableAndAllSatisfied(o, type, filters)) result.add((T) o);
                }
            }
        }

        sortResult(result, option);

        return result;
    }
} 
package project.query;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import project.entity.ArenaObject;
import project.query.ArenaObjectStorage.StoredComparableType;
import project.query.ArenaObjectStorage.SortOption;

/**
 * A class that selects the {@link ArenaObject}s inside a defined circle (including the boundary) within the arena.
 * @param <T> The type of comparable {@link ArenaObject} that is selected.
 */
public class ArenaObjectCircleSortedSelector<T extends ArenaObject & Comparable<T>>
        extends ArenaObjectCircleSelector implements ArenaObjectSortedSelector<T> {

    /**
     * Constructs a newly allocated {@link ArenaObjectCircleSortedSelector} object.
     * @param centerX The center x-coordinate of the circle.
     * @param centerY The center y-coordinate of the circle.
     * @param radius The radius of the circle.
     */
    public ArenaObjectCircleSortedSelector(short centerX, short centerY, short radius) {
        super(centerX, centerY, radius);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> select(ArenaObjectStorage storage,
            StoredComparableType type, List<ArenaObjectSortedSelector<T>> filters, SortOption option) {
        
        List<T> result = new LinkedList<>();

        // Out of bounds (== 0 means a point search)
        if (effectiveWidth < 0 || effectiveHeight < 0) return result;
        
        // Determine whether to access through x- or y- index based on number of accesses.
        if (effectiveWidth < effectiveHeight) {
            ArrayList<List<ArenaObject>> index = storage.getXIndex();

            for (short x = startX; x <= endX; x++) {
                for (ArenaObject o : index.get(x)) {
                    if (isInSelection(o)) {
                        if (isComparableAndAllSatisfied(o, type, filters)) result.add((T) o);
                    }
                }
            }
        } else {
            ArrayList<List<ArenaObject>> index = storage.getYIndex();

            for (short y = startY; y <= endY; y++) {
                for (ArenaObject o : index.get(y)) {
                    if (isInSelection(o)) {
                        if (isComparableAndAllSatisfied(o, type, filters)) result.add((T) o);
                    }
                }
            }
        }

        sortResult(result, option);

        return result;
    }
} 
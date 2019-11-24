package project.query;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.function.Predicate;

import project.entity.ArenaObject;
import project.query.ArenaObjectStorage.StoredComparableType;
import project.query.ArenaObjectStorage.SortOption;

/**
 * A class that selects the {@link ArenaObject}s based on their properties.
 * Note that any object that is not an instance of the specified type will be discarded.
 * @param <T> The type of comparable {@link ArenaObject} that is selected.
 */
public class ArenaObjectPropertySortedSelector<T extends ArenaObject & Comparable<T>>
        extends ArenaObjectPropertySelector<T> implements ArenaObjectSortedSelector<T> {

    /**
     * Constructs a newly allocated {@link ArenaObjectPropertySortedSelector} object.
     * @param objectType The type of object the predicate applies to.
     * @param predicate The predicate to satisfy.
     */
    public ArenaObjectPropertySortedSelector(Class<T> objectType, Predicate<T> predicate) {
        super(objectType, predicate);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PriorityQueue<T> select(ArenaObjectStorage storage, StoredComparableType type,
            LinkedList<ArenaObjectSortedSelector<T>> filters, SortOption option) {
        
        PriorityQueue<T> result = createPriorityQueue(option);

        if (type.getObjectClass().isAssignableFrom(objectType)) {
            for (ArenaObject o : storage.getIndexFor(type)) {
                if (isComparableAndAllSatisfied(o, type, filters)) {
                    if (predicate.test((T) o)) result.add((T) o);
                }
            }
        }

        return result;
    }
} 
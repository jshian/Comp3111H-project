package project.query;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import project.entity.ArenaObject;
import project.query.ArenaObjectStorage.StoredComparableType;
import project.query.ArenaObjectStorage.SortOption;

/**
 * A class that selects the {@link ArenaObject}s based on their properties.
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

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> select(ArenaObjectStorage storage, StoredComparableType type,
            List<ArenaObjectSortedSelector<T>> filters, SortOption option) {
        
        List<T> result = new LinkedList<>();

        if (type.getObjectClass().isAssignableFrom(objectType)) {
            for (ArenaObject o : storage.getSortedIndexFor(type, option)) {
                if (isComparableAndAllSatisfied(o, type, filters)) {
                    if (predicate.test((T) o)) result.add((T) o);
                }
            }
        }

        return result;
    }
} 
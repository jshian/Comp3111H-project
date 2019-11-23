package project.query;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.function.Predicate;

import project.entity.ArenaComparableObject;
import project.entity.ArenaObject;
import project.query.ArenaObjectStorage.StoredComparableType;
import project.query.ArenaObjectStorage.SortOption;
import project.query.ArenaObjectStorage.StoredType;

/**
 * A class that selects the {@link ArenaObject}s based on their properties.
 * Note that any object that is not an instance of the specified type will be discarded.
 * @param <T> The type of object the predicate applies to.
 */
public class ArenaObjectPropertySelector<T extends ArenaObject> extends ArenaObjectSortedSelector {

    /**
     * The type of object the predicate applies to.
     */
    protected Class<T> objectType;

    /**
     * The predicate to satisfy.
     */
    protected Predicate<T> predicate;

    /**
     * Constructs a newly allocated {@link ArenaObjectPropertySelector} object.
     * @param objectType The type of object the predicate applies to.
     * @param predicate The predicate to satisfy.
     */
    public ArenaObjectPropertySelector(Class<T> objectType, Predicate<T> predicate) {
        this.objectType = objectType;
        this.predicate = predicate;
    }

    @Override
    float estimateSelectivity(ArenaObjectStorage storage) {
        return 1;
    }

    @Override
    @SuppressWarnings("unchecked")
    LinkedList<ArenaObject> select(ArenaObjectStorage storage,
            EnumSet<StoredType> types, LinkedList<ArenaObjectSelector> filters) {
        
        LinkedList<ArenaObject> result = new LinkedList<>();

        for (StoredType type : types) {
            if (type.getObjectClass().isAssignableFrom(objectType)) {
                for (ArenaObject o : storage.getIndexFor(type)) {
                    if (predicate.test((T) o)) result.add(o);
                }
            }
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    PriorityQueue<ArenaComparableObject> select(ArenaObjectStorage storage,
            EnumSet<StoredComparableType> types, LinkedList<ArenaObjectSortedSelector> filters, SortOption option) {
        
        PriorityQueue<ArenaComparableObject> result = createPriorityQueue(option);

        for (StoredComparableType type : types) {
            if (type.getObjectClass().isAssignableFrom(objectType)) {
                for (ArenaComparableObject o : storage.getIndexFor(type)) {
                    if (predicate.test((T) o)) result.add(o);
                }
            }
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    boolean isInSelection(ArenaObject o) {
        if (!objectType.isAssignableFrom(o.getClass())) return false;

        return predicate.test((T) o);
    }
} 
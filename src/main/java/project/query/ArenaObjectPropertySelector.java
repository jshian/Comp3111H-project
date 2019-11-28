package project.query;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.function.Predicate;

import project.entity.ArenaObject;
import project.query.ArenaObjectStorage.StoredType;

/**
 * A class that selects the {@link ArenaObject}s based on their properties.
 * Note that any object that is not an instance of the specified type will be discarded.
 * @param <T> The type of {@link ArenaObject} that is selected.
 */
public class ArenaObjectPropertySelector<T extends ArenaObject> implements ArenaObjectSelector {

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
    public int estimateCost(ArenaObjectStorage storage) {
        return Integer.MAX_VALUE; // So that the query will always search by type
    }

    @Override
    @SuppressWarnings("unchecked")
    public LinkedList<ArenaObject> select(ArenaObjectStorage storage, EnumSet<StoredType> types,
            LinkedList<ArenaObjectSelector> filters) {
        
        LinkedList<ArenaObject> result = new LinkedList<>();

        for (StoredType type : types) {
            if (type.getObjectClass().isAssignableFrom(objectType)) {
                for (ArenaObject o : storage.getIndexFor(type)) {
                    if (isAllSatisfied(o, types, filters)) {
                        if (predicate.test((T) o)) result.add((T) o);
                    }
                }
            }
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isInSelection(ArenaObject o) {
        if (!objectType.isAssignableFrom(o.getClass())) return false;

        return predicate.test((T) o);
    }
} 
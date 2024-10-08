package project.query;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int estimateCost(ArenaObjectStorage storage) {
        return Integer.MAX_VALUE; // So that the query will always search by type
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ArenaObject> select(ArenaObjectStorage storage, EnumSet<StoredType> types,
            List<ArenaObjectSelector> filters) {
        
        List<ArenaObject> result = new LinkedList<>();

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

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean isInSelection(ArenaObject o) {
        if (!objectType.isAssignableFrom(o.getClass())) return false;

        return predicate.test((T) o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean isInSelectionByDefinition(ArenaObject o) {
        // Wrong object type, can't apply predicate
        if (!objectType.isAssignableFrom(o.getClass())) return false;

        // Definition
        return predicate.test((T) o);
    }
} 
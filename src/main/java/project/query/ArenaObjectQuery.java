package project.query;

import java.util.EnumSet;
import java.util.LinkedList;

import project.entity.ArenaObject;
import project.query.ArenaObjectStorage.StoredType;

/**
 * A query on the {@link ArenaObjectStorage}.
 */
class ArenaObjectQuery {

    /**
     * A list of selectors that collectively restricts the result.
     */
    protected LinkedList<ArenaObjectSelector> selectors = new LinkedList<>();

    /**
     * Constructs a newly allocated {@link ArenaObjectQuery} object with no selectors.
     */
    public ArenaObjectQuery() {}

    /**
     * Constructs a newly allocated {@link ArenaObjectQuery} object with one selector.
     * @param selector The selector.
     */
    public ArenaObjectQuery(ArenaObjectSelector selector) {
        this.selectors.add(selector);
    }

    /**
     * Constructs a newly allocated {@link ArenaObjectQuery} object with multiple selectors.
     * @param selectors The list of selectors.
     */
    public ArenaObjectQuery(LinkedList<ArenaObjectSelector> selectors) {
        this.selectors = new LinkedList<>(selectors);
    }

    /**
     * Adds a selector that further restricts the selection.
     * @param selector The selector to be added.
     */
    public void restrict(ArenaObjectSelector selector) {
        if (!selectors.contains(selector)) {
            selectors.add(selector);
        }
    }

    /**
     * Runs the query on a storage.
     * @param storage The storage to run the query on.
     * @param types The types of {@link ArenaObject} to select.
     * @return The query result.
     */
    LinkedList<ArenaObject> run(ArenaObjectStorage storage, EnumSet<StoredType> types) {
        // Return everything if there are no selectors
        if (selectors.isEmpty()) {
            LinkedList<ArenaObject> result = new LinkedList<>();

            for (StoredType type : types) {
                result.addAll(storage.getIndexFor(type));
            }

            return result;
        }

        // Query using the minSelector and apply the other selections as the results are being fetched
        float minSelectivity = Float.POSITIVE_INFINITY;
        ArenaObjectSelector minSelector = null;
        for (ArenaObjectSelector selector : selectors) {
            float selectivity = selector.estimateSelectivity(storage);
            if (selectivity < minSelectivity) {
                minSelectivity = selectivity;
                minSelector = selector;
            }
        }

        assert (minSelector != null);

        LinkedList<ArenaObjectSelector> otherSelectors = new LinkedList<>(selectors);
        otherSelectors.remove(minSelector);

        return minSelector.select(storage, types, otherSelectors);
    }
}
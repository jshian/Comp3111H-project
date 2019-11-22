package project.query;

import java.util.LinkedList;

import project.entity.ArenaObject;

/**
 * Interface for queries on the {@link ArenaObjectStorage}
 */
interface ArenaObjectQuery {

    /**
     * Runs the query.
     * @return The query result.
     */
    abstract LinkedList<ArenaObject> query();
}
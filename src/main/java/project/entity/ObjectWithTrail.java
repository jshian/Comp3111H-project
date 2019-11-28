package project.entity;

import java.util.List;

/**
 * Interface for {@link ArenaObject}s that leave a trail.
 */
interface ObjectWithTrail {

    /**
     * Returns the positions that the object has passed through during the previous frame.
     * @return The positions that the object has passed through during the previous frame.
     */
    abstract List<ArenaObjectPositionInfo> getTrail();

}

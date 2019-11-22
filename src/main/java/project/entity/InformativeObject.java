package project.entity;

/**
 * Interface for objects that are informative.
 */
interface InformativeObject {
    
    /**
     * Returns the display name of the object.
     * @return The display name of the object.
     */
    abstract String getDisplayName();

    /**
     * Returns the displayed detailed information about the object.
     * @return The displayed detailed information about the object.
     */
    abstract String getDisplayDetails();
}
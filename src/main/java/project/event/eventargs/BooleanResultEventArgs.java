package project.event.eventargs;

/**
 * Struct containing event data about a boolean result.
 */
public class BooleanResultEventArgs extends EventArgs {

    /**
     * The recipient of the result.
     */
    public Object recipient;
    
    /**
     * The value of the result.
     */
    public boolean result;

}
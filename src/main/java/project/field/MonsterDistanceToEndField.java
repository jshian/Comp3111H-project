package project.field;

import java.util.EnumSet;
import java.util.LinkedList;

import project.arena.ArenaEventRegister;
import project.arena.ArenaInstance;
import project.control.ArenaManager;
import project.entity.ArenaObject;
import project.entity.Tower;
import project.event.EventHandler;
import project.event.eventargs.ArenaObjectEventArgs;
import project.query.ArenaObjectGridSelector;
import project.query.ArenaObjectStorage;
import project.query.ArenaObjectStorage.StoredType;

/**
 * A scalar field where the value on each point equals the minimum distance
 * travelled from that point to the end zone.
 */
public final class MonsterDistanceToEndField extends ArenaScalarField<Short> {
    
    /**
     * The method invoked when an {@link ArenaObject} is being added.
     */
    private EventHandler<ArenaObjectEventArgs> onAddObject = (sender, args) -> {
        if (args.subject instanceof Tower) {
            recalculate(ArenaManager.getActiveObjectStorage());
        }
    };

    /**
     * The method invoked when an {@link ArenaObject} is being removed.
     */
    private EventHandler<ArenaObjectEventArgs> onRemoveObject = (sender, args) -> {
        if (args.subject instanceof Tower) {
            recalculate(ArenaManager.getActiveObjectStorage());
        }
    };

    /**
     * Constructs a newly allocated {@link MonsterDistanceToEndField} object and attaches it to an arena instance.
     * @param arenaInstance The arena instance.
     */
    public MonsterDistanceToEndField(ArenaInstance arenaInstance) {
        super(arenaInstance);
        
        recalculate(arenaInstance.getStorage());
        
        ArenaEventRegister register = arenaInstance.getEventRegister();
        register.ARENA_OBJECT_ADD.subscribe(onAddObject);
        register.ARENA_OBJECT_REMOVE.subscribe(onRemoveObject);
    }

    /**
     * Recalculates the entire scalar field.
     * @param storage The storage to base the calculation on.
     */
    private void recalculate(ArenaObjectStorage storage) {
        // Reset values
        setAll(Short.MAX_VALUE);

        // Calculate distance
    	LinkedList<ScalarFieldPoint> openSet = new LinkedList<>();
        openSet.add(new ScalarFieldPoint(ArenaManager.END_X, ArenaManager.END_Y));

        setValueAt(ArenaManager.END_X, ArenaManager.END_Y, (short) 0);
    	while (!openSet.isEmpty()) {
            ScalarFieldPoint current = openSet.poll();
            short currentX = current.getX();
            short currentY = current.getY();
    		// Monsters can only travel horizontally or vertically
    		for (ScalarFieldPoint neighbour : getTaxicabNeighbours(currentX, currentY)) {
                short neighbourX = neighbour.getX();
                short neighbourY = neighbour.getY();

                // Monsters can only go to grids that do not contain a Tower
                ArenaObjectGridSelector selector = new ArenaObjectGridSelector(neighbourX, neighbourY);
                if (storage.getQueryResult(selector, EnumSet.of(StoredType.TOWER)).isEmpty()) {
        			short newCost = (short) (getValueAt(currentX, currentY) + 1);
        			if (getValueAt(neighbourX, neighbourY) > newCost) {
                        setValueAt(neighbourX, neighbourY, newCost);
        				openSet.add(new ScalarFieldPoint(neighbourX, neighbourY));
        			}
    			}
    		}
        }
    }
}
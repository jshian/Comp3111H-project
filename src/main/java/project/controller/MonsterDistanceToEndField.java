package project.controller;

import java.util.LinkedList;

import project.entity.ArenaObject;
import project.entity.Tower;
import project.event.EventHandler;
import project.event.eventargs.ArenaObjectEventArgs;
import project.event.eventsets.ArenaObjectIOEvent;

/**
 * A scalar field where the value on each point equals the minimum distance
 * travelled from that point to the end zone.
 */
public final class MonsterDistanceToEndField extends ArenaScalarField<Integer> {
    private EventHandler<ArenaObjectEventArgs> onAddObject = (sender, args) -> {
        if (args.subject instanceof Tower) {
            recalculate();
        }
    };


    private EventHandler<ArenaObjectEventArgs> onRemoveObject = (sender, args) -> {
        if (args.subject instanceof Tower) {
            recalculate();
        }
    };

    /**
     * Constructs a newly allocated MonsterDistanceToEndField object.
     */
    public MonsterDistanceToEndField() {
        ArenaEventManager.OBJECT_IO.subscribe(ArenaObjectIOEvent.ADD, onAddObject);
        ArenaEventManager.OBJECT_IO.subscribe(ArenaObjectIOEvent.REMOVE, onRemoveObject);
    }

    /**
     * Recalculates the entire scalar field.
     */
    private void recalculate() {
        // Reset values
        setAll(Integer.MAX_VALUE);

        // Calculate distance
    	LinkedList<ScalarFieldPoint> openSet = new LinkedList<>();
        openSet.add(new ScalarFieldPoint(ArenaManager.END_X, ArenaManager.END_Y));

        setValueAt(ArenaManager.END_X, ArenaManager.END_Y, 0);
    	while (!openSet.isEmpty()) {
            ScalarFieldPoint current = openSet.poll();
            short currentX = current.getX();
            short currentY = current.getY();
    		// Monsters can only travel horizontally or vertically
    		for (ScalarFieldPoint neighbour : getTaxicabNeighbours(currentX, currentY)) {
                short neighbourX = neighbour.getX();
                short neighbourY = neighbour.getY();
    			// Monsters can only go to grids that do not contain a Tower
    			if (arena.findObjectsInGrid(c, EnumSet.of(Arena.TypeFilter.Tower)).isEmpty()) {
        			int newCost = getValueAt(currentX, currentY) + 1;
        			if (getValueAt(neighbourX, neighbourY) > newCost ) {
                        setValueAt(neighbourX, neighbourY, newCost);
        				openSet.add(new ScalarFieldPoint(neighbourX, neighbourY));
        			}
    			}
    		}
        }
    }
}
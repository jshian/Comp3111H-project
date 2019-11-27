package project.event;

import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import project.event.eventargs.EventArgs;

/**
 * Tests the {@link EventManager} class.
 */
public class EventManagerTester extends EventManager<EventArgs> {
	@Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private boolean is_h1_called = false;
    private boolean is_h2_called = false;
    private boolean is_h3_called = false;

    private void reset() {
        is_h1_called = false;
        is_h2_called = false;
        is_h3_called = false;
    }

    @Test
    public void test() {
        EventHandler<EventArgs> h1 = (sender, args) -> { is_h1_called = true; };
        EventHandler<EventArgs> h2 = (sender, args) -> { is_h2_called = true; };
        EventHandler<EventArgs> h3 = (sender, args) -> { is_h3_called = true; };
        
        assertTrue(subscribers.isEmpty());

        subscribe(h1); assertTrue(subscribers.size() == 1);
        invoke(this, new EventArgs()); assertTrue(is_h1_called && !is_h2_called && !is_h3_called);
        reset();

        subscribe(h2); assertTrue(subscribers.size() == 2);
        invoke(this, new EventArgs()); assertTrue(is_h1_called && is_h2_called && !is_h3_called);
        reset();

        subscribe(h1); assertTrue(subscribers.size() == 2); // No repetition
        invoke(this, new EventArgs()); assertTrue(is_h1_called && is_h2_called && !is_h3_called);
        reset();

        subscribe(h3); assertTrue(subscribers.size() == 3);
        invoke(this, new EventArgs()); assertTrue(is_h1_called && is_h2_called && is_h3_called);
        reset();

        unsubscribe(h2); assertTrue(subscribers.size() == 2);
        invoke(this, new EventArgs()); assertTrue(is_h1_called && !is_h2_called && is_h3_called);
        reset();

        unsubscribe(h3); assertTrue(subscribers.size() == 1);
        invoke(this, new EventArgs()); assertTrue(is_h1_called && !is_h2_called && !is_h3_called);
        reset();

        unsubscribe(h3); assertTrue(subscribers.size() == 1); // Already unsubscribed
        invoke(this, new EventArgs()); assertTrue(is_h1_called && !is_h2_called && !is_h3_called);
        reset();

        unsubscribe(h1); assertTrue(subscribers.isEmpty());
        invoke(this, new EventArgs()); assertTrue(!is_h1_called && !is_h2_called && !is_h3_called);
        reset();
    }
}
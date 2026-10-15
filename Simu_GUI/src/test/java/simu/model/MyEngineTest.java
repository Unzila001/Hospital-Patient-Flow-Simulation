package simu.model;

import controller.Controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simu.framework.Event;
import simu.framework.EventList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for the MyEngine class.
 * <p>
 * Verifies the initialization, configuration,
 * service points and event scheduling of the
 * hospital patient flow simulation engine.
 * <p>
 * Mockito is used to avoid launching the
 * JavaFX user interface during testing.
 */
public class MyEngineTest {

    private TestableMyEngine engine;

    /**
     * Creates a new simulation engine before each test.
     */
    @BeforeEach
    void setUp() {
        Controller controller = mock(Controller.class);
        engine = new TestableMyEngine(controller, 5);
    }

    /**
     * Tests that the simulation engine is created
     * successfully.
     */
    @Test
    void testEngineInitialization() {
        assertNotNull(engine);
    }

    /**
     * Tests that the simulation delay can be
     * set and retrieved correctly.
     */
    @Test
    void testSimulationDelay() {
        engine.setDelay(100);

        assertEquals(100, engine.getDelay());
    }

    /**
     * Tests that the simulation delay can be
     * changed after initialization.
     */
    @Test
    void testSimulationDelayChange() {
        engine.setDelay(100);
        engine.setDelay(200);

        assertEquals(200, engine.getDelay());
    }

    /**
     * Tests that the simulation engine creates
     * exactly six hospital service points.
     */
    @Test
    void testServicePointCount() {
        ServicePoint[] points = engine.getServicePoints();

        assertNotNull(points);
        assertEquals(6, points.length);
    }

    /**
     * Tests that all six service points have
     * the correct names and order.
     */
    @Test
    void testServicePointNames() {
        ServicePoint[] points = engine.getServicePoints();

        assertEquals("Registration", points[0].getName());
        assertEquals("Triage", points[1].getName());
        assertEquals("Doctor", points[2].getName());
        assertEquals("X-ray", points[3].getName());
        assertEquals("Laboratory", points[4].getName());
        assertEquals("Treatment", points[5].getName());
    }

    /**
     * Tests that the service points have
     * the expected patient capacities.
     * <p>
     * The Doctor service point has capacity two,
     * while the other service points have capacity one.
     */
    @Test
    void testServicePointCapacities() {
        ServicePoint[] points = engine.getServicePoints();

        assertEquals(1, points[0].getCapacity());
        assertEquals(1, points[1].getCapacity());
        assertEquals(2, points[2].getCapacity());
        assertEquals(1, points[3].getCapacity());
        assertEquals(1, points[4].getCapacity());
        assertEquals(1, points[5].getCapacity());
    }

    /**
     * Tests that every service point is initialized
     * with an empty patient waiting queue.
     */
    @Test
    void testServicePointQueuesInitiallyEmpty() {
        ServicePoint[] points = engine.getServicePoints();

        for (ServicePoint point : points) {
            assertEquals(0, point.getQueueSize());
        }
    }

    /**
     * Tests that simulation initialization schedules
     * the first patient arrival event.
     */
    @Test
    void testFirstArrivalEventIsScheduled() {
        engine.initializeForTest();

        Event firstEvent = engine.getEventListForTest().remove();

        assertNotNull(firstEvent);
        assertEquals(EventType.ARRIVAL, firstEvent.getType());
    }

    /**
     * Test subclass that exposes selected protected
     * engine functionality for unit testing.
     * <p>
     * This allows tests to inspect service points
     * and initialization without modifying MyEngine.
     */
    private static class TestableMyEngine extends MyEngine {

        /**
         * Creates a testable simulation engine.
         *
         * @param controller mocked application controller
         * @param randomSeed seed for random distributions
         */
        TestableMyEngine(Controller controller, long randomSeed) {
            super(controller, randomSeed);
        }

        /**
         * Returns the simulation service points.
         *
         * @return array of hospital service points
         */
        ServicePoint[] getServicePoints() {
            return servicePoints;
        }

        /**
         * Executes the engine initialization method.
         */
        void initializeForTest() {
            initialization();
        }

        /**
         * Returns the simulation event list.
         *
         * @return the event list
         */
        EventList getEventListForTest() {
            return eventList;
        }
    }
}

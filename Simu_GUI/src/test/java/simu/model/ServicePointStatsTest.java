package simu.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ServicePointStatsTest {

    //Test 1: Verify default constructor values
    @Test
    void testDefaultConstructor() {

        ServicePointStats stats = new ServicePointStats();

        assertNull(stats.getServiceName());
        assertEquals(0.0, stats.getAverageWaitingTime());
        assertEquals(0, stats.getMaxQueueLength());
        assertEquals(0, stats.getCurrentCapacity());
        assertEquals(0.0, stats.getTotalServiceTime());
        assertEquals(0, stats.getNumberStarted());
        assertEquals(0.0, stats.getUtilization());
    }
//===============================================================================//
    //Test 2: Verify constructor stores all statistics correctly
    @Test
    void testConstructorWithValues() {

        ServicePointStats stats = new ServicePointStats(
                "Doctor",
                12.5,
                8,
                2,
                150.0,
                20,
                75.0
        );

        assertEquals("Doctor", stats.getServiceName());
        assertEquals(12.5, stats.getAverageWaitingTime());
        assertEquals(8, stats.getMaxQueueLength());
        assertEquals(2, stats.getCurrentCapacity());
        assertEquals(150.0, stats.getTotalServiceTime());
        assertEquals(20, stats.getNumberStarted());
        assertEquals(75.0, stats.getUtilization());
    }
//===============================================================================//
    //Test 3: Verify setters and getters work correctly
    @Test
    void testSettersAndGetters() {

        ServicePointStats stats = new ServicePointStats();

        //set values using setters
        stats.setServiceName("Laboratory");
        stats.setAverageWaitingTime(15.5);
        stats.setMaxQueueLength(6);
        stats.setCurrentCapacity(1);
        stats.setTotalServiceTime(200.0);
        stats.setNumberStarted(25);
        stats.setUtilization(80.0);

        assertEquals("Laboratory", stats.getServiceName());
        assertEquals(15.5, stats.getAverageWaitingTime());
        assertEquals(6, stats.getMaxQueueLength());
        assertEquals(1, stats.getCurrentCapacity());
        assertEquals(200.0, stats.getTotalServiceTime());
        assertEquals(25, stats.getNumberStarted());
        assertEquals(80.0, stats.getUtilization());
    }
//===============================================================================//
    //Test 4: Verify that existing statistics can be updated correctly
    @Test
    void testUpdatingStatistics() {

        ServicePointStats stats = new ServicePointStats(
                "Doctor",
                10.0,
                5,
                2,
                100.0,
                15,
                60.0
        );

        //Update statistics
        stats.setAverageWaitingTime(20.0);
        stats.setMaxQueueLength(10);
        stats.setUtilization(85.0);

        //Verify updated values
        assertEquals(20.0, stats.getAverageWaitingTime());
        assertEquals(10, stats.getMaxQueueLength());
        assertEquals(85.0, stats.getUtilization());

        //Verify other values remain unchanged
        assertEquals("Doctor", stats.getServiceName());
        assertEquals(2, stats.getCurrentCapacity());
        assertEquals(100.0, stats.getTotalServiceTime());
        assertEquals(15, stats.getNumberStarted());
    }
//===============================================================================//
    //Test 5: Verify zero-value statistics are stored correctly
    @Test
    void testZeroValueStatistics() {

        ServicePointStats stats = new ServicePointStats(
                "X-ray",
                0.0,
                0,
                1,
                0.0,
                0,
                0.0
        );

        assertEquals("X-ray", stats.getServiceName());
        assertEquals(0.0, stats.getAverageWaitingTime());
        assertEquals(0, stats.getMaxQueueLength());
        assertEquals(1, stats.getCurrentCapacity());
        assertEquals(0.0, stats.getTotalServiceTime());
        assertEquals(0, stats.getNumberStarted());
        assertEquals(0.0, stats.getUtilization());
    }
//===============================================================================//
}

package simu.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import simu.framework.Trace;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

    private static Customer customer;
    private double totalTime = 0;

    @BeforeAll
    static void setCustomer() throws Exception{
        Trace.setTraceLevel(Trace.Level.INFO);
        customer = new Customer();
    }

    @Test
    void testArrivalTime(){
        customer.setArrivalTime(0.00);
        assertEquals(0.00, customer.getArrivalTime(), 0.01, "Not correct value");
    }

    @Test
    void testRemovalTime(){
        customer.setRemovalTime(0.2);
        assertEquals(0.2, customer.getRemovalTime(), 0.01, "Not correct value");
    }

    @Test
    void testTotalHospitalTime(){
        customer.setTotalHospitalTime();
        totalTime = 0.2 - 0.00;
        assertEquals(totalTime, customer.getTotalHospitalTime(), 0.01, "Not correct value");
    }

    @DisplayName("Test patient condition failing and working")
    @Test
    void testPatientCondition(){
        customer.setPatientCondition("cured");
        assertEquals("", customer.getPatientCondition());

        customer.setPatientCondition("minor injury");
        assertEquals("minor injury", customer.getPatientCondition());
    }

    @DisplayName("Test priority logic working")
    @Test
    void testPriority(){
        customer.setPriority(true);
        assertTrue(customer.hasPriority());

        customer.setPriority(false);
        assertFalse(customer.hasPriority());
    }

    @Test
    void testGetId(){
        assertEquals(1, customer.getId());
    }

    @Test
    void testResultReport(){
        String[] expectedResults = {
                "\nCustomer 1 ready! ",
                "New customer 1 arrived at 0,00 minutes.",
                "Customer 1 removed at 0,20 minutes.",
                "Customer 1 stayed 0,00 minutes.",
                "Customer 1 had condition: minor injury",
                "Customer 1 didn't have priority.",
                "Current mean patient time in hospital: 0,00 minutes.",
                "Current amount of patients with minor injuries: 1",
                "Current amount of patients with serious injury: 0",
                "Current amount of patients with illnesses: 0"
        };

        assertArrayEquals(expectedResults, customer.reportResults(), "The arrays do not match");
    }
}

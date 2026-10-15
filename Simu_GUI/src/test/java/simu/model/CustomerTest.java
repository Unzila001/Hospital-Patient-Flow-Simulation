package simu.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import simu.framework.Trace;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

    private Customer customer;
    private double totalTime = 0;

    @BeforeEach
    void setCustomer() throws Exception{
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
        customer.setArrivalTime(0.00);
        customer.setRemovalTime(0.20);
        customer.setTotalHospitalTime();

        assertEquals(0.20, customer.getTotalHospitalTime(), 0.01, "Not correct value");
    }

    @DisplayName("Test patient condition failing and working")
    @Test
    void testPatientCondition(){
        customer.setPatientCondition("cured");
        assertEquals("", customer.getPatientCondition());

        customer.setPatientCondition("illness");
        assertEquals("illness", customer.getPatientCondition());

        customer.setPatientCondition("serious injury");
        assertEquals("serious injury", customer.getPatientCondition());

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
        assertTrue(customer.getId() > 0);
    }

    @Test
    void testResultReport(){

        customer.setArrivalTime(0.00);
        customer.setRemovalTime(0.20);
        customer.setPatientCondition("minor injury");
        customer.setPriority(false);
        customer.setTotalHospitalTime();

        int id = customer.getId();
        String[] expectedResults = {
                "\nCustomer " + id + " ready! ",
                "New customer " + id + " arrived at 0,00 minutes.",
                "Customer " + id + " removed at 0,20 minutes.",
                "Customer " + id + " stayed 0,20 minutes.",
                "Customer " + id + " had condition: minor injury",
                "Customer " + id + " didn't have priority.",
                "Current mean patient time in hospital: 0,20 minutes.",
                "Current amount of patients with minor injuries: 1",
                "Current amount of patients with serious injury: 0",
                "Current amount of patients with illnesses: 0"
        };

        assertArrayEquals(expectedResults, customer.reportResults(), "The arrays do not match");
    }
}

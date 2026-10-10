package simu.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import simu.framework.Trace;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {

    private static Customer customer;

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
        double correctResult = 0.2 - 0.00;
        assertEquals(correctResult, customer.getTotalHospitalTime(), 0.01, "Not correct value");
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



}

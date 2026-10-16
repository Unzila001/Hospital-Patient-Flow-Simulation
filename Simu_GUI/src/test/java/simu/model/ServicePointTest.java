package simu.model;

import controller.Controller;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import simu.framework.Trace;
import eduni.distributions.Normal;
import simu.framework.EventList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServicePointTest {

    //Test 1: Verify that a newly created service point has the correct name and capacity.
    @Test
    void testServicePointNameAndCapacity() {

        //Create a service point with capacity 2
        ServicePoint servicePoint = new ServicePoint(
                null,
                null,
                null,
                "Doctor",
                2,
                null
        );

        //Check the service point name
        assertEquals("Doctor", servicePoint.getName());

        //Check the service point capacity
        assertEquals(2, servicePoint.getCapacity());
    }
//===============================================================================//
    //Test 2: Verify that the  minimum service capacity is always 1.
    @Test
    void testMinimumCapacity() {

        //Create a service point with capacity 0
        ServicePoint servicePoint = new ServicePoint(
                null,
                null,
                null,
                "Registration",
                0,
                null
        );

        //Check the service point capacity
        assertEquals(1, servicePoint.getCapacity());
    }
//===============================================================================//
    //Test 3: Verify that a newly created service point has an empty waiting queue.
    @Test
    void testEmptyQueue() {

        ServicePoint servicePoint = new ServicePoint(
                null,
                null,
                null,
                "Triage",
                1,
                null
        );

        assertEquals(0, servicePoint.getQueueLength());
    }
//===============================================================================//
    //Test 4: Verify that a service point is not reserved when its service capacity is available
    @Test
    void testServicePointNotReserved() {

        ServicePoint servicePoint = new ServicePoint(
                null,
                null,
                null,
                "Doctor",
                2,
                null
        );

        assertFalse(servicePoint.isReserved());
    }
//===============================================================================//
    //Test 5:Verify that a new service point has zero statistics
    @Test
    void testInitialStatistics() {

        ServicePoint servicePoint = new ServicePoint(
                null,
                null,
                null,
                "Registration",
                1,
                null
        );

        assertEquals(0, servicePoint.getCustomersStartedService());
        assertEquals(0, servicePoint.getMaximumQueueLength());
        assertEquals(0.0, servicePoint.getTotalServiceTime());
        assertEquals(0.0, servicePoint.getAverageWaitingTime());
    }
//===============================================================================//
    //Task 5: Verify that the utilization is zero when simulation time is zero or negative
    @Test
    void testUtilizationWithInvalidTime() {

        ServicePoint servicePoint = new ServicePoint(
                null,
                null,
                null,
                "Doctor",
                2,
                null
        );

        //Check utilization with zero simulation time
        assertEquals(0.0, servicePoint.getUtilization(0));

        //Check utilization with negative simulation time
        assertEquals(0.0, servicePoint.getUtilization(-10));
    }
//===============================================================================//
    //Test 7: Verify that a patient is added to the waiting queue.
    @Test
    void testAddPatientToQueue() {

        //Initialize tracing before creating a patient
        Trace.setTraceLevel(Trace.Level.INFO);

        //Create a mock controller to avoid starting JavaFX
        Controller mockController = mock(Controller.class);

        //Create a service point
        ServicePoint servicePoint = new ServicePoint(
                null,
                null,
                null,
                "Registration",
                1,
                mockController
        );

        //Create a patient
        Customer patient = new Customer();

        //Add patient to the waiting queue
        servicePoint.addQueue(patient);

        //Check that the queue length is 1
        assertEquals(1, servicePoint.getQueueLength());

        //Check that queue is not empty
        assertTrue(servicePoint.isOnQueue());

        //Verify that controller was notified
        verify(mockController).updateWaitingStatus(patient.getId(), true);
    }
//===============================================================================//
    //Test 8: Verify that priority patients start service before normal patients.
    @Test
    void testPriorityPatientsServedFirst() {

        Trace.setTraceLevel(Trace.Level.INFO);
        Controller mockController = mock(Controller.class);

        ServicePoint servicePoint = new ServicePoint(
                new Normal(10, 6, 5),
                new EventList(),
                EventType.REGISTRATION_COMPLETE,
                "Registration",
                1,
                mockController
        );

        //Normal patient arrive first
        Customer normalPatient = new Customer();
        normalPatient.setPriority(false);

        //Priority patient arrives second
        Customer priorityPatient = new Customer();
        priorityPatient.setPriority(true);

        servicePoint.addQueue(normalPatient);
        servicePoint.addQueue(priorityPatient);

        //Start service for the first patient in the queue
        servicePoint.beginService();

        //Only one patient should have started service
        assertEquals(1, servicePoint.getCustomersInService());

        //Priority patient should no longer be waiting
        verify(mockController).updateWaitingStatus(priorityPatient.getId(), false);

        //Normal patient should still be waiting
        assertEquals(1, servicePoint.getQueueLength());
    }
//===============================================================================//
    //Test 9: Verify that the maximum queue length is updated correctly when patients are added to the queue.
    @Test
    void testMaximumQueueLength() {

        Trace.setTraceLevel(Trace.Level.INFO);
        Controller mockController = mock(Controller.class);

        ServicePoint servicePoint = new ServicePoint(
                null,
                null,
                null,
                "Registration",
                1,
                mockController
        );

        //Add three patients to the queue
        Customer patient1 = new Customer();
        Customer patient2 = new Customer();
        Customer patient3 = new Customer();

        servicePoint.addQueue(patient1);
        servicePoint.addQueue(patient2);
        servicePoint.addQueue(patient3);

        //Check that the maximum queue length is 3
        assertEquals(3, servicePoint.getQueueLength());
        assertEquals(3, servicePoint.getMaximumQueueLength());
    }
//===============================================================================//
    //Test 10: Verify service point capacity and reserved status
    @Test
    void testServicePointCapacity() {

        Trace.setTraceLevel(Trace.Level.INFO);
        Controller mockController = mock(Controller.class);

        ServicePoint servicePoint = new ServicePoint(
                new Normal(10, 6, 5),
                new EventList(),
                EventType.DOCTOR_COMPLETE,
                "Doctor",
                2,
                mockController
        );

        //Add two patients to the service point
        Customer patient1 = new Customer();
        Customer patient2 = new Customer();
        Customer patient3 = new Customer();

        servicePoint.addQueue(patient1);
        servicePoint.addQueue(patient2);
        servicePoint.addQueue(patient3);

        //Initially no doctors are busy
        assertFalse(servicePoint.isReserved());

        //Start service for patient
        servicePoint.beginService();

        //Only two patients should be in service due to capacity
        assertEquals(2, servicePoint.getCustomersInService());

        //Both doctors are busy (full capacity)
        assertTrue(servicePoint.isReserved());

        //Third patient should still be waiting
        assertEquals(1, servicePoint.getQueueLength());
    }
//===============================================================================//

}
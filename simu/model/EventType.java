package simu.model;

import simu.framework.IEventType;

/**
 * Event types for the Hospital Patient Flow Simulation.
 *
 * NOTE for the team: EventType.java is officially Unna's area (Person 1).
 * This is a DRAFT extended version needed to test the full A-B-C cycle
 * end-to-end for the ABC-Results branch. Please review together before
 * merging - rename freely if you prefer different naming.
 */
public enum EventType implements IEventType {
	ARR1,               // patient arrival
	DEP_REGISTRATION,   // registration completed
	DEP_TRIAGE,         // triage completed
	DEP_DOCTOR,         // doctor consultation completed
	DEP_LAB,            // laboratory test completed
	DEP_XRAY,           // x-ray imaging completed
	DEP_TREATMENT       // treatment completed -> patient departs
}

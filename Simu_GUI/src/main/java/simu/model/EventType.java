package simu.model;

import simu.framework.IEventType;

/**
 * Defines the event types used in the hospital simulation.
 *
 * Each event type represents and event that can occur during the patient's progress through the hospital system.
 */
public enum EventType implements IEventType {
	ARRIVAL, REGISTRATION_COMPLETE, TRIAGE_COMPLETE, DOCTOR_COMPLETE, LABORATORY_COMPLETE, XRAY_COMPLETE, TREATMENT_COMPLETE;
}
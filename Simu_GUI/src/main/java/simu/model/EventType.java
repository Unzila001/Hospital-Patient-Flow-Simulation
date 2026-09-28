package simu.model;

import simu.framework.IEventType;

/**
 * Event types are defined by the requirements of the simulation model
 *
 * TODO: This must be adapted to the actual simulator
 */
public enum EventType implements IEventType {
	ARRIVAL, REGISTRATION_COMPLETE, TRIAGE_COMPLETE, DOCTOR_COMPLETE, LABORATORY_COMPLETE, XRAY_COMPLETE, TREATMENT_COMPLETE;
}
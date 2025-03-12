package simu.model;

/**
 * EventType is an enumeration class that numerates the types of events that can occur in the simulation model.
 */
public enum EventType {
	/**
	 * Arrival of a customer
	 */
	ARRIVAL,
	/**
	 * Departure of a customer from ticket booth
	 */
	DEP_TICKET_BOOTH,
	/**
	 * Departure of a customer from ride
	 */
	DEP_RIDE,
	/**
	 * Departure of a customer from restaurant
	 */
	DEP_RESTAURANT,
}

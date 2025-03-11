package simu.framework;

import simu.model.EventType;

/**
 * Event is the class for the events in the simulation. The class is used to manage the events in the simulation.
 */
public class Event implements Comparable<Event> {
	
	/**
	 * The type of the event.
	 */
	private EventType type;
	/**
	 * The time of the event.
	 */
	private double time;

	/**
	 * Constructor for the Event class.
	 * @param type The type of the event (Arrival, Departure, etc.).
	 * @param time The time of the event.
	 */
	public Event(EventType type, double time){
		this.type = type;
		this.time = time;
	}

	/**
	 * Sets the type of the event.
	 * @param type The type of the event (Arrival, Departure, etc.).
	 */
	public void setType(EventType type) {
		this.type = type;
	}
	/**
	 * Gets the type of the event.
	 * @return The type of the event.
	 */
	public EventType getType() {
		return type;
	}
	/**
	 * Sets the time of the event.
	 * @param time The time of the event.
	 */
	public void setTime(double time) {
		this.time = time;
	}
	/**
	 * Gets the time of the event.
	 * @return The time of the event.
	 */
	public double getTime() {
		return time;
	}

	/**
	 * Compares the time of the event to another event.
	 * @param arg The event to compare to.
	 * @return -1 if the time is less than the other event, 1 if the time is greater, 0 if the times are equal.
	 */
	@Override
	public int compareTo(Event arg) {
		if (this.time < arg.time) return -1;
		else if (this.time > arg.time) return 1;
		return 0;
	}
	
	
	

}

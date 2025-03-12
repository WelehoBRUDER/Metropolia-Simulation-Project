package simu.framework;

import java.util.PriorityQueue;

/**
 * EventList is a class that represents a list of events in the simulation model.
 */
public class EventList {
	/**
	 * The list of events in the simulation model.
	 */
	private PriorityQueue<Event> list = new PriorityQueue<Event>();

	/**
	 * Constructor for the EventList class.
	 */
	public EventList(){
	 
	}

	/**
	 * Removes the next event from the list.
	 * @return The next event in the list.
	 */
	public Event remove(){
		return list.remove();
	}

	/**
	 * Adds an event to the list.
	 * @param t The event to be added to the list.
	 */
	public void add(Event t){
		list.add(t);
	}

	/**
	 * Checks if there are any events in the list.
	 * @return Time of the next event.
	 */
	public double getNextTime(){
		return list.peek().getTime();
	}
	
	
}

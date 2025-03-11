package simu.framework;
import distributions.*;
import simu.model.EventType;

/**
 * ArrivalProgress is the class for the arrival process in the simulation. The class is used to manage new arrivals in the simulation.
 */
public class ArrivalProgress {

	/**
	 * The generator for the arrival process.
	 */
	private ContinuousGenerator generator;
	/**
	 * The event list for the arrival process.
	 */
	private EventList eventList;
	/**
	 * The type of the event.
	 */
	private EventType type;

	/**
	 * Constructor for the ArrivalProgress class.
	 * @param g The continuous generator for how often the arrivals occur.
	 * @param tl The event list for the arrival process.
	 * @param type The type of the event.
	 */
	public ArrivalProgress(ContinuousGenerator g, EventList tl, EventType type){
		this.generator = g;
		this.eventList = tl;
		this.type = type;
	}

	/**
	 * Generates the next event for the arrival process.
	 */
	public void generateNext(){
		Event t = new Event(type, Clock.getInstance().getTime()+generator.sample());
		eventList.add(t);
	}

}

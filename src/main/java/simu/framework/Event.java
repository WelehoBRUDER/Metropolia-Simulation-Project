package simu.framework;

import simu.model.EventType;

public class Event implements Comparable<Event> {
	
		
	private EventType type;
	private double time;
	
	public Event(EventType type, double time){
		this.type = type;
		this.time = time;
	}
	
	public void setType(EventType type) {
		this.type = type;
	}
	public EventType getType() {
		return type;
	}
	public void setTime(double time) {
		this.time = time;
	}
	public double getTime() {
		return time;
	}

	@Override
	public int compareTo(Event arg) {
		if (this.time < arg.time) return -1;
		else if (this.time > arg.time) return 1;
		return 0;
	}
	
	
	

}

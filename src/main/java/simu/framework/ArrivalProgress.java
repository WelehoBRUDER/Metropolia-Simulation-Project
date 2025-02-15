package simu.framework;
import distributions.*;
import simu.model.EventType;
public class ArrivalProgress {
	
	private ContinuousGenerator generator;
	private EventList eventList;
	private EventType type;

	public ArrivalProgress(ContinuousGenerator g, EventList tl, EventType type){
		this.generator = g;
		this.eventList = tl;
		this.type = type;
	}

	public void generateNext(){
		Event t = new Event(type, Clock.getInstance().getTime()+generator.sample());
		eventList.add(t);
	}

}

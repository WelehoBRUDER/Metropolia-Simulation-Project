package sim.model;

import sim.framework.*;

import java.util.ArrayList;

// TODO:
// Asiakas koodataan simulointimallin edellyttämällä tavalla (data!)
public class Customer {
	private double arrivalTime;
	private double departTime;
	private int id;
	private static int i = 1;
	private static long sum = 0;
	private boolean wristband;
	private boolean ticket;
	private ArrayList<EventType> eventList = new ArrayList<>();
	
	public Customer(){
	    id = i++;
		if (id%2 == 0){
	        wristband = true;
	    } else {
	        wristband = false;
	    }

		for (int i = 0; i <= (int) (Math.random() * 3 + 1) ; i++) {
			EventType newEvent = EventType.values()[(int) (Math.random() * 3 + 2)];
			while (!eventList.contains(newEvent)) {
				eventList.add(newEvent);
			}
		}

		String eventString = "";
		for (EventType eventType: eventList) {
			if (eventType == eventList.get(eventList.size() - 1)) {
				eventString += eventType;
			} else {
				eventString += eventType + ", ";
			}
		}

		Trace.out(Trace.Level.INFO, "Customer " + id + " wants to go to rides: " + eventString);

		arrivalTime = Clock.getInstance().getTime();
		Trace.out(Trace.Level.INFO, "New customer number " + id + " arrived at  "+arrivalTime);
		if (wristband) {
			Trace.out(Trace.Level.INFO, "Customer " + id + " has a wristband");
		} else {
			Trace.out(Trace.Level.INFO, "Customer " + id + " does not have a wristband");
		}
	}

	public double getDepartTime() {
		return departTime;
	}

	public void setDepartTime(double departTime) {
		this.departTime = departTime;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(double arrivalTime) {
			this.arrivalTime = arrivalTime;
	}

	public int getId() {
		return id;
	}

	public void addTicket(){
		ticket = true;
	}

	public void removeTicket(){
		ticket = false;
	}

	public boolean isWristband() {
		return wristband;
	}

	public boolean isTicket() {
		return ticket;
	}


	public boolean ridesLeft() {
		return !eventList.isEmpty();
	}

	public EventType getNextRide() {
		return eventList.remove(0);
	}

	public EventType peekNextRide() {
		return eventList.get(0);
	}

	public void removeNextRide() {
		if (!eventList.isEmpty())
			eventList.remove(0);
	}

	public int getEventListSize() {
		return eventList.size();
	}

	public static double getAverage() {
		return (double)sum/i;
	}

	public static int getI() {
		return i; // i = customer amount
	}

	public void report(){
		Trace.out(Trace.Level.INFO, "\nCustomer "+id+ " done! ");
		Trace.out(Trace.Level.INFO, "Customer "+id+ " arrived at: " +arrivalTime);
		Trace.out(Trace.Level.INFO,"Customer "+id+ " departed at: " +departTime);
		Trace.out(Trace.Level.INFO,"Customer "+id+ " stayed for: " +(departTime-arrivalTime));
		sum += (departTime-arrivalTime);
		double average = sum/id;
		System.out.println("Average time customers spent in the service:  "+ average);
	}

}

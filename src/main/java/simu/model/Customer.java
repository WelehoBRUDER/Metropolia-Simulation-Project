package simu.model;

import simu.framework.Clock;
import simu.framework.Trace;

import java.util.ArrayList;


// TODO:
// Asiakas koodataan simulointimallin edellyttämällä tavalla (data!)
public class Customer {
	private double arrivalTime;
	private double departureTime;
	private int id;
	private static int i = 1;
	private static long sum = 0;
	private boolean wristband;
	private ArrayList<Integer> rideList = new ArrayList<>();
	private boolean inRestaurant = false;
	private final int RIDE_COUNT;
	
	public Customer(int RIDE_COUNT, double WRISTBAND_CHANCE) {
	    id = i++;
		this.RIDE_COUNT = RIDE_COUNT;

		if (Math.random() < WRISTBAND_CHANCE) {
			wristband = true;
		} else {
			wristband = false;
		}

		for (int i = 0; i < (int) (Math.random() * RIDE_COUNT + 1) ; i++) {
			int tempInt;

			do {
				tempInt = (int) (Math.random() * RIDE_COUNT + 1);
			} while (rideList.contains(tempInt));

			rideList.add(tempInt);
		}
	    
		arrivalTime = Clock.getInstance().getTime();
		Trace.out(Trace.Level.INFO, "Uusi asiakas " + id + ". saapumisaika: "+arrivalTime + " Ranneke: " + wristband + ". Laitteet: " + ridesToSring());

	}

	public int getId() {
		return id;
	}

	public boolean hasWristband() {
		return wristband;
	}

	public boolean ridesLeft() {
		return !rideList.isEmpty();
	}

	public int getNextRideID() {
		return rideList.get(0);
	}

	public void removeNextRide() {
		rideList.remove(0);
	}

	public void setInRestaurant(boolean inRestaurant) {
		this.inRestaurant = inRestaurant;
	}

	public boolean InRestaurant() {
		return inRestaurant;
	}

	public double getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(double departureTime) {
		this.departureTime = departureTime;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String ridesToSring() {
		String rides = "";
		for (int eventID : rideList) {
			rides += "ride " + eventID + " ";
		}
		return rides;
	}
	
	public void report(){
		Trace.out(Trace.Level.INFO, "Asiakas "+id+ " saapui:" +arrivalTime);
		Trace.out(Trace.Level.INFO,"Asiakas "+id+ " poistui:" +departureTime);
		Trace.out(Trace.Level.INFO,"Asiakas "+id+ " viipyi:" +(departureTime-arrivalTime));
		sum += (departureTime-arrivalTime);
		double average = sum/id;
		System.out.println("Asiakkaiden läpimenoaikojen keskiarvo "+ average);
	}

}

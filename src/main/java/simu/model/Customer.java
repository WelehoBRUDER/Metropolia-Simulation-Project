package simu.model;

import simu.framework.Clock;
import simu.framework.Trace;
import java.util.ArrayList;



public class Customer {
	private double arrivalTime;
	private double departureTime;
	private int id;
	private static int i = 1;
	private static double wristbandSum = 0;
	private static int wristbandCount = 0;
	private static double ticketSum = 0;
	private static int ticketCount = 0;
	private boolean wristband;
	private ArrayList<Integer> rideList = new ArrayList<>();
	private boolean inRestaurant = false;
	private double queueArrivalTime;
	private double queueDepartureTime;
	private int ticketboothCounter = 0;
	private static int ticketboothCounterSum = 0;
	private int tickets = 0;
	private int maxTicketPurchase;
	private int minTicketPurchase;
	private static int totalTicketCount = 0;
	
	public Customer(int rideCount, double wristbandChance, int minTicketPurchase, int maxTicketPurchase) {
	    id = i++;
		this.minTicketPurchase = minTicketPurchase;
		this.maxTicketPurchase = maxTicketPurchase;

		if (wristbandChance == 1) {
			wristband = true;
		} else {
			wristband = false;

		}


		for (int i = 0; i < (int) (Math.random() * rideCount + 1) ; i++) {
			int tempInt;

			do {
				tempInt = (int) (Math.random() * rideCount + 1);
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

	public static int getI() {
		return i;
	}

	public void setQueueArrivalTime(double queueArrivalTime) {
		this.queueArrivalTime = queueArrivalTime;
	}

	public double getQueueArrivalTime() {
		return queueArrivalTime;
	}

	public void setQueueDepartureTime(double queueDepartureTime) {
		this.queueDepartureTime = queueDepartureTime;
	}

	public double getQueueDepartureTime() {
		return queueDepartureTime;
	}

	public void incrementTicketboothCounter() {
		ticketboothCounter++;
	}

	public void addTicketboothCounterSum() {
		ticketboothCounterSum += ticketboothCounter;
	}

	public static double getTicketboothCounterAverage() {
		return (double) ticketboothCounterSum /ticketCount;
	}

	public double report(){
		Trace.out(Trace.Level.INFO, "Asiakas "+id+ " saapui:" +arrivalTime);
		Trace.out(Trace.Level.INFO,"Asiakas "+id+ " poistui:" +departureTime);
		Trace.out(Trace.Level.INFO,"Asiakas "+id+ " viipyi:" +(departureTime-arrivalTime));
		double average;
		if (wristband) {
			wristbandCount++;
			wristbandSum += (departureTime - arrivalTime);
			average = wristbandSum / wristbandCount;
			System.out.println("Rannekkeellisten asiakkaiden läpimenoaikojen keskiarvo "+ average);

		} else {
			ticketCount++;
			System.out.println("Asiakas kävi " + ticketboothCounter + " kertaa lipunmyyntipisteessä");
			addTicketboothCounterSum();
			ticketSum += (departureTime - arrivalTime);
			average = ticketSum / ticketCount;
			System.out.println("Lippu-asiakkaiden läpimenoaikojen keskiarvo "+ average);
		}
		return average;
	}

	public void addTickets() {
		int amount = (int)(Math.random() * maxTicketPurchase) + minTicketPurchase;
		totalTicketCount += amount;
		tickets += amount;
	}

	public int getTickets() {
		return tickets;
	}

	public void removeTicket() {
		tickets -= 1;
	}

	public static int getTotalTicketCount() {
		return totalTicketCount;
	}


}

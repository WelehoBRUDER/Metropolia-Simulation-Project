package simu.model;

import simu.framework.Clock;
import simu.framework.Trace;

import java.util.ArrayList;

/**
 * Customer is the class for customers in the simulation model. Contains data for the customers and methods for handling the data.
 */

public class Customer {
	/** Arrival time of the customer in the system */
	private double arrivalTime;
	/** Departure time of the customer from the system */
	private double departureTime;
	/** Unique id of the customer */
	private int id;
	/** Static variable for generating unique id's */
	private static int i = 1;
	/** Static variable for calculating the sum of wristband customer's time in the system */
	private static double wristbandSum = 0;
	/** Static variable for counting the amount of wristband customers */
	private static int wristbandCount = 0;
	/** Static variable for calculating the sum of ticket customer's time in the system */
	private static double ticketSum = 0;
	/** Static variable for counting the amount of ticket customers */
	private static int ticketCount = 0;
	/** Boolean for determining if the customer has a wristband */
	private boolean wristband;
	/** List of ride id's the customer wants to ride */
	private ArrayList<Integer> rideList = new ArrayList<>();
	/** Boolean for determining if the customer is in the restaurant */
	private boolean inRestaurant = false;
	/** Arrival time of the customer in the service point queue */
	private double queueArrivalTime;
	/** Departure time of the customer from the service point queue */
	private double queueDepartureTime;
	/** Counter for counting how many times the customer has visited the ticket booth */
	private int ticketboothCounter = 0;
	/** Static variable for calculating the sum of ticket booth visits in the simulation */
	private static int ticketboothCounterSum = 0;
	/** Amount of tickets the customer has */
	private int tickets = 0;
	/** Maximum amount of tickets the customer can purchase */
	private int maxTicketPurchase;
	/** Minimum amount of tickets the customer can purchase */
	private int minTicketPurchase;
	/** Static variable for counting the total amount of tickets purchased in the simulation */
	private static int totalTicketCount = 0;

	/**
	 * Constructor for the Customer class. Initializes the customer with the given parameters.
	 * Sets a random amount of rides for the customer to ride and arrival time for the customer.
	 * @param rideCount Amount of rides in the simulation
	 * @param wristbandChance Chance of the customer having a wristband
	 * @param minTicketPurchase Minimum amount of tickets the customer can purchase
	 * @param maxTicketPurchase Maximum amount of tickets the customer can purchase
	 */
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
		Trace.out(Trace.Level.INFO, "New customer " + id + ". arrival time: "+arrivalTime + " Wristband: " + wristband + ". Rides: " + ridesToSring());

	}

	/**
	 * Getter for the customer's id
	 * @return Customer's id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Getter for the customer's wristband
	 * @return True if the customer has a wristband, false if not
	 */
	public boolean hasWristband() {
		return wristband;
	}

	/**
	 * Checks if the customer has rides left
	 * @return True if the customer has rides left, false if not
	 */
	public boolean ridesLeft() {
		return !rideList.isEmpty();
	}

	/**
	 * Getter for the next ride id
	 * @return Next ride id
	 */
	public int getNextRideID() {
		return rideList.get(0);
	}

	/**
	 * Removes the next ride from the ride list
	 */
	public void removeNextRide() {
		rideList.remove(0);
	}

	/**
	 * Setter for the customer's inRestaurant boolean
	 * @param inRestaurant True if the customer is in the restaurant, false if not
	 */
	public void setInRestaurant(boolean inRestaurant) {
		this.inRestaurant = inRestaurant;
	}

	/**
	 * Getter for the customer's inRestaurant boolean
	 * @return True if the customer is in the restaurant, false if not
	 */
	public boolean InRestaurant() {
		return inRestaurant;
	}

	/**
	 * Getter for the customer's departure time
	 * @return Departure time of the customer
	 */
	public double getDepartureTime() {
		return departureTime;
	}

	/**
	 * Setter for the customer's departure time
	 * @param departureTime Departure time of the customer
	 */
	public void setDepartureTime(double departureTime) {
		this.departureTime = departureTime;
	}

	/**
	 * Getter for the customer's arrival time
	 * @return Arrival time of the customer
	 */
	public double getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * Setter for the customer's arrival time
	 * @param arrivalTime Arrival time of the customer
	 */
	public void setArrivalTime(double arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	/**
	 * Converts the ride list to a string
	 * @return String of the ride list
	 */
	public String ridesToSring() {
		String rides = "";
		for (int eventID : rideList) {
			rides += "ride " + eventID + " ";
		}
		return rides;
	}

	/**
	 * Getter for the static variable i, the unique id generator
	 * @return Static variable i
	 */
	public static int getI() {
		return i;
	}

	public static void setI(int num) {
		i = num;
	}

	/**
	 * Setter for the customer's queue arrival time
	 * @param queueArrivalTime Queue arrival time of the customer
	 */
	public void setQueueArrivalTime(double queueArrivalTime) {
		this.queueArrivalTime = queueArrivalTime;
	}

	/**
	 * Getter for the customer's queue arrival time
	 * @return Queue arrival time of the customer
	 */
	public double getQueueArrivalTime() {
		return queueArrivalTime;
	}

	/**
	 * Setter for the customer's queue departure time
	 * @param queueDepartureTime Queue departure time of the customer
	 */
	public void setQueueDepartureTime(double queueDepartureTime) {
		this.queueDepartureTime = queueDepartureTime;
	}

	/**
	 * Getter for the customer's queue departure time
	 * @return Queue departure time of the customer
	 */
	public double getQueueDepartureTime() {
		return queueDepartureTime;
	}

	/**
	 * Increments the ticketbooth counter
	 */
	public void incrementTicketboothCounter() {
		ticketboothCounter++;
	}

	/**
	 * Adds the ticketbooth counter to the sum
	 */
	public void addTicketboothCounterSum() {
		ticketboothCounterSum += ticketboothCounter;
	}

	/**
	 * Getter for the customer´s average times visited the ticket booth
	 * @return Average times visited the ticket booth
	 */
	public static double getTicketboothCounterAverage() {
		return (double) ticketboothCounterSum /ticketCount;
	}

	/**
	 * Method for reporting the customer's time in the system. Calculates the average time for wristband and ticket customers.
	 * @return Average time for wristband or ticket customers
	 */
	public double report(){
		Trace.out(Trace.Level.INFO, "Customer "+id+ " arrived:" +arrivalTime);
		Trace.out(Trace.Level.INFO,"Customer "+id+ " left:" +departureTime);
		Trace.out(Trace.Level.INFO,"Customer "+id+ " stayed for:" +(departureTime-arrivalTime));
		double time = departureTime - arrivalTime;
		if (wristband) {
			wristbandCount++;
			wristbandSum += (time);
		} else {
			ticketCount++;
			addTicketboothCounterSum();
			ticketSum += (time);
		}
		return time;
	}

	/**
	 * Adds tickets to the customer. Generates a random amount of tickets between the min and max ticket purchase values.
	 * Adds the amount of tickets to the total ticket count in the simulation.
	 */
	public void addTickets() {
		int amount = (int)(Math.random() * maxTicketPurchase) + minTicketPurchase;
		totalTicketCount += amount;
		tickets += amount;
	}

	/**
	 * Getter for the customer's amount of tickets
	 * @return Amount of tickets the customer has
	 */
	public int getTickets() {
		return tickets;
	}

	/**
	 * Removes one ticket from the customer
	 */
	public void removeTicket() {
		tickets -= 1;
	}

	/**
	 * Getter for the total ticket count in the simulation
	 * @return Total ticket count in the simulation
	 */
	public static int getTotalTicketCount() {
		return totalTicketCount;
	}


}
package simu.model;

import distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;
import simu.framework.Trace;

import java.util.*;

/**
 * ServicePoint is the class for the service points in the simulation. The class is used to manage the service points and the customers in the service points.
 */
public class ServicePoint {
	/**
	 * The queue of customers in the service point.
	 */
	protected final LinkedList<Customer> queue = new LinkedList<>(); // Tietorakennetoteutus
	/**
	 * The continuous generator for the service point. Used to generate service times.
	 */
	protected final ContinuousGenerator generator;
	/**
	 * The event list for the service point.
	 */
	protected final EventList eventList;
	/**
	 * The type of the scheduled event.
	 */
	protected final EventType scheduledEventType;
	/**
	 * The ID of the ride.
	 */
	protected int rideID = 0;
	/**
	 * The counter for the ride ID for the rides in the simulation.
	 */
	protected static int i = 0;
	/**
	 * The counter for the ride ID for ticket booths in the simulation.
	 */
	protected static int j = 0;
	/**
	 * The service times for the service point.
	 */
	private HashMap<Integer, List<Double>> serviceTimes = new HashMap<>();
	/**
	 * The queue times for the service point.
	 */
	private HashMap<Integer, List<Double>> queueTimes = new HashMap<>();
	/**
	 * The counter for the customers in the service point.
	 */
	private int customerCounter = 0;
	/**
	 * The boolean for the reservation of the service point.
	 */
	protected boolean reserved = false;

	/**
	 * The constructor for the ServicePoint class. The constructor initializes the service point with the given parameters and sets the ride ID (service point ID) for the service point.
	 * @param generator The generator for the service point.
	 * @param eventList The event list for the service point.
	 * @param type      The type of the scheduled event.
	 * @param rideCount The number of rides in the simulation.
	 */
	public ServicePoint(ContinuousGenerator generator, EventList eventList, EventType type, int rideCount){
		this.eventList = eventList;
		this.generator = generator;
		this.scheduledEventType = type;
		if (this.scheduledEventType == EventType.DEP_TICKET_BOOTH) {
			j--;
			rideID = j;
		} else if (this.scheduledEventType == EventType.DEP_RESTAURANT) {
			rideID = rideCount + 2;
		}else {
			i++;
			rideID = i;
		}
	}

	/**
	 * The method to get the ride ID of the service point.
	 * @return The ride ID of the service point.
	 */
	public int getRideID() {
		return rideID;
	}


	/**
	 * The method to add a customer to the queue of the service point.
	 * @param a The customer to be added to the queue.
	 */
	public void addToQueue(Customer a){   // Jonon 1. asiakas aina palvelussa
		queue.add(a);
		a.setQueueArrivalTime(Clock.getInstance().getTime());
	}

	/**
	 * The method to fetch a customer from the queue of the service point.
	 * @return The customer fetched from the queue.
	 */
	public Customer fetchFromQueue(){  // Poistetaan palvelussa ollut
		reserved = false;
		return queue.poll();
	}

	/**
	 * The method to begin a new service in the service point. The method starts a new service and the customer is in the queue during the service.
	 * Scheduled event is added to the event list at the end of the service time. Service time is sampled from the generator.
	 */
	public void beginService(){  //Aloitetaan uusi palvelu, asiakas on jonossa palvelun aikana
		queue.peek().setQueueDepartureTime(Clock.getInstance().getTime());
		addQueueTime(queue.peek());
		double serviceTime = generator.sample();
		addServiceTime(rideID, serviceTime);
		Trace.out(Trace.Level.INFO, "Service time: " + serviceTime + ", at service point: " + rideID + " , ready: " + (Clock.getInstance().getTime() + serviceTime));
		reserved = true;
		eventList.add(new Event(scheduledEventType, Clock.getInstance().getTime()+serviceTime));
	}

	/**
	 * The method to check if the service point is reserved.
	 * @return The boolean value for the reservation of the service point.
	 */
	public boolean isReserved(){
		return reserved;
	}

	/**
	 * The method to check if the service point has customers in the queue.
	 * @return The boolean value for the customers in the queue of the service point.
	 */
	public boolean isInQueue(){
		return queue.size() != 0;
	}

	/**
	 * The method to fetch a customer from the customer list of the service point.
	 * @return The customer fetched from the customer list.
	 */
	public Customer fetchFromCustomerList() {
		return null;
	}

	/**
	 * The method to get the size of the customer list of the service point.
	 * @return The size of the customer list.
	 */
	public int getCustomerListSize() {
		return 0;
	}

	/**
	 * The method to get the average service time of the service point.
	 * @return The average service time of the service point.
	 */
	public double getAverageServiceTime() {
		return (serviceTimes.get(rideID).get(0) / serviceTimes.get(rideID).get(1));
	}

	/**
	 * The method to add the service time of a customer to the service point.
	 * @param servicePointID The ID of the service point.
	 * @param serviceTime    The service time of the customer.
	 */
	public void addServiceTime(int servicePointID, double serviceTime){
			if (!serviceTimes.containsKey(servicePointID)) {
				serviceTimes.put(servicePointID, new ArrayList<>(Arrays.asList(serviceTime, 1.0)));
			} else {
				serviceTimes.get(rideID).set(0, serviceTimes.get(rideID).get(0) + serviceTime);
				serviceTimes.get(rideID).set(1, serviceTimes.get(rideID).get(1) + 1);
			}
		}

	/**
	 * The method to get the average queue time of the service point.
	 * @return The average queue time of the service point.
	 */
	public double getAverageQueueTime() {
		if (queueTimes.containsKey(rideID)) {
			return queueTimes.get(rideID).get(0) / queueTimes.get(rideID).get(1);
		} else {
			return 0;
		}
	}

	/**
	 * The method to add the queue time of a customer to the service point.
	 * @param customer The customer whose queue time is added to the service point.
	 */
	public void addQueueTime(Customer customer){
		double queueTime = customer.getQueueDepartureTime() - customer.getQueueArrivalTime();
		if (!queueTimes.containsKey(rideID)) {
			queueTimes.put(rideID, new ArrayList<>(Arrays.asList(queueTime, 1.0)));
		} else {
			queueTimes.get(rideID).set(0, queueTimes.get(rideID).get(0) + queueTime);
			queueTimes.get(rideID).set(1, queueTimes.get(rideID).get(1) + 1);
		}
	}

	/**
	 * The method to increment the customer counter of the service point.
	 */
	public void incrementCustomerCounter() {
		customerCounter++;
	}
	/**
	 * The method to get the customer counter of the service point.
	 * @return The customer counter of the service point.
	 */
	public int getCustomerCounter() {
		return customerCounter;
	}
}

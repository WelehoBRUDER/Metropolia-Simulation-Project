package simu.model;

import distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;
import simu.framework.Trace;

import java.util.*;

// TODO:
// Palvelupistekohtaiset toiminnallisuudet, laskutoimitukset (+ tarvittavat muuttujat) ja raportointi koodattava
public class ServicePoint {

	protected final LinkedList<Customer> queue = new LinkedList<>(); // Tietorakennetoteutus
	protected final ContinuousGenerator generator;
	protected final EventList eventList;
	protected final EventType scheduledEventType;
	protected int rideID = 0;
	protected static int i = 0;
	protected static int j = 0;
	private HashMap<Integer, List<Double>> serviceTimes = new HashMap<>();
	private HashMap<Integer, List<Double>> queueTimes = new HashMap<>();
	private int customerCounter = 0;
	
	//JonoStartegia strategia; //optio: asiakkaiden järjestys

	protected boolean reserved = false;


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

	public int getRideID() {
		return rideID;
	}


	public void addToQueue(Customer a){   // Jonon 1. asiakas aina palvelussa
		queue.add(a);
		a.setQueueArrivalTime(Clock.getInstance().getTime());
	}


	public Customer fetchFromQueue(){  // Poistetaan palvelussa ollut
		reserved = false;
		return queue.poll();
	}

	public Customer peekNextCustomer(){
		return queue.peek();
	}


	public void beginService(){  //Aloitetaan uusi palvelu, asiakas on jonossa palvelun aikana
		queue.peek().setQueueDepartureTime(Clock.getInstance().getTime());
		addQueueTime(queue.peek());
		double serviceTime = generator.sample();
		addServiceTime(rideID, serviceTime);
		Trace.out(Trace.Level.INFO, "Aloitetaan uusi palvelu asiakkaalle " + queue.peek().getId() + " pisteessä: " + scheduledEventType + " " + rideID + " valmis: " + (Clock.getInstance().getTime()+serviceTime));
		reserved = true;
		eventList.add(new Event(scheduledEventType, Clock.getInstance().getTime()+serviceTime));
	}

	public boolean isReserved(){
		return reserved;
	}

	public boolean isInQueue(){
		return queue.size() != 0;
	}

	public Customer fetchFromCustomerList() {
		return null;
	}

	public int getCustomerListSize() {
		return 0;
	}

	public double getAverageServiceTime() {
		return (serviceTimes.get(rideID).get(0) / serviceTimes.get(rideID).get(1));
	}

	public void addServiceTime(int servicePointID, double serviceTime){
			if (!serviceTimes.containsKey(servicePointID)) {
				serviceTimes.put(servicePointID, new ArrayList<>(Arrays.asList(serviceTime, 1.0)));
			} else {
				serviceTimes.get(rideID).set(0, serviceTimes.get(rideID).get(0) + serviceTime);
				serviceTimes.get(rideID).set(1, serviceTimes.get(rideID).get(1) + 1);
			}
		}

	public double getAverageQueueTime() {
		if (queueTimes.containsKey(rideID)) {
			return queueTimes.get(rideID).get(0) / queueTimes.get(rideID).get(1);
		} else {
			return 0;
		}
	}

	public void addQueueTime(Customer customer){
		double queueTime = customer.getQueueDepartureTime() - customer.getQueueArrivalTime();
		if (!queueTimes.containsKey(rideID)) {
			queueTimes.put(rideID, new ArrayList<>(Arrays.asList(queueTime, 1.0)));
		} else {
			queueTimes.get(rideID).set(0, queueTimes.get(rideID).get(0) + queueTime);
			queueTimes.get(rideID).set(1, queueTimes.get(rideID).get(1) + 1);
		}
	}

	public void incrementCustomerCounter() {
		System.out.println("Laite id " + rideID + " asiakasmäärä: " + customerCounter);
		customerCounter++;
	}

	public int getCustomerCounter() {
		return customerCounter;
	}
}

package simu.model;

import distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;
import simu.framework.Trace;

import java.util.ArrayList;
import java.util.LinkedList;

// TODO:
// Palvelupistekohtaiset toiminnallisuudet, laskutoimitukset (+ tarvittavat muuttujat) ja raportointi koodattava
public class ServicePoint {

	protected final LinkedList<Customer> queue = new LinkedList<>(); // Tietorakennetoteutus
	protected final ContinuousGenerator generator;
	protected final EventList eventList;
	protected final EventType scheduledEventType;
	protected int rideID = 0;
	protected static int i = 0;
	private ArrayList<Double> serviceTimes = new ArrayList<>();
	
	//JonoStartegia strategia; //optio: asiakkaiden järjestys

	protected boolean reserved = false;


	public ServicePoint(ContinuousGenerator generator, EventList eventList, EventType type){
		this.eventList = eventList;
		this.generator = generator;
		this.scheduledEventType = type;
		if (this.scheduledEventType == EventType.DEP_RIDE) {
			i++;
			rideID = i;
		}
	}

	public int getRideID() {
		return rideID;
	}


	public void addToQueue(Customer a){   // Jonon 1. asiakas aina palvelussa
		queue.add(a);
		
	}


	public Customer fetchFromQueue(){  // Poistetaan palvelussa ollut
		reserved = false;
		return queue.poll();
	}

	public Customer peekNextCustomer(){
		return queue.peek();
	}


	public void beginService(){  //Aloitetaan uusi palvelu, asiakas on jonossa palvelun aikana
		double serviceTime = generator.sample();
		Trace.out(Trace.Level.INFO, "Aloitetaan uusi palvelu asiakkaalle " + queue.peek().getId() + " pisteessä: " + scheduledEventType + " " + rideID + " valmis: " + (Clock.getInstance().getTime()+serviceTime));
		serviceTimes.add(serviceTime);
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
		double sum = 0;
		for (double time : serviceTimes) {
			sum += time;
		}
		return sum / serviceTimes.size();
	}
}

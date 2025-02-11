package sim.model;

import sim.framework.*;
import java.util.LinkedList;
import eduni.distributions.ContinuousGenerator;

// TODO:
// Palvelupistekohtaiset toiminnallisuudet, laskutoimitukset (+ tarvittavat muuttujat) ja raportointi koodattava
public class ServicePoint {

	private final LinkedList<Customer> queue = new LinkedList<>(); // Tietorakennetoteutus
	private final ContinuousGenerator generator;
	private final EventList eventList;
	private final EventType scheduledEventType;
	
	//JonoStartegia strategia; //optio: asiakkaiden järjestys
	
	private boolean reserved = false;
	private boolean restaurant = false;
	private int restaurantCapacity;
	private int restaurantCustomerCounter = 0;


	public ServicePoint(ContinuousGenerator generator, EventList eventList, EventType type){
		this.eventList = eventList;
		this.generator = generator;
		this.scheduledEventType = type;
				
	}


	public void addToQueue(Customer a){   // Jonon 1. asiakas aina palvelussa
		queue.add(a);
		
	}


	public Customer fetchFromQueue(){  // Poistetaan palvelussa ollut
		reserved = false;
		return queue.poll();
	}

	public Customer fetchFromRestaurantQueue() {
		if (hasRoomInRestaurant()) {
			Customer customer = queue.poll();
			if (customer != null) {
				return customer; //Asiakas ravintolaan
			}
		} return null; //Ravintola täynnä
	}

	public void beginService(){  //Aloitetaan uusi palvelu, asiakas on jonossa palvelun aikana
		
		Trace.out(Trace.Level.INFO, "Starting new service for customer  " + queue.peek().getId());

		if (isRestaurant()){
			restaurantCustomerCounter++;
			System.out.println("Customers in restaurant: " + restaurantCustomerCounter); //Tämä tässä vaan jotta voi seurata kapasiteetin ylitystä
			if (restaurantCustomerCounter < restaurantCapacity){ //Varattu vasta kun ravintola täynnä
				reserved = false;
			} else {
				reserved = true;
			}
		} else {
			reserved = true;
		}

		double serviceTime = generator.sample();
		eventList.add(new Event(scheduledEventType,Clock.getInstance().getTime()+serviceTime));
	}


	public boolean isReserved(){
		return reserved;
	}


	public boolean isInQueue(){
		return queue.size() != 0;
	}

	public int getQueueSize() {
		return queue.size();
	}

	public void setRestaurant(boolean restaurant) {
		this.restaurant = restaurant;
	}

	public void setRestaurantCapacity(int capacity) {
		if (isRestaurant()) {
			restaurantCapacity = capacity;
		} else {
			Trace.out(Trace.Level.WAR, "ServicePoint is not a restaurant");
		}
	}

	public boolean isRestaurant() {
		return restaurant;
	}

	public void customerLeftRestaurant() {
		if (restaurantCustomerCounter > 0) {
			restaurantCustomerCounter--;
		} else {
			Trace.out(Trace.Level.WAR, "customerLeftRestaurant called when counter was already 0");
		}
	}

	public int getRestaurantCustomerCounter() {
		return restaurantCustomerCounter;
	}

	public boolean hasRoomInRestaurant() {
		if (restaurantCustomerCounter >= restaurantCapacity) {
			return false;
		}
		return true;
	}

}

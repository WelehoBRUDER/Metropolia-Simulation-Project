package simu.model;

import distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;
import simu.framework.Trace;

import java.util.ArrayList;

/**
 * RestaurantServicePoint is a class that extends {@link simu.model.ServicePoint} and represents a restaurant service point in the simulation.
 * It differs from other service points in that it has a capacity for customers and a list of customers currently being served.
 */
public class RestaurantServicePoint extends ServicePoint{
    /**
     * The ID of the restaurant service point.
     */
    protected int rideID = 100;
    /**
     * The capacity of the restaurant service point.
     */
    private int capacity;
    /**
     * The list of customers currently being served in the restaurant.
     */
    private ArrayList<Customer> CustomerList = new ArrayList<>();
    /**
     * The list of service times for customers.
     */
    private ArrayList<Double> serviceTimes = new ArrayList<>();

    /**
     * Constructor for the RestaurantServicePoint class.
     * @param generator The generator for the service point, used to generate service times.
     * @param eventList The event list for the simulation.
     * @param type The type of event.
     * @param rideCount The number of rides in the simulation.
     * @param capacity The capacity of the restaurant service point.
     */
    public RestaurantServicePoint(ContinuousGenerator generator, EventList eventList, EventType type, int rideCount, int capacity) {
        super(generator, eventList, type, rideCount);
        this.capacity = capacity;
    }

    /**
     * Fetches a customer from the customer list.
     * @return The customer fetched from the customer list.
     */
    @Override
    public Customer fetchFromCustomerList() {
        return CustomerList.remove(0);
    }

    /**
     * Checks if the restaurant service point is reserved.
     * @return True if the restaurant service point is reserved, false otherwise.
     */
    @Override
    public boolean isReserved() {
        return CustomerList.size() >= capacity;
    }

    /**
     * Gets the size of the customer list.
     * @return The size of the customer list.
     */
    @Override
    public int getCustomerListSize() {
        return CustomerList.size();
    }

    /**
     * The method to begin a new service at the restaurant service point. The method starts a new service and the customer is in the queue during the service.
     * The method also sets the customer as in the restaurant and adds the customer to the customer list.
     */
    @Override
    public void beginService(){  //Aloitetaan uusi palvelu, asiakas on jonossa palvelun aikana
        Customer c = queue.peek();

        if (!c.InRestaurant()) {
            c.setInRestaurant(true);
            CustomerList.add(queue.remove());
            c.setQueueDepartureTime(Clock.getInstance().getTime());
            addQueueTime(c);

            double serviceTime = generator.sample();
            serviceTimes.add(serviceTime);
            Trace.out(Trace.Level.INFO, "Service time: " + serviceTime + ", at service point: " + rideID + " , ready: " + (Clock.getInstance().getTime() + serviceTime));

            reserved = isReserved();
            eventList.add(new Event(scheduledEventType, Clock.getInstance().getTime() + serviceTime));
        }
    }

    /**
     * Gets the average service time for the restaurant service point.
     * @return The average service time for the restaurant service point.
     */
    public double getAverageServiceTime() {
        double sum = 0;
        for (double time : serviceTimes) {
            sum += time;
        }
        return sum / serviceTimes.size();
    }
}

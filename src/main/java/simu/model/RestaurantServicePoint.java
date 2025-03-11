package simu.model;

import distributions.ContinuousGenerator;
import simu.framework.Clock;
import simu.framework.Event;
import simu.framework.EventList;
import simu.framework.Trace;

import java.util.ArrayList;

public class RestaurantServicePoint extends ServicePoint{
    protected int rideID = 100;
    private int capacity;
    private ArrayList<Customer> CustomerList = new ArrayList<>();
    private ArrayList<Double> serviceTimes = new ArrayList<>();

    public RestaurantServicePoint(ContinuousGenerator generator, EventList eventList, EventType type, int rideCount, int capacity) {
        super(generator, eventList, type, rideCount);
        this.capacity = capacity;
    }

    @Override
    public Customer fetchFromCustomerList() {
        return CustomerList.remove(0);
    }

    @Override
    public boolean isReserved() {
        return CustomerList.size() >= capacity;
    }

    @Override
    public int getCustomerListSize() {
        return CustomerList.size();
    }

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
            Trace.out(Trace.Level.INFO, "Aloitetaan uusi palvelu asiakkaalle " + c.getId() + " pisteessä: " + scheduledEventType + " " + rideID + " valmis: " + (Clock.getInstance().getTime() + serviceTime));

            reserved = isReserved();
            eventList.add(new Event(scheduledEventType, Clock.getInstance().getTime() + serviceTime));
        }
    }

    public double getAverageServiceTime() {
        double sum = 0;
        for (double time : serviceTimes) {
            sum += time;
        }
        return sum / serviceTimes.size();
    }
}

package simu.model;

import controller.ISettingsControllerForM;
import controller.ResultsController;
import distributions.Bernoulli;
import distributions.Negexp;
import distributions.Normal;
import simu.framework.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import database.Dao;

/**
 * OwnEngine extends {@link simu.framework.Engine} and is the class for the simulation engine in the simulation.
 */
public class OwnEngine extends Engine {

    /**
     * ArrivalProgress: Arrival of customers
     */
    private ArrivalProgress arrivalProgress;
    /**
     * ServicePoints: Service points in the simulation
     */
    private ServicePoint[] servicePoints;
    /**
     * RideOrder: Order of rides
     */
    private ArrayList<Integer> rideOrder = new ArrayList<>();
    /**
     * TicketOrder: Order of tickets
     */
    private ArrayList<Integer> ticketOrder = new ArrayList<>();
    /**
     * WristbandChance: Chance of customer being a wristband customer
     */
    private double wristbandChance = 0.1;
    /**
     * RestaurantCapacity: Capacity of the restaurant
     */
    private int RESTAURANT_CAPASITY = 20;
    /**
     * Bernoulli: Bernoulli distribution for wristband chance
     */
    private Bernoulli bernoulli;
    /**
     * RideCount: Number of rides in the simulation
     */
    private int rideCount;
    /**
     * TicketBoothCount: Number of ticket booths in the simulation
     */
    private int ticketBoothCount = 1;
    /**
     * MinTicketPurchase: Minimum amount of tickets that can be purchased
     */
    private int minTicketPurchase = 1;
    /**
     * MaxTicketPurchase: Maximum amount of tickets that can be purchased
     */
    private int maxTicketPurchase = 1;
    /**
     * RideParameters: Parameters for the rides
     */
    private ArrayList<int[]> rideParameters;
    /**
     * WristbandTimes: List of times that wristband customers spend in the simulation
     */
    private ArrayList<Double> wristbandTimes = new ArrayList<>();
    /**
     * TicketTimes: List of times that ticket customers spend in the simulation
     */
    private ArrayList<Double> ticketTimes = new ArrayList<>();
    /**
     * StaticResults: Static results of the simulation, the results that are same for all simulations
     */
    private HashMap<String, Double> staticResults = new HashMap<>();
    /**
     * DynamicResults: Dynamic results of the simulation, the results for all the service points in the simulation
     */
    private TreeMap<String, Double> dynamicResults = new TreeMap<>();
    /**
     * ReadyCustomers: Amount of customers that have reached the end of the simulation
     */
    private int readyCustomers = 0;
    /**
     * TicketBoothCounter: Counter for the ticket booths
     */
    private int ticketBoothCounter = -1;

    private Dao dao = new Dao();

    /**
     * Constructor for the OwnEngine class. Initializes the simulation engine with the given parameters. Sets the time of the simulation to 0.
     * Creates the service points for the simulation and assigns them IDs. Initializes the arrival progress for the simulation and generates the first arrival in the simulation.
     *
     * @param controller       The controller for the simulation
     * @param arrivalInterval  The interval between customer arrivals
     * @param rideCount        The number of rides in the simulation
     * @param ticketBoothCount The number of ticket booths in the simulation
     * @param rideProperties   The properties for the rides, such as mean and variance
     * @param restaurantCap    The capacity of the restaurant
     * @param wristbandChance  The chance of the customer being a wristband customer
     */
    public OwnEngine(ISettingsControllerForM controller, double arrivalInterval, int rideCount, int ticketBoothCount, ArrayList<int[]> rideProperties, int restaurantCap, double wristbandChance) {

        super(controller);

        Clock.getInstance().setTime(0);
        Customer.setI(1);

        this.rideCount = rideCount;
        this.ticketBoothCount = ticketBoothCount;
        this.RESTAURANT_CAPASITY = restaurantCap;
        this.wristbandChance = wristbandChance / 100;
        this.bernoulli = new Bernoulli(this.wristbandChance);
        this.rideParameters = rideProperties;

        ServicePoint.i = 0;
        ServicePoint.j = 0;

        servicePoints = new ServicePoint[rideCount + ticketBoothCount + 1];
        System.out.println("Service points: " + servicePoints.length);

        for (int i = 0; i < ticketBoothCount; i++) {

            servicePoints[i] = new ServicePoint(new Normal(5, 2), eventList, EventType.DEP_TICKET_BOOTH, rideCount);
        }

        for (int i = ticketBoothCount; i < ticketBoothCount + rideCount; i++) {
            int mean = rideProperties.get(i - ticketBoothCount)[1];
            int variance = rideProperties.get(i - ticketBoothCount)[0];
            servicePoints[i] = new ServicePoint(new Normal(mean, variance), eventList, EventType.DEP_RIDE, rideCount);
        }

        servicePoints[rideCount + ticketBoothCount] = new RestaurantServicePoint(new Normal(80, 3), eventList, EventType.DEP_RESTAURANT, rideCount, RESTAURANT_CAPASITY);
        arrivalProgress = new ArrivalProgress(new Negexp(arrivalInterval), eventList, EventType.ARRIVAL);

    }

    /**
     * FindRideByID: Finds a ride by its ID
     *
     * @param id ID of the ride
     * @return The ride with the given ID
     */
    private ServicePoint findRideByID(int id) {
        for (ServicePoint p : servicePoints) {
            if (p.getRideID() == id) {
                return p;
            }
        }
        return null;
    }

    /**
     * Init: Initializes the simulation engine and generates the first arrival to the system
     */
    @Override
    protected void init() {
        arrivalProgress.generateNext(); // Ensimmäinen saapuminen järjestelmään
    }

    /**
     * RunEvent: Runs the B events in the simulation. Checks the type of the event and runs the corresponding event.
     *
     * @param t Event to be run
     */
    @Override
    protected void runEvent(Event t) {

        Trace.out(Trace.Level.INFO, "Restaurant has " + servicePoints[rideCount + ticketBoothCount].getCustomerListSize() + " customers.");

        Customer c;
        ServicePoint p;
        int from = -100;
        int to = -101;
        switch ((EventType) t.getType()) {
            case ARRIVAL:
                double sample = bernoulli.sample();
                c = new Customer(rideCount, sample, minTicketPurchase, maxTicketPurchase);

                if (c.hasWristband()) {
                    p = findRideByID(c.getNextRideID());
                    controller.updateConsole("Customer " + c.getId() + " (wristband) arrived and goes to ride " + p.getRideID());

                    p.addToQueue(c);
                    c.removeNextRide();
                    rideOrder.add(p.getRideID());
                    from = -100;
                    to = p.getRideID();

                } else {
                    c.addTickets();
                    servicePoints[nextTicketBooth()].addToQueue(c);
                    ticketOrder.add(ticketBoothCounter);
                    from = -100;
                    to = (ticketBoothCounter + 1) / -1;

                    controller.updateConsole("Customer " + c.getId() + " (no wristband) arrived and goes to ticket booth " + (ticketBoothCounter + 1));
                    Trace.out(Trace.Level.INFO, "Customer " + c.getId() + " goes to ticket booth");
                }

                arrivalProgress.generateNext();
                break;

            case DEP_TICKET_BOOTH:
                c = servicePoints[ticketOrder.get(0)].fetchFromQueue();
                servicePoints[ticketOrder.get(0)].incrementCustomerCounter();
                c.incrementTicketboothCounter();

                p = findRideByID(c.getNextRideID());
                p.addToQueue(c);
                c.removeNextRide();
                rideOrder.add(p.getRideID());
                c.removeTicket();

                from = servicePoints[ticketOrder.remove(0)].getRideID();
                to = p.getRideID();

                controller.updateConsole("Customer " + c.getId() + " bought tickets from ticket booth " + (from / -1) + " and goes to ride " + to);
                Trace.out(Trace.Level.INFO, "Customer: " + c.getId() + " goes to ride " + p.getRideID() + " queue");
                break;

            case DEP_RIDE:
                servicePoints[rideOrder.get(0) + ticketBoothCount - 1].incrementCustomerCounter();
                c = servicePoints[rideOrder.get(0) + ticketBoothCount - 1].fetchFromQueue();

                if (c.ridesLeft()) {
                    if (c.hasWristband() || c.getTickets() > 0) {
                        p = findRideByID(c.getNextRideID());

                        p.addToQueue(c);
                        c.removeNextRide();
                        rideOrder.add(p.getRideID());
                        from = servicePoints[rideOrder.remove(0) + ticketBoothCount - 1].getRideID();
                        to = p.getRideID();
                        controller.updateConsole("Customer " + c.getId() + " rode ride " + from + " and goes to ride " + to);
                        Trace.out(Trace.Level.INFO, "Customer " + c.getId() + " goes to ride: " + p.getRideID());
                    } else {
                        servicePoints[nextTicketBooth()].addToQueue(c);
                        ticketOrder.add(ticketBoothCounter);
                        from = servicePoints[rideOrder.remove(0) + ticketBoothCount - 1].getRideID();
                        to = (ticketBoothCounter + 1) / -1;
                        controller.updateConsole("Customer " + c.getId() + " rode ride " + from + " and goes to ticket booth " + (to / -1));
                        Trace.out(Trace.Level.INFO, "Customer " + c.getId() + " goes to ticket booth");
                    }
                } else {
                    servicePoints[rideCount + ticketBoothCount].addToQueue(c);
                    from = servicePoints[rideOrder.remove(0) + ticketBoothCount - 1].getRideID();
                    to = 100;
                    controller.updateConsole("Customer " + c.getId() + " rode ride " + from + " and goes to the restaurant");
                    Trace.out(Trace.Level.INFO, "Customer " + c.getId() + " goes to restaurant");
                }
                break;

            case DEP_RESTAURANT:
                c = servicePoints[rideCount + ticketBoothCount].fetchFromCustomerList();
                servicePoints[rideCount + ticketBoothCount].incrementCustomerCounter();
                c.setDepartureTime(Clock.getInstance().getTime());
                readyCustomers++;
                from = 100;
                to = -101;
                controller.updateConsole("Customer " + c.getId() + " left the restaurant and is ready to leave the park");

                double average = c.report();
                if (c.hasWristband()) {
                    wristbandTimes.add(average);
                } else {
                    ticketTimes.add(average);
                }
                break;
        }
        controller.updateEventTime(Clock.getInstance().getTime());
        controller.addCustomerToAnimation(from, to);
    }

    /**
     * AttemptCEvents: Attempts to run the C events in the simulation. Begins service if the service point is not reserved and there are customers in the queue.
     */
    @Override
    protected void attemptCEvents() {
        for (ServicePoint p : servicePoints) {
            if (!p.isReserved() && p.isInQueue()) {
                p.beginService();
            }
        }
    }

    /**
     * NextTicketBooth: Returns the next ticket booth to be used
     *
     * @return The next ticket booth to be used
     */
    public int nextTicketBooth() {
        ticketBoothCounter++;
        if (ticketBoothCounter >= ticketBoothCount) {
            ticketBoothCounter = 0;
        }
        return ticketBoothCounter;
    }

    /**
     * SetWristbandChance: Sets the chance of getting a wristband
     *
     * @param amount Amount to be added to the wristband chance
     */
    public void setWristbandChance(double amount) {
        wristbandChance = amount / 100;
        if (wristbandChance > 1) {
            wristbandChance = 1;
        } else if (wristbandChance < 0) {
            wristbandChance = 0;
        }
        System.out.println("Wristband chance: " + wristbandChance);
        this.bernoulli = new Bernoulli(this.wristbandChance);
        bernoulli = new Bernoulli(wristbandChance);
    }

    public void addResults(Map results, String key, double value) {
        if (Double.isNaN(value)) {
            results.put(key, 0.0);
        } else {
            results.put(key, value);
        }
    }

    /**
     * Results: Generates and summarizes the results of the simulation. Results are related to customer counts, ticket booths, customer times and service point specific results.
     * Closes the simulation and visualizes the results in the results view.
     */
    @Override
    protected void results() {
        dao.incrementSimId();
        double endTime = Clock.getInstance().getTime();
        Trace.out(Trace.Level.INFO, "Simulation ends at time: " + endTime + ", generating results.");
        staticResults.put("End time", endTime);

        //Customer count related results:
        int unreadyCustomers = Customer.getI() - readyCustomers;
        addResults(staticResults, "Ready customers", readyCustomers);
        addResults(staticResults, "Ticket customers", ticketTimes.size());
        addResults(staticResults, "Wristband customers", wristbandTimes.size());
        addResults(staticResults, "Unready customers", unreadyCustomers);

        //Ticket booth related results:
        addResults(staticResults, "Ticket booth average", Customer.getTicketboothCounterAverage());
        addResults(staticResults, "Total ticket count", Customer.getTotalTicketCount());

        //Customer time related results:
        addResults(staticResults, "Wristband average time", getAverageWristbandTime());
        addResults(staticResults, "Ticket average time", getAverageTicketTime());
        addResults(staticResults, "Whole average time", getWholeAverage());
        addResults(staticResults, "Wristband ticket ratio", getWristbandTicketAverageRatio());

        //dao
        dao.addServicePoint(endTime, readyCustomers, ticketTimes.size(), wristbandTimes.size(), unreadyCustomers, Customer.getTicketboothCounterAverage()/*0*/, Customer.getTotalTicketCount(), getAverageWristbandTime(), getAverageTicketTime(), getWholeAverage(), getWristbandTicketAverageRatio());

        int i = ticketBoothCount;
        //Results for each service point in the simulation:
        for (ServicePoint point : servicePoints) {
            int customerCount = point.getCustomerCounter();
            double averageServiceTime = point.getAverageServiceTime();
            double averageQueueTime = point.getAverageQueueTime();

            if (!(point instanceof RestaurantServicePoint)) {
                if (point.getRideID() < 0) {
                    int ticketBoothNumber = point.getRideID() * -1;
                    dynamicResults.put("Ticket booth " + ticketBoothNumber + " count", (double) customerCount);
                    dynamicResults.put("Ticket booth " + ticketBoothNumber + " average service time", averageServiceTime);
                    dynamicResults.put("Ticket booth " + ticketBoothNumber + " average queue time", averageQueueTime);
                    dao.addTicketBooth(ticketBoothNumber, customerCount, averageServiceTime, averageQueueTime);
                } else {
                    dynamicResults.put("Ride " + point.getRideID() + " count", (double) customerCount);
                    dynamicResults.put("Ride " + point.getRideID() + " average service time", averageServiceTime);
                    dynamicResults.put("Ride " + point.getRideID() + " average queue time", averageQueueTime);
                    dao.addRide(point.getRideID(), customerCount, averageServiceTime, averageQueueTime, rideParameters.get(i - ticketBoothCount)[0], rideParameters.get(i - ticketBoothCount)[1]);
                    i++;
                }
            } else {
                dynamicResults.put("Restaurant count", (double) customerCount);
                dynamicResults.put("Restaurant average service time", averageServiceTime);
                dynamicResults.put("Restaurant average queue time", averageQueueTime);
                dao.addRestaurant(customerCount, averageServiceTime, averageQueueTime, RESTAURANT_CAPASITY);
            }
        }
        ResultsController resultsController = new ResultsController();
        resultsController.visualizeResults(staticResults, dynamicResults);
        controller.showEndTime(Clock.getInstance().getTime());
        controller.closeSimulation();
    }

    /**
     * GetWholeAverage: Returns the average time that customers spend in the simulation regardless of the type of the customer
     *
     * @return The average time that customers spend in the simulation
     */
    protected double getWholeAverage() {
        if (wristbandTimes.isEmpty() && ticketTimes.isEmpty()) {
            return 0.00;
        } else if (wristbandTimes.isEmpty()) {
            return getAverageTicketTime();
        } else if (ticketTimes.isEmpty()) {
            return getAverageWristbandTime();
        }
        return (getAverageTicketTime() + getAverageWristbandTime()) / 2;
    }

    /**
     * GetAverageWristbandTime: Returns the average time that wristband customers spend in the simulation
     *
     * @return The average time that wristband customers spend in the simulation, 0 if no wristband customers
     */
    public double getAverageWristbandTime() {
        if (wristbandTimes.isEmpty()) {
            return 0.00;
        } else {
            double sum = 0;
            for (double time : wristbandTimes) {
                sum += time;
            }
            return sum / wristbandTimes.size();
        }
    }

    /**
     * GetAverageTicketTime: Returns the average time that ticket customers spend in the simulation
     *
     * @return The average time that ticket customers spend in the simulation, 0 if no ticket customers
     */
    public double getAverageTicketTime() {
        if (ticketTimes.isEmpty()) {
            return 0.00;
        } else {
            double sum = 0;
            for (double time : ticketTimes) {
                sum += time;
            }
            return sum / ticketTimes.size();
        }
    }

    /**
     * GetWristbandTicketAverageRatio: Returns the ratio of the average time that ticket customers spend in the simulation to the average time that wristband customers spend in the simulation
     *
     * @return The ratio of the average time that ticket customers spend in the simulation to the average time that wristband customers spend in the simulation, 0.00 if no wristband or ticket customers
     */
    public double getWristbandTicketAverageRatio() {
        if (wristbandTimes.isEmpty() || ticketTimes.isEmpty()) {
            return 0.00;
        } else {
            return getAverageTicketTime() / getAverageWristbandTime();
        }
    }
}

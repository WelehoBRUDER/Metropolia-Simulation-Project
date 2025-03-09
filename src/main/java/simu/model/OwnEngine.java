
package simu.model;

import controller.ISettingsControllerForM;
import controller.ResultsController;
import distributions.Bernoulli;
import distributions.Negexp;
import distributions.Normal;
import simu.framework.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
/**
 * OwnEngine.java class is the main class for the simulation engine.
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
     * WristbandChance: Chance of getting a wristband
     */
    private double wristbandChance = 0.1;
    /**
     * RestaurantCapacity: Capacity of the restaurant
     */
    private int RESTAURANT_CAPASITY = 20;
    /**
     * Bernoulli: Bernoulli distribution used for wristband chance
     */
    private Bernoulli bernoulli = new Bernoulli(wristbandChance);
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
     * TicketAverages: List of times that ticket customers spend in the simulation
     */
    private ArrayList<Double> ticketTimes = new ArrayList<>();
    /**
     * Results: The static part of the results of the simulation
     */
    private HashMap<String, Double> staticResults = new HashMap<>();
    /**
     * DynamicResults: The dynamic part of the results of the simulation
     */
    private TreeMap<String, Double> dynamicResults = new TreeMap<>();
    /**
     * ReadyCustomers: Number of customers that have finished the simulation
     */
    private int readyCustomers = 0;
    /**
     * TicketBoothCounter: Counter for the ticket booths
     */
    private int ticketBoothCounter = -1;

    /**
     * Constructor for the OwnEngine class. Initializes the simulation engine with the given parameters and creates arrival process and service points.
     * @param controller Controller for the simulation
     * @param rideCount Number of rides in the simulation
     * @param ticketBoothCount Number of ticket booths in the simulation
     * @param rideProperties Properties for the rides
     * @param restaurantCap Capacity of the restaurant
     * @param wristbandChance Chance of getting a wristband
     */
    public OwnEngine(ISettingsControllerForM controller, int rideCount, int ticketBoothCount, ArrayList<int[]> rideProperties, int restaurantCap, double wristbandChance) {

        super(controller);
        this.rideCount = rideCount;
        this.ticketBoothCount = ticketBoothCount;
        this.RESTAURANT_CAPASITY = restaurantCap;
        this.wristbandChance = wristbandChance;

        ServicePoint.i = 0;
        ServicePoint.j = 0;

        //TESTI PARAMETREJÄ!!!
        rideParameters = new ArrayList<>();
        rideParameters.add(new int[]{5, 2});
        rideParameters.add(new int[]{10, 10});
        //TESTI PARAMETREJÄ!!!

        servicePoints = new ServicePoint[rideCount + ticketBoothCount + 1];
        System.out.println("Palvelupisteitä: " + servicePoints.length);

        for (int i = 0; i < ticketBoothCount; i++) {

            servicePoints[i] = new ServicePoint(new Normal(5, 2), eventList, EventType.DEP_TICKET_BOOTH, rideCount);//lipunmyynti
        }

        for (int i = ticketBoothCount; i < ticketBoothCount + rideCount; i++) {
            int mean = rideProperties.get(i - ticketBoothCount)[0];
            int variance = rideProperties.get(i - ticketBoothCount)[1];
            servicePoints[i] = new ServicePoint(new Normal(mean, variance), eventList, EventType.DEP_RIDE, rideCount); //Laitteet
        }

        servicePoints[rideCount + ticketBoothCount] = new RestaurantServicePoint(new Normal(80, 3), eventList, EventType.DEP_RESTAURANT, rideCount, RESTAURANT_CAPASITY); //Ravintola

        //arrivalProgress = new ArrivalProgress(new Negexp(15, 5), eventList, EventType.ARRIVAL); //Saapuminen, Tällä asiakkaat saapuvat n. 15 aikayksikön välein eli aika harvoin
        arrivalProgress = new ArrivalProgress(new Negexp(5, 5), eventList, EventType.ARRIVAL); //Saapuminen
    }

    /**
     * FindRideByID: Finds a ride by its ID
     * @param id ID of the ride
     * @return The ride with the given ID
     */
    private ServicePoint findRideByID(int id) {
        for (ServicePoint p : servicePoints) {
            System.out.println("Palvelupisteen id on " + p.getRideID());
            if (p.getRideID() == id) {
                return p;
            }
        }
        System.out.println("Haettu id on " + id);
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
     * @param t Event to be run
     */
    @Override
    protected void runEvent(Event t) {  // B-vaiheen tapahtumat

        Trace.out(Trace.Level.INFO, "Ravintolassa on " + servicePoints[rideCount + ticketBoothCount].getCustomerListSize() + " asiakasta.");

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
                    controller.visualizeCustomer(c.getId(), p.getRideID(), true);

                    p.addToQueue(c);
                    c.removeNextRide();
                    rideOrder.add(p.getRideID());
                    from = -100;
                    to = p.getRideID();
                    //System.out.println("from " + -100 + " to " + p.getRideID()); //FROMTO

                    Trace.out(Trace.Level.INFO, "Asiakas " + c.getId() + " menee laitteen " + p.getRideID() + " jonoon");

                } else {
                    c.addTickets();
                    servicePoints[nextTicketBooth()].addToQueue(c);
                    ticketOrder.add(ticketBoothCounter);
                    from = -100;
                    to = (ticketBoothCounter + 1) / -1;
                    //System.out.println("from " + -100 + " to " + (ticketBoothCounter+1)/-1); //FROMTO

                    controller.visualizeCustomer(c.getId(), 0, false);
                    Trace.out(Trace.Level.INFO, "Asiakas " + c.getId() + " menee lippujonoon");
                }

                arrivalProgress.generateNext();
                break;

            case DEP_TICKET_BOOTH:
                c = servicePoints[ticketOrder.get(0)].fetchFromQueue();
                servicePoints[ticketOrder.get(0)].incrementCustomerCounter();
                c.incrementTicketboothCounter();

                p = findRideByID(c.getNextRideID());
                controller.visualizeCustomer(c.getId(), p.getRideID(), c.hasWristband());

                p.addToQueue(c);
                c.removeNextRide();
                rideOrder.add(p.getRideID());
                c.removeTicket();
                from = servicePoints[ticketOrder.remove(0)].getRideID();
                to = p.getRideID();
                //System.out.println("from " + servicePoints[ticketOrder.remove(0)].getRideID() + " to " + p.getRideID()); //FROMTO

                Trace.out(Trace.Level.INFO, "Asiakas: " + c.getId() + " menee laitteen " + p.getRideID() + " jonoon");

                break;

            case DEP_RIDE:
                servicePoints[rideOrder.get(0) + ticketBoothCount - 1].incrementCustomerCounter();
                c = servicePoints[rideOrder.get(0) + ticketBoothCount - 1].fetchFromQueue();

                if (c.ridesLeft()) {
                    if (c.hasWristband() || c.getTickets() > 0) {
                        p = findRideByID(c.getNextRideID());
                        controller.visualizeCustomer(c.getId(), p.getRideID(), c.hasWristband());

                        p.addToQueue(c);
                        c.removeNextRide();
                        rideOrder.add(p.getRideID());
                        from = servicePoints[rideOrder.remove(0) + ticketBoothCount - 1].getRideID();
                        to = p.getRideID();
                        //System.out.println("from " + servicePoints[rideOrder.remove(0)+ticketBoothCount-1].getRideID() + " to " + p.getRideID()); //FROMTO

                        Trace.out(Trace.Level.INFO, "Asiakas " + c.getId() + " menee laitteeseen: " + p.getRideID());
                    } else {
                        servicePoints[nextTicketBooth()].addToQueue(c);
                        ticketOrder.add(ticketBoothCounter);
                        controller.visualizeCustomer(c.getId(), 0, c.hasWristband());
                        from = servicePoints[rideOrder.remove(0) + ticketBoothCount - 1].getRideID();
                        to = (ticketBoothCounter + 1) / -1;
                        //System.out.println("from " + servicePoints[rideOrder.remove(0)+ticketBoothCount-1].getRideID() + " to " + (ticketBoothCounter+1)/-1); //FROMTO

                        Trace.out(Trace.Level.INFO, "Asiakas " + c.getId() + " menee lippujonoon");
                    }
                } else {
                    servicePoints[rideCount + ticketBoothCount].addToQueue(c);
                    controller.visualizeCustomer(c.getId(), rideCount + 1, c.hasWristband());
                    from = servicePoints[rideOrder.remove(0) + ticketBoothCount - 1].getRideID();
                    to = 100;
                    //System.out.println("from " + servicePoints[rideOrder.remove(0)+ticketBoothCount-1].getRideID() + " to " + 100); //FROMTO

                    Trace.out(Trace.Level.INFO, "Asiakas " + c.getId() + " menee ravintolajonoon");
                }

                break;

            case DEP_RESTAURANT:
                c = servicePoints[rideCount + ticketBoothCount].fetchFromCustomerList();
                servicePoints[rideCount + ticketBoothCount].incrementCustomerCounter();
                controller.visualizeCustomer(c.getId(), rideCount + 2, c.hasWristband());
                c.setDepartureTime(Clock.getInstance().getTime());
                readyCustomers++;
                from = 100;
                to = -101;
                //System.out.println("from " + 100 + " to " + -101); //FROMTO

                double average = c.report();
                double ticketWristRatio = getWristbandTicketAverageRatio();
                if (!Double.isNaN(ticketWristRatio)) {
                    System.out.printf("Lippuja ostaneiden viipymä suhteessa rannekkeellisten viipymään: %.2f\n", ticketWristRatio);
                } else {
                    System.out.println("Lippu-asiakkaiden ja rannekkeellisten viipymäaikojen suhde ei voida vielä laskea.");
                }
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
     * @param amount Amount to be added to the wristband chance
     */
    public void setWristbandChance(double amount) {
        wristbandChance += amount;
        if (wristbandChance > 1) {
            wristbandChance = 1;
        } else if (wristbandChance < 0) {
            wristbandChance = 0;
        }
        System.out.println("Wristband chance: " + wristbandChance);
        bernoulli = new Bernoulli(wristbandChance);
    }

    public double getWristbandChance() {
        return wristbandChance;
    }

    /**
     * Results: Prints the results of the simulation and visualizes them.
     */
    @Override
    protected void results() {
        double endTime = Clock.getInstance().getTime();
        System.out.println("Simulointi päättyi kello " + endTime);
        staticResults.put("End time", endTime);

        //Asiakkaat:
        int unreadyCustomers = Customer.getI() - readyCustomers;
        System.out.println("Valmiita asiakkaita: " + readyCustomers + ", joista rannekeasiakkaita oli " + wristbandTimes.size() + " ja lippuasiakkaita " + ticketTimes.size());
        System.out.println("Kesken jääneiden asiakkaiden määrä: " + unreadyCustomers);
        staticResults.put("Ready customers", (double) readyCustomers);
        staticResults.put("Ticket customers", (double) ticketTimes.size());
        staticResults.put("Wristband customers", (double) wristbandTimes.size());
        staticResults.put("Unready customers", (double) unreadyCustomers);

        //Lippuluukku (ticketboothCounterSumiin lisätään vasta kun asiakas valmis, joten lasketaan valmiilla asiakkailla:
        System.out.printf("Lippuasiakas kävi keskimäärin %.2f kertaa lippuluukulla.\n", Customer.getTicketboothCounterAverage());
        System.out.printf(("Simulaation aikana ostetut liput: %d \n"), Customer.getTotalTicketCount());
        staticResults.put("Ticket booth average", Customer.getTicketboothCounterAverage());
        staticResults.put("Total ticket count", (double)Customer.getTotalTicketCount());

        //Viipymät:
        System.out.printf("Rannekkeellisten keskimääräinen viipymäaika: %.2f\n", getAverageWristbandTime());
        System.out.printf("Lippujen ostajien keskimääräinen viipymäaika: %.2f\n", getAverageTicketTime());
        System.out.printf("Asiakkaiden keskimääräinen viipymäaika: %.2f\n", getWholeAverage());
        System.out.printf("Lippuja ostaneiden viipymä suhteessa rannekkeellisten viipymään: %.2f\n", getWristbandTicketAverageRatio());
        staticResults.put("Wristband average time", getAverageWristbandTime());
        staticResults.put("Ticket average time", getAverageTicketTime());
        staticResults.put("Whole average time", getWholeAverage());
        staticResults.put("Wristband ticket ratio", getWristbandTicketAverageRatio());

        //Palvelupisteiden tulokset:
        for (ServicePoint point : servicePoints) {
            int customerCount = point.getCustomerCounter();
            double averageServiceTime = point.getAverageServiceTime();
            double averageQueueTime = point.getAverageQueueTime();
            int ticketBoothCustomerCount = 0;

            if (!(point instanceof RestaurantServicePoint)) {
                if (point.getRideID() < 0) {
                    int ticketBoothNumber = point.getRideID() * -1;
                    ticketBoothCustomerCount += customerCount;
                    System.out.println("Lippupisteessä " + ticketBoothNumber + " käytiin " + customerCount + " kertaa.");
                    System.out.println("Lippupisteessä " + ticketBoothNumber + " keskimääräinen palveluaika: " + averageServiceTime);
                    System.out.println("Lippupisteessä " + ticketBoothNumber + " keskimääräinen jonotusaika: " + averageQueueTime);
                    dynamicResults.put("Ticket booth " + ticketBoothNumber + " count", (double) customerCount);
                    dynamicResults.put("Ticket booth " + ticketBoothNumber + " average service time", averageServiceTime);
                    dynamicResults.put("Ticket booth " + ticketBoothNumber + " average queue time", averageQueueTime);
                } else {
                    System.out.println("Laitteessa " + point.getRideID() + " palveltiin " + customerCount + " asiakasta.");
                    System.out.println("Laitteessa " + point.getRideID() + " keskimääräinen palveluaika: " + averageServiceTime);
                    System.out.println("Laitteessa " + point.getRideID() + " keskimääräinen jonotusaika: " + averageQueueTime);
                    dynamicResults.put("Ride " + point.getRideID() + " count", (double) customerCount);
                    dynamicResults.put("Ride " + point.getRideID() + " average service time", averageServiceTime);
                    dynamicResults.put("Ride " + point.getRideID() + " average queue time", averageQueueTime);
                }
            } else {
                System.out.println("Ravintolassa palveltiin " + customerCount + " asiakasta.");
                System.out.println("Ravintolassa keskimääräinen palveluaika: " + averageServiceTime);
                System.out.println("Ravintolassa keskimääräinen jonotusaika: " + averageQueueTime);
                dynamicResults.put("Restaurant count", (double) customerCount);
                dynamicResults.put("Restaurant average service time", averageServiceTime);
                dynamicResults.put("Restaurant average queue time", averageQueueTime);
            }
        }

        //Tulokset hashmapissa:
        System.out.println("Results hashmap: " + staticResults);
        System.out.println("DynamicResults hashmap: " + dynamicResults);

        // UUTTA graafista
        ResultsController resultsController = new ResultsController();
        resultsController.visualizeResults(staticResults, dynamicResults);
        controller.showEndTime(Clock.getInstance().getTime());
    }

    /**
     * GetWholeAverage: Returns the average time that customers spend in the simulation regardless of the type of the customer
     * @return The average time that customers spend in the simulation
     */
    protected double getWholeAverage() {
        return (getAverageTicketTime() + getAverageWristbandTime()) / 2;
    }

    /**
     * GetAverageWristbandTime: Returns the average time that wristband customers spend in the simulation
     * @return The average time that wristband customers spend in the simulation
     */
    public double getAverageWristbandTime() {
        double sum = 0;
        for (double time : wristbandTimes) {
            sum += time;
        }
        return sum / wristbandTimes.size();
    }

    /**
     * GetAverageTicketTime: Returns the average time that ticket customers spend in the simulation
     * @return The average time that ticket customers spend in the simulation
     */
    public double getAverageTicketTime() {
        double sum = 0;
        for (double time : ticketTimes) {
            sum += time;
        }
        return sum / ticketTimes.size();
    }

    /**
     * GetWristbandTicketAverageRatio: Returns the ratio of the average time that ticket customers spend in the simulation to the average time that wristband customers spend in the simulation
     * @return The ratio of the average time that ticket customers spend in the simulation to the average time that wristband customers spend in the simulation
     */
    public double getWristbandTicketAverageRatio() {
        return getAverageTicketTime() / getAverageWristbandTime();
    }

    /**
     * GetStaticResults: Returns the static results of the simulation
     * @return The static results of the simulation
     */
    public HashMap<String, Double> getStaticResults() {
        return staticResults;
    }

    /**
     * GetDynamicResults: Returns the dynamic results of the simulation
     * @return The dynamic results of the simulation
     */
    public TreeMap<String, Double> getDynamicResults() {
        return dynamicResults;
    }
}

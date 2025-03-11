package simu.model;

import controller.ISettingsControllerForM;
import controller.ResultsController;
import distributions.Bernoulli;
import distributions.Negexp;
import distributions.Normal;
import simu.framework.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import database.Dao;

public class OwnEngine extends Engine {

    Dao dao = new Dao();


    private ArrivalProgress arrivalProgress;
    private ServicePoint[] servicePoints;
    private ArrayList<Integer> rideOrder = new ArrayList<>();
    private ArrayList<Integer> ticketOrder = new ArrayList<>();

    private double wristbandChance = 0.1;
    private int RESTAURANT_CAPASITY = 20;
    private Bernoulli bernoulli;
    private int rideCount;
    private int ticketBoothCount = 1;
    private int minTicketPurchase = 1;
    private int maxTicketPurchase = 1;
    private ArrayList<int[]> rideParameters;

    private ArrayList<Double> wristbandAverages = new ArrayList<>();
    private ArrayList<Double> ticketAverages = new ArrayList<>();
    private HashMap<String, Double> results = new HashMap<>();
    private TreeMap<String, Double> dynamicResults = new TreeMap<>();
    private int readyCustomers = 0;

    private int ticketBoothCounter = -1;


    public OwnEngine(ISettingsControllerForM controller, int rideCount, int ticketBoothCount, ArrayList<int[]> rideProperties, int restaurantCap, double wristbandChance) {

        super(controller);
        this.rideCount = rideCount;
        this.ticketBoothCount = ticketBoothCount;
        this.RESTAURANT_CAPASITY = restaurantCap;
        this.wristbandChance = wristbandChance/100;
        this.bernoulli = new Bernoulli(this.wristbandChance);

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
            int mean = rideProperties.get(i - ticketBoothCount)[1];
            int variance = rideProperties.get(i - ticketBoothCount)[0];
            servicePoints[i] = new ServicePoint(new Normal(mean, variance), eventList, EventType.DEP_RIDE, rideCount); //Laitteet
        }

        servicePoints[rideCount + ticketBoothCount] = new RestaurantServicePoint(new Normal(80, 3), eventList, EventType.DEP_RESTAURANT, rideCount, RESTAURANT_CAPASITY); //Ravintola

        //arrivalProgress = new ArrivalProgress(new Negexp(15, 5), eventList, EventType.ARRIVAL); //Saapuminen, Tällä asiakkaat saapuvat n. 15 aikayksikön välein eli aika harvoin
        arrivalProgress = new ArrivalProgress(new Negexp(5, 5), eventList, EventType.ARRIVAL); //Saapuminen
    }

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

    @Override
    protected void init() {
        arrivalProgress.generateNext(); // Ensimmäinen saapuminen järjestelmään
    }

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
                    controller.updateConsole("Customer " + c.getId() + " (wristband) arrived and goes to ride " + p.getRideID());

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
                    controller.updateConsole("Customer " + c.getId() + " (no wristband) arrived and goes to ticket booth " + (ticketBoothCounter+1));
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
                controller.updateConsole("Customer " + c.getId() + " bought tickets from ticket booth " + (from/-1) + " and goes to ride " + to);
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
                        controller.updateConsole("Customer " + c.getId() + " rode ride " + from + " and goes to ride " + to);
                        //System.out.println("from " + servicePoints[rideOrder.remove(0)+ticketBoothCount-1].getRideID() + " to " + p.getRideID()); //FROMTO

                        Trace.out(Trace.Level.INFO, "Asiakas " + c.getId() + " menee laitteeseen: " + p.getRideID());
                    } else {
                        servicePoints[nextTicketBooth()].addToQueue(c);
                        ticketOrder.add(ticketBoothCounter);
                        controller.visualizeCustomer(c.getId(), 0, c.hasWristband());
                        from = servicePoints[rideOrder.remove(0) + ticketBoothCount - 1].getRideID();
                        to = (ticketBoothCounter + 1) / -1;
                        controller.updateConsole("Customer " + c.getId() + " rode ride " + from + " and goes to ticket booth " + (to/-1));
                        //System.out.println("from " + servicePoints[rideOrder.remove(0)+ticketBoothCount-1].getRideID() + " to " + (ticketBoothCounter+1)/-1); //FROMTO

                        Trace.out(Trace.Level.INFO, "Asiakas " + c.getId() + " menee lippujonoon");
                    }
                } else {
                    servicePoints[rideCount + ticketBoothCount].addToQueue(c);
                    controller.visualizeCustomer(c.getId(), rideCount + 1, c.hasWristband());
                    from = servicePoints[rideOrder.remove(0) + ticketBoothCount - 1].getRideID();
                    to = 100;
                    controller.updateConsole("Customer " + c.getId() + " rode ride " + from + " and goes to the restaurant");
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
                controller.updateConsole("Customer " + c.getId() + " left the restaurant and is ready to leave the park");
                //System.out.println("from " + 100 + " to " + -101); //FROMTO

                double average = c.report();
                double ticketWristRatio = getWristbandTicketAverageRatio();
                if (!Double.isNaN(ticketWristRatio)) {
                    System.out.printf("Lippuja ostaneiden viipymä suhteessa rannekkeellisten viipymään: %.2f\n", ticketWristRatio);
                } else {
                    System.out.println("Lippu-asiakkaiden ja rannekkeellisten viipymäaikojen suhde ei voida vielä laskea.");
                }
                if (c.hasWristband()) {
                    wristbandAverages.add(average);
                } else {
                    ticketAverages.add(average);
                }
                break;
        }
        controller.updateEventTime(Clock.getInstance().getTime());
        controller.addCustomerToAnimation(from, to);
    }

    @Override
    protected void attemptCEvents() {
        for (ServicePoint p : servicePoints) {
            if (!p.isReserved() && p.isInQueue()) {
                p.beginService();
            }
        }
    }

    public int nextTicketBooth() {
        ticketBoothCounter++;
        if (ticketBoothCounter >= ticketBoothCount) {
            ticketBoothCounter = 0;
        }
        return ticketBoothCounter;
    }

    public void setWristbandChance(double amount) {
        wristbandChance = amount/100;
        if (wristbandChance > 1) {
            wristbandChance = 1;
        } else if (wristbandChance < 0) {
            wristbandChance = 0;
        }
        System.out.println("Wristband chance: " + wristbandChance);
        this.bernoulli = new Bernoulli(this.wristbandChance);
        bernoulli = new Bernoulli(wristbandChance);
    }

    public double getWristbandChance() {
        return wristbandChance;
    }


    @Override
    protected void results() {
        double endTime = Clock.getInstance().getTime();
        System.out.println("Simulointi päättyi kello " + endTime);
        results.put("End time", endTime);

        //Asiakkaat:
        int unreadyCustomers = Customer.getI() - readyCustomers;
        System.out.println("Valmiita asiakkaita: " + readyCustomers + ", joista rannekeasiakkaita oli " + wristbandAverages.size() + " ja lippuasiakkaita " + ticketAverages.size());
        System.out.println("Kesken jääneiden asiakkaiden määrä: " + unreadyCustomers);
        results.put("Ready customers", (double) readyCustomers);
        results.put("Ticket customers", (double) ticketAverages.size());
        results.put("Wristband customers", (double) wristbandAverages.size());
        results.put("Unready customers", (double) unreadyCustomers);

        //Lippuluukku (ticketboothCounterSumiin lisätään vasta kun asiakas valmis, joten lasketaan valmiilla asiakkailla:
        System.out.printf("Lippuasiakas kävi keskimäärin %.2f kertaa lippuluukulla.\n", Customer.getTicketboothCounterAverage());
        System.out.printf(("Simulaation aikana ostetut liput: %d \n"), Customer.getTotalTicketCount());
        results.put("Ticket booth average", Customer.getTicketboothCounterAverage());
        results.put("Total ticket count", (double)Customer.getTotalTicketCount());

        //Viipymät:
        System.out.printf("Rannekkeellisten keskimääräinen viipymäaika: %.2f\n", getAverageWristbandTime());
        System.out.printf("Lippujen ostajien keskimääräinen viipymäaika: %.2f\n", getAverageTicketTime());
        System.out.printf("Asiakkaiden keskimääräinen viipymäaika: %.2f\n", getWholeAverage());
        System.out.printf("Lippuja ostaneiden viipymä suhteessa rannekkeellisten viipymään: %.2f\n", getWristbandTicketAverageRatio());
        results.put("Wristband average time", getAverageWristbandTime());
        results.put("Ticket average time", getAverageTicketTime());
        results.put("Whole average time", getWholeAverage());
        results.put("Wristband ticket ratio", getWristbandTicketAverageRatio());

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
        System.out.println("Results hashmap: " + results);
        System.out.println("DynamicResults hashmap: " + dynamicResults);

        // UUTTA graafista
        ResultsController resultsController = new ResultsController();
        resultsController.visualizeResults(results, dynamicResults);
        controller.showEndTime(Clock.getInstance().getTime());
        controller.closeSimulation();
        dao.incrementSimId();
        dao.incrementSimId();
        dao.addRide(60,60,60,60,60,60);
        dao.addRestaurant(60, 60, 60);
        dao.addTicketBooth(60, 60, 60);
        dao.addServicePoint(60,60,60,60,60,60,60,60,60,60);
    }


    protected double getWholeAverage() {
        if (wristbandAverages.isEmpty() && ticketAverages.isEmpty()) {
            return 0.00;
        } else if (wristbandAverages.isEmpty()) {
            return getAverageTicketTime();
        } else if (ticketAverages.isEmpty()) {
            return getAverageWristbandTime();
        }
        return (getAverageTicketTime() + getAverageWristbandTime()) / 2;
    }

    public double getAverageWristbandTime() {
        if (wristbandAverages.isEmpty()) {
            return 0.00;
        } else {
            double sum = 0;
            for (double time : wristbandAverages) {
                sum += time;
            }
            return sum / wristbandAverages.size();
        }
    }

    public double getAverageTicketTime() {
        if (ticketAverages.isEmpty()) {
            return 0.00;
        } else {
            double sum = 0;
            for (double time : ticketAverages) {
                sum += time;
            }
            return sum / ticketAverages.size();
        }
    }

    public double getWristbandTicketAverageRatio() {
        if (wristbandAverages.isEmpty() || ticketAverages.isEmpty()) {
            return 0.00;
        }
         else {
             return getAverageTicketTime() / getAverageWristbandTime();
        }
    }

    public HashMap<String, Double> getStaticResults() {
        return results;
    }

    public TreeMap<String, Double> getDynamicResults() {
        return dynamicResults;
    }
}

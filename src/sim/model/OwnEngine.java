package sim.model;

import sim.framework.*;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;

public class OwnEngine extends sim.framework.Engine {

    private ArrivalProcess arrivalProcess;

    private ServicePoint[] servicePoints;

    public OwnEngine() {

        servicePoints = new ServicePoint[5];

        servicePoints[0] = new ServicePoint(new Normal(7, 4), eventList, EventType.TICKET);

        //Laitteilla vähän eri ajoajat, mutta hajonta kaikissa pientä
        servicePoints[1] = new ServicePoint(new Normal(3, 1), eventList, EventType.RIDE1);
        servicePoints[2] = new ServicePoint(new Normal(5, 2), eventList, EventType.RIDE2);
        servicePoints[3] = new ServicePoint(new Normal(2, 1), eventList, EventType.RIDE3);

        servicePoints[4] = new ServicePoint(new Normal(10, 5), eventList, EventType.RESTAURANT);
        servicePoints[4].setRestaurant(true);
        servicePoints[4].setRestaurantCapacity(50);


        arrivalProcess = new ArrivalProcess(new Negexp(5, 5), eventList, EventType.ENTRENCE);

    }

    @Override
    protected void init() {
        arrivalProcess.generateNext(); // Ensimmäinen saapuminen järjestelmään
    }

    private ServicePoint selectNextQueue(Customer c) {
        for (ServicePoint p : servicePoints) {
            if (c.getEventListSize() > 0 && p.getScheduledEventType() == c.peekNextRide()) {
                return p;
            }
        }
        return servicePoints[4];
    }

    private boolean addToOtherQueue(Customer c, ServicePoint rideQueue, ServicePoint ticketQueue){   //
        if ((c.isWristband() || c.isTicket()) && c.ridesLeft()) {
            rideQueue.addToQueue(c);
            c.removeNextRide();
            if (!c.isWristband()) {
                c.removeTicket();
                return true;
            }
        } else if (c.ridesLeft()) {
            ticketQueue.addToQueue(c);
            Trace.out(Trace.Level.INFO, "Customer " + c.getId() + " goes to ticket queue");//Tämä vaan tsekkinä, että toimiiko
        } else {
            servicePoints[4].addToQueue(c);
            c.removeNextRide();
        }
        return false;
    }

    @Override
    protected void processEvent(Event t) {  // B-vaiheen tapahtumat

        Customer c;
        ServicePoint nextService;
        switch ((EventType) t.getType()) {

            case ENTRENCE:
                Customer customer = new Customer();
                if (customer.isWristband()){
                    nextService = selectNextQueue(customer);
                    nextService.addToQueue(customer);
                    customer.removeNextRide();
                    Trace.out(Trace.Level.INFO, "Customer " + customer.getId() + " goes to " + nextService.getScheduledEventType() + " queue");
                } else {
                    servicePoints[0].addToQueue(customer);
                    Trace.out(Trace.Level.INFO, "Customer " + customer.getId() + " goes to ticket queue");
                }
                arrivalProcess.generateNext();
                break;
            case TICKET:
                c = (Customer) servicePoints[0].fetchFromQueue();
                c.addTicket();
                nextService = selectNextQueue(c);
                nextService.addToQueue(c);
                c.removeNextRide();
                c.removeTicket();
                Trace.out(Trace.Level.INFO, "Customer " + c.getId() + " goes to " + nextService.getScheduledEventType() + " queue");
                break;
            case RIDE1:
                c = (Customer) servicePoints[1].fetchFromQueue();
                nextService = selectNextQueue(c);
                if (addToOtherQueue(c, nextService, servicePoints[0])) {
                    Trace.out(Trace.Level.INFO, "Customer " + c.getId() + " goes to " + nextService.getScheduledEventType() + " queue");
                    //c.removeNextRide();
                }
                break;
            case RIDE2:
                c = (Customer) servicePoints[2].fetchFromQueue();
                nextService = selectNextQueue(c);
                if (addToOtherQueue(c, nextService, servicePoints[0])) {
                    Trace.out(Trace.Level.INFO, "Customer " + c.getId() + " goes to " + nextService.getScheduledEventType() + " queue");
                    //c.removeNextRide();
                }
                break;
            case RIDE3:
                c = (Customer) servicePoints[3].fetchFromQueue();
                if (c == null) {
                    System.out.println("quf");
                }
                nextService = selectNextQueue(c);
                if (addToOtherQueue(c, nextService, servicePoints[0])) {
                    Trace.out(Trace.Level.INFO, "Customer " + c.getId() + " goes to " + nextService.getScheduledEventType() + " queue");
                    //c.removeNextRide();
                }
                break;

            case RESTAURANT:
                if (servicePoints[4].hasRoomInRestaurant()) {
                    c = (Customer) servicePoints[4].fetchFromRestaurantQueue();
                    if (c == null) {
                        System.out.println("fuq");
                    }
                    if (c != null) {
                        servicePoints[4].customerLeftRestaurant();
                        c.setDepartTime(Clock.getInstance().getTime());
                        c.report();
                    }
                }
        }
    }

    @Override
    protected void attemptCEvents() {
        for (ServicePoint p : servicePoints) {
            if (!p.isReserved() && p.isInQueue()) {
                if (!p.isRestaurant()|| (p.isRestaurant() && p.hasRoomInRestaurant())) {
                    p.beginService();
                }
            }
        }
    }

    @Override
    protected void results() {
        System.out.println("Simulation ended at " + Clock.getInstance().getTime());
        System.out.println("Results so far...");
        System.out.println("Total amound of customers: " + Customer.getI());
        System.out.println("Average time in system: " + Customer.getAverage());
    }


}

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

        servicePoints[4] = new ServicePoint(new Normal(45, 15), eventList, EventType.RESTAURANT);
        servicePoints[4].setRestaurant(true);
        servicePoints[4].setRestaurantCapacity(50);


        arrivalProcess = new ArrivalProcess(new Negexp(5, 5), eventList, EventType.ENTRENCE);

    }


    @Override
    protected void init() {
        arrivalProcess.generateNext(); // Ensimmäinen saapuminen järjestelmään
    }

    public void addToOtherQueue(Customer c, ServicePoint rideQueue, ServicePoint ticketQueue){   //
        if (c.isWristband() || c.isTicket()) {
            rideQueue.addToQueue(c);
        } else {
            ticketQueue.addToQueue(c);
            System.out.println("Customer " + c.getId() + " goes to ticket queue"); //Tämä vaan tsekkinä, että toimiiko
        }
    }

    @Override
    protected void processEvent(Event t) {  // B-vaiheen tapahtumat

        Customer c;
        switch ((EventType) t.getType()) {

            case ENTRENCE:
                Customer customer = new Customer();
                if (customer.isWristband()){
                    servicePoints[1].addToQueue(customer);
                } else {
                    servicePoints[0].addToQueue(customer);
                    System.out.println("Customer " + customer.getId() + " goes to ticket queue");
                }
                arrivalProcess.generateNext();
                break;
            case TICKET:
                c = (Customer) servicePoints[0].fetchFromQueue();
                c.addTicket();
                servicePoints[1].addToQueue(c);
                break;
            case RIDE1:
                c = (Customer) servicePoints[1].fetchFromQueue();
                if (!c.isWristband()){c.removeTicket();}
                addToOtherQueue(c, servicePoints[2], servicePoints[0]);
                break;
            case RIDE2:
                c = (Customer) servicePoints[2].fetchFromQueue();
                if (!c.isWristband()){c.removeTicket();}
                addToOtherQueue(c, servicePoints[3], servicePoints[0]);
                break;
            case RIDE3:
                c = (Customer) servicePoints[3].fetchFromQueue();
                if (!c.isWristband()){c.removeTicket();}
                servicePoints[4].addToQueue(c);
                break;

            case RESTAURANT:
                c = (Customer) servicePoints[4].fetchFromRestaurantQueue(); //Palauttaa siis null jos ravintola täynnä
                if (c != null) {
                    servicePoints[4].customerLeftRestaurant();
                    c.setDepartTime(Clock.getInstance().getTime());
                    c.report();
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

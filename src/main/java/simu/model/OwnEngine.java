package simu.model;

import controller.IControllerForM;
import distributions.Negexp;
import distributions.Normal;
import simu.framework.*;

import java.util.ArrayList;

public class OwnEngine extends Engine {
	
	private ArrivalProgress arrivalProgress;

	private ServicePoint[] servicePoints;

	private ArrayList<Integer> rideOrder = new ArrayList<>();

	private final int RIDE_COUNT = 3;
	private final double WRISTBAND_CHANCE = 0.5;


	public OwnEngine(IControllerForM controller){

		super(controller);

		servicePoints = new ServicePoint[RIDE_COUNT + 2];

		servicePoints[0]=new ServicePoint(new Normal(10,6), eventList, EventType.DEP_TICKET_BOOTH);  //lipunmyynti

		for (int i = 1; i <= RIDE_COUNT; i++) {
			servicePoints[i]=new ServicePoint(new Normal(10,10), eventList, EventType.DEP_RIDE); //Laitteet
		}

		servicePoints[RIDE_COUNT+1]=new RestaurantServicePoint(new Normal(80,3), eventList, EventType.DEP_RESTAURANT, 5); //Ravintola

		arrivalProgress = new ArrivalProgress(new Negexp(15,5), eventList, EventType.ARRIVAL); //Saapuminen

	}


	private ServicePoint findRideByID (int id) {
		for (ServicePoint p: servicePoints) {
			if (p.getRideID() == id) {
				return p;
			}
		}
		return null;
	}

	@Override
	protected void init() {
		arrivalProgress.generateNext(); // Ensimmäinen saapuminen järjestelmään
	}

	@Override
	protected void runEvent(Event t){  // B-vaiheen tapahtumat

		Trace.out(Trace.Level.INFO, "Ravintolassa on " + servicePoints[RIDE_COUNT + 1].getCustomerListSize() + " asiakasta.");

		Customer c;
		ServicePoint p;
		switch ((EventType) t.getType()){
			case ARRIVAL:
				c = new Customer(RIDE_COUNT, WRISTBAND_CHANCE);

				if (c.hasWristband()) {
					p = findRideByID(c.getNextRideID());

					p.addToQueue(c);
					c.removeNextRide();
					rideOrder.add(p.getRideID());

					Trace.out(Trace.Level.INFO, "Asiakas " + c.getId() + " menee laitteen " + p.getRideID() + " jonoon");

				} else {
					servicePoints[0].addToQueue(c);
					Trace.out(Trace.Level.INFO, "Asiakas " + c.getId() + " menee lippujonoon");
				}

				arrivalProgress.generateNext();
				controller.visualizeCustomer();
				break;

			case DEP_TICKET_BOOTH:
				c = servicePoints[0].fetchFromQueue();

				p = findRideByID(c.getNextRideID());

				p.addToQueue(c);
				c.removeNextRide();
				rideOrder.add(p.getRideID());

				Trace.out(Trace.Level.INFO, "Asiakas: " + c.getId() + " menee laitteen " + p.getRideID() + " jonoon");

				break;

			case DEP_RIDE:
				c = servicePoints[rideOrder.remove(0)].fetchFromQueue();

				if (c.ridesLeft()) {
					if (c.hasWristband()) {
						p = findRideByID(c.getNextRideID());

						p.addToQueue(c);
						c.removeNextRide();
						rideOrder.add(p.getRideID());

						Trace.out(Trace.Level.INFO, "Asiakas " + c.getId() + " menee laitteeseen: " + p.getRideID());
					} else {
						servicePoints[0].addToQueue(c);
						Trace.out(Trace.Level.INFO, "Asiakas " + c.getId() + " menee lippujonoon");
					}
				} else {
					servicePoints[RIDE_COUNT + 1].addToQueue(c);
					Trace.out(Trace.Level.INFO, "Asiakas " + c.getId() + " menee ravintolajonoon");
				}

				break;

			case DEP_RESTAURANT:
				c = servicePoints[RIDE_COUNT + 1].fetchFromCustomerList();
				c.setDepartureTime(Clock.getInstance().getTime());
				c.report();

				break;
		}
	}

	@Override
	protected void attemptCEvents(){
		for (ServicePoint p: servicePoints){
			if (!p.isReserved() && p.isInQueue()){
				p.beginService();
			}
		}
	}

	@Override
	protected void results() {
		System.out.println("Simulointi päättyi kello " + Clock.getInstance().getTime());
		System.out.println("Tulokset ... puuttuvat vielä");

		// UUTTA graafista
		controller.showEndTime(Clock.getInstance().getTime());
	}

	
}

package simu.model;

import controller.IControllerForM;
import distributions.Bernoulli;
import distributions.Negexp;
import distributions.Normal;
import simu.framework.*;

import java.util.ArrayList;

public class OwnEngine extends Engine {
	
	private ArrivalProgress arrivalProgress;

	private ServicePoint[] servicePoints;

	private ArrayList<Integer> rideOrder = new ArrayList<>();

	private double wristbandChance = 0.5;
	private Bernoulli bernoulli = new Bernoulli(wristbandChance);
	private int rideCount;
	private ArrayList<Double> averages = new ArrayList<>();


	public OwnEngine(IControllerForM controller, int rideCount){

		super(controller);
		this.rideCount = rideCount;

		servicePoints = new ServicePoint[rideCount + 2];

		servicePoints[0]=new ServicePoint(new Normal(5,2), eventList, EventType.DEP_TICKET_BOOTH);  //lipunmyynti

		for (int i = 1; i <= rideCount; i++) {
			servicePoints[i]=new ServicePoint(new Normal(10,10), eventList, EventType.DEP_RIDE); //Laitteet
		}

		servicePoints[rideCount+1]=new RestaurantServicePoint(new Normal(80,3), eventList, EventType.DEP_RESTAURANT, 5); //Ravintola

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

		Trace.out(Trace.Level.INFO, "Ravintolassa on " + servicePoints[rideCount + 1].getCustomerListSize() + " asiakasta.");

		Customer c;
		ServicePoint p;
		switch ((EventType) t.getType()){
			case ARRIVAL:
				double sample = bernoulli.sample();
				c = new Customer(rideCount, sample);

				if (c.hasWristband()) {
					p = findRideByID(c.getNextRideID());
					controller.visualizeCustomer(c.getId(), p.getRideID(), true);

					p.addToQueue(c);
					c.removeNextRide();
					rideOrder.add(p.getRideID());

					Trace.out(Trace.Level.INFO, "Asiakas " + c.getId() + " menee laitteen " + p.getRideID() + " jonoon");

				} else {
					servicePoints[0].addToQueue(c);
					controller.visualizeCustomer(c.getId(), 0, false);
					Trace.out(Trace.Level.INFO, "Asiakas " + c.getId() + " menee lippujonoon");
				}

				arrivalProgress.generateNext();
				break;

			case DEP_TICKET_BOOTH:
				c = servicePoints[0].fetchFromQueue();

				p = findRideByID(c.getNextRideID());
				controller.visualizeCustomer(c.getId(), p.getRideID(), c.hasWristband());

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
						controller.visualizeCustomer(c.getId(), p.getRideID(), c.hasWristband());

						p.addToQueue(c);
						c.removeNextRide();
						rideOrder.add(p.getRideID());

						Trace.out(Trace.Level.INFO, "Asiakas " + c.getId() + " menee laitteeseen: " + p.getRideID());
					} else {
						servicePoints[0].addToQueue(c);
						controller.visualizeCustomer(c.getId(), 0, c.hasWristband());
						Trace.out(Trace.Level.INFO, "Asiakas " + c.getId() + " menee lippujonoon");
					}
				} else {
					servicePoints[rideCount + 1].addToQueue(c);
					controller.visualizeCustomer(c.getId(), rideCount + 1, c.hasWristband());
					Trace.out(Trace.Level.INFO, "Asiakas " + c.getId() + " menee ravintolajonoon");
				}

				break;

			case DEP_RESTAURANT:
				c = servicePoints[rideCount + 1].fetchFromCustomerList();
				controller.visualizeCustomer(c.getId(), rideCount + 2, c.hasWristband());
				c.setDepartureTime(Clock.getInstance().getTime());
				double average = c.report();
				averages.add(average);

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

	public void setWristbandChance(double amount){
		wristbandChance += amount;
		if (wristbandChance > 1) {
			wristbandChance = 1;
		} else if (wristbandChance < 0) {
			wristbandChance = 0;
		}
		System.out.println("Wristband chance: " + wristbandChance);
		bernoulli = new Bernoulli(wristbandChance);
	}

	public double getWristbandChance(){
		return wristbandChance;
	}

	@Override
	protected void results() {
		System.out.println("Simulointi päättyi kello " + Clock.getInstance().getTime());
		System.out.println("Valmiita asiakkaita: " + averages.size());
		System.out.printf("Asiakkaiden keskimääräinen viipymäaika: %.2f\n", getWholeAverage());
		System.out.println("Asiakkaita vielä huvipuistossa: " + (Customer.getI()- averages.size()));
		System.out.println("Asiakkaiden keskimääräinen palveluaika ravintolassa: " + servicePoints[rideCount + 1].getAverageServiceTime());
		System.out.println("Asiakkaiden keskimääräinen aika laitteissa: " + getAverageRideTime());
		// UUTTA graafista
		controller.visualizeResults();
		controller.showEndTime(Clock.getInstance().getTime());
	}

	protected double getWholeAverage() {
		int sum = 0;
		for (int i = 0; i < averages.size(); i++) {
			sum += averages.get(i);
		}
		return (double) sum / averages.size();
	}

	protected double getAverageRideTime() {
		double sum = 0;
		for (int i = 1; i < servicePoints.length-1; i++) {
			sum += servicePoints[i].getAverageServiceTime();
		} return sum / (servicePoints.length-2);
	}
}

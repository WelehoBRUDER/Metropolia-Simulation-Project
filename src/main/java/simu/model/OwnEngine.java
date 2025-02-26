package simu.model;

import controller.IControllerForM;
import distributions.Bernoulli;
import distributions.Negexp;
import distributions.Normal;
import simu.framework.*;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class OwnEngine extends Engine {
	
	private ArrivalProgress arrivalProgress;

	private ServicePoint[] servicePoints;

	private ArrayList<Integer> rideOrder = new ArrayList<>();

	private double wristbandChance = 0.5;
	private Bernoulli bernoulli = new Bernoulli(wristbandChance);
	private int rideCount;
	private ArrayList<Double> wristbandAverages = new ArrayList<>();
	private ArrayList<Double> ticketAverages = new ArrayList<>();


	public OwnEngine(IControllerForM controller, int rideCount){

		super(controller);
		this.rideCount = rideCount;

		servicePoints = new ServicePoint[rideCount + 2];

		servicePoints[0]=new ServicePoint(new Normal(5,2), eventList, EventType.DEP_TICKET_BOOTH, rideCount);  //lipunmyynti

		for (int i = 1; i <= rideCount; i++) {
			servicePoints[i]=new ServicePoint(new Normal(10,10), eventList, EventType.DEP_RIDE, rideCount); //Laitteet
		}

		servicePoints[rideCount+1]=new RestaurantServicePoint(new Normal(80,3), eventList, EventType.DEP_RESTAURANT, rideCount, 20); //Ravintola

		//arrivalProgress = new ArrivalProgress(new Negexp(15, 5), eventList, EventType.ARRIVAL); //Saapuminen, Tällä asiakkaat saapuvat n. 15 aikayksikön välein eli aika harvoin
		arrivalProgress = new ArrivalProgress(new Negexp(5, 5), eventList, EventType.ARRIVAL); //Saapuminen
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
				servicePoints[0].incrementCustomerCounter();

				p = findRideByID(c.getNextRideID());
				controller.visualizeCustomer(c.getId(), p.getRideID(), c.hasWristband());

				p.addToQueue(c);
				c.removeNextRide();
				rideOrder.add(p.getRideID());

				Trace.out(Trace.Level.INFO, "Asiakas: " + c.getId() + " menee laitteen " + p.getRideID() + " jonoon");

				break;

			case DEP_RIDE:
				servicePoints[rideOrder.get(0)].incrementCustomerCounter();
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
				servicePoints[rideCount + 1].incrementCustomerCounter();
				controller.visualizeCustomer(c.getId(), rideCount + 2, c.hasWristband());
				c.setDepartureTime(Clock.getInstance().getTime());
				double average = c.report();
				if (c.hasWristband()) {
					wristbandAverages.add(average);
				} else {
					ticketAverages.add(average);
				}
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
		int readyCustomers = wristbandAverages.size() + ticketAverages.size();

		System.out.println("Valmiita asiakkaita: " + readyCustomers);
		System.out.println("Kesken jääneiden asiakkaiden määrä: " + (Customer.getI() - readyCustomers));

		System.out.printf("Rannekkeellisten keskimääräinen viipymäaika: %.2f\n", getAverageWristbandTime());
		System.out.printf("Lippujen ostajien keskimääräinen viipymäaika: %.2f\n", getAverageTicketTime());
		System.out.printf("Asiakkaiden keskimääräinen viipymäaika: %.2f\n", getWholeAverage());
		System.out.printf("Lippuja ostaneiden viipymä suhteessa rannekkeellisten viipymään: %.2f\n", getWristbandTicketAverageRatio());

		for (ServicePoint point: servicePoints) {
			if (!(point instanceof RestaurantServicePoint)) {
				if (point.getRideID() == 0) {
					System.out.println("Lippupisteessä käytiin " + point.getCustomerCounter() + " kertaa.");
					System.out.println("Lippupisteessä keskimääräinen palveluaika: " + point.getAverageServiceTime());
					System.out.println("Lippupisteessä keskimääräinen jonotusaika: " + point.getAverageQueueTime());
				} else {
					System.out.println("Laitteessa " + point.getRideID() + " palveltiin " + point.getCustomerCounter() + " asiakasta.");
					System.out.println("Laitteessa " + point.getRideID() + " keskimääräinen palveluaika: " + point.getAverageServiceTime());
					System.out.println("Laitteessa " + point.getRideID() + " keskimääräinen jonotusaika: " + point.getAverageQueueTime());
				}
			} else {
				System.out.println("Ravintolassa palveltiin " + point.getCustomerCounter() + " asiakasta.");
				System.out.println("Ravintolassa keskimääräinen palveluaika: " + point.getAverageServiceTime());
				System.out.println("Ravintolassa keskimääräinen jonotusaika: " + point.getAverageQueueTime());
			}
		}

		// Nämä vielä tässä jos metodeille tarvetta
//		serviceTimes(); //For loop, joka käy läpi kaikki palvelupisteet ja tulostaa niiden palveluajan (point.getAverageServiceTime())
//		queueLengths(); //For loop, joka käy läpi kaikki palvelupisteet ja tulostaa niiden jonon pituuden (point.getAverageQueueTime())
//		customerCounts(); //For loop, joka käy läpi kaikki palvelupisteet ja tulostaa niiden asiakasmäärän (point.getCustomerCounter())
//
		// UUTTA graafista
		controller.visualizeResults();
		controller.showEndTime(Clock.getInstance().getTime());
	}

	protected double getWholeAverage() {
		return (getAverageTicketTime() + getAverageWristbandTime()) / 2;
	}

	public double getAverageWristbandTime() {
		double sum = 0;
		for (double time : wristbandAverages) {
			sum += time;
		}
		return sum / wristbandAverages.size();
	}

	public double getAverageTicketTime() {
		double sum = 0;
		for (double time : ticketAverages) {
			sum += time;
		}
		return sum / ticketAverages.size();
	}

//	protected void queueLengths() {
//
//		for (ServicePoint point : servicePoints) {
//			double averageQueueTime = point.getAverageQueueTime();
//			if (!(point instanceof RestaurantServicePoint)) {
//				if (point.getRideID() == 0) {
//					System.out.println("Keskimääräinen jonotusaika lippupisteessä: " + averageQueueTime);
//				} else {
//					System.out.println("Keskimääräinen jonotusaika laitteessa " + point.getRideID() + " on " + averageQueueTime);
//				}
//			}
//			else {
//				System.out.println("Keskimääräinen jonotusaika ravintolassa: " + averageQueueTime);
//			}
//		}
//	}
//
//	public void serviceTimes() {
//		for (ServicePoint point: servicePoints) {
//			if (!(point instanceof RestaurantServicePoint)) {
//				if (point.getRideID() == 0) {
//					System.out.println("Lippupisteen keskimääräinen palveluaika: " + point.getAverageServiceTime());
//				} else {
//					System.out.println("Laitteen " + point.getRideID() + " keskimääräinen palveluaika: " + point.getAverageServiceTime());
//				}
//			} else {
//				System.out.println("Ravintolan keskimääräinen palveluaika: " + point.getAverageServiceTime());
//			}
//		}
//	}
//
//	public void customerCounts(){
//		for (ServicePoint point: servicePoints) {
//			if (!(point instanceof RestaurantServicePoint)) {
//				if (point.getRideID() == 0) {
//					System.out.println("Lippupisteessä käytiin " + point.getCustomerCounter() + " kertaa.");
//				} else {
//					System.out.println("Laitteessa " + point.getRideID() + " palveltiin " + point.getCustomerCounter() + " asiakasta.");
//				}
//			} else {
//				System.out.println("Ravintolassa palveltiin " + point.getCustomerCounter() + " asiakasta.");
//			}
//		}
//	}

	public double getWristbandTicketAverageRatio(){
		return getAverageTicketTime() / getAverageWristbandTime();
	}
}

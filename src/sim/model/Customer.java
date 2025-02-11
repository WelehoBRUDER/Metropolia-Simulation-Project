package sim.model;

import sim.framework.*;

// TODO:
// Asiakas koodataan simulointimallin edellyttämällä tavalla (data!)
public class Customer {
	private double arrivalTime;
	private double departTime;
	private int id;
	private static int i = 1;
	private static long sum = 0;
	private boolean wristband;
	private boolean ticket;
	
	public Customer(){
	    id = i++;
		if (id%2 == 0){
	        wristband = true;
	    } else {
	        wristband = false;
	    }
	    
		arrivalTime = Clock.getInstance().getTime();
		Trace.out(Trace.Level.INFO, "New customer number " + id + " arrived at  "+arrivalTime);
		if (wristband) {
			Trace.out(Trace.Level.INFO, "Customer " + id + " has a wristband");
		} else {
			Trace.out(Trace.Level.INFO, "Customer " + id + " does not have a wristband");
		}
	}

	public double getDepartTime() {
		return departTime;
	}

	public void setDepartTime(double departTime) {
		this.departTime = departTime;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(double arrivalTime) {
			this.arrivalTime = arrivalTime;
	}

	public int getId() {
		return id;
	}

	public void addTicket(){
		ticket = true;
	}

	public void removeTicket(){
		ticket = false;
	}

	public boolean isWristband() {
		return wristband;
	}

	public boolean isTicket() {
		return ticket;
	}

	public static double getAverage() {
		return (double)sum/i;
	}

	public static int getI() {
		return i; // i = customer amount
	}

	public void report(){
		Trace.out(Trace.Level.INFO, "\nCustomer "+id+ " done! ");
		Trace.out(Trace.Level.INFO, "Customer "+id+ " arrived at: " +arrivalTime);
		Trace.out(Trace.Level.INFO,"Customer "+id+ " departed at: " +departTime);
		Trace.out(Trace.Level.INFO,"Customer "+id+ " stayed for: " +(departTime-arrivalTime));
		sum += (departTime-arrivalTime);
		double average = sum/id;
		System.out.println("Average time customers spent in the service:  "+ average);
	}

}

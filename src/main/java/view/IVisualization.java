package view;


public interface IVisualization {

	public void clearScreen();

	public void newCustomer(int id, int rideID, boolean wristband);

	public void showResults();

	public void rides(int rideCount);
		
}


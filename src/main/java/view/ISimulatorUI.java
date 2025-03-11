package view;

/**
 * ISimulatorUI is an interface for the user interface of the simulation.
 */
public interface ISimulatorUI {
	/**
	 * Gets the time for the simulation.
	 * @return The time for the simulation.
	 */
	public double getTime();
	/**
	 * Sets the end time for the simulation.
	 * @param time The simulation time.
	 */
	public void setEndTime(double time);


}

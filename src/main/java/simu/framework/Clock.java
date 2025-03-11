package simu.framework;

/**
 * Clock is a singleton class that represents the time in the simulation.
 */
public class Clock {

	/**
	 * The time for the simulation.
	 */
	private double time;
	/**
	 * The instance of the clock.
	 */
	private static Clock instance;

	/**
	 * Constructor for the Clock class.
	 */
	private Clock(){
		time = 0;
	}

	/**
	 * Gets the instance of the clock.
	 * @return The instance of the clock.
	 */
	public static Clock getInstance(){
		if (instance == null){
			instance = new Clock();
		}
		return instance;
	}

	/**
	 * Sets the time for the simulation.
	 * @param time The time for the simulation.
	 */
	public void setTime(double time){
		this.time = time;
	}

	/**
	 * Gets the time for the simulation.
	 * @return The time for the simulation.
	 */
	public double getTime(){
		return time;
	}
}

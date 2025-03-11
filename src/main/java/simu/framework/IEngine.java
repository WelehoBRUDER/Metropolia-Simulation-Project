package simu.framework;

/**
 * IEngine is the interface for the simulation engine. The interface is used to manage the simulation.
 * Implemented y {@link simu.framework.Engine} and {@link simu.model.OwnEngine}.
 */
public interface IEngine {

	/**
	 * Sets the simulation time for the simulation.
	 * @param time How long the simulation will run.
	 */
	public void setSimulationTime(double time);

	/**
	 * Sets the delay in the simulation.
	 * @param time Delay for the simulation.
	 */
	public void setDelay(long time);

	/**
	 * Gets the delay for the simulation.
	 * @return The delay for the simulation.
	 */
	public long getDelay();
    void setWristbandChance(double wristbandChanceValue);
}

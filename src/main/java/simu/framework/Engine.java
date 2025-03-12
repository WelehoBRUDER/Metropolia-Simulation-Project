package simu.framework;

import controller.ISettingsControllerForM;

/**
 * Base implementation of {@link IEngine}. The class is used to manage the simulation.
 * Extended by {@link simu.model.OwnEngine}.
 */

public abstract class Engine extends Thread implements IEngine {

	/**
	 * The simulation time for the simulation.
	 */
	private double simulationTime = 0;
	/**
	 * The delay for the simulation.
	 */
	private long delay = 0;

	/**
	 * The clock for the simulation.
	 */
	private Clock clock;

	/**
	 * The event list for the simulation.
	 */
	protected EventList eventList;
	/**
	 * The controller for the simulation.
	 */
	protected ISettingsControllerForM controller;
	
	/**
	 * Constructor for the Engine class. Starts the clock and creates a new event list.
	 * Is defined in the simu.model package in a subclass of the Engine class.
	 * @param controller The controller for the simulation.
	 */
	public Engine(ISettingsControllerForM controller){
		
		this.controller = controller;

		clock = Clock.getInstance();
		
		eventList = new EventList();
	}

	/**
	 * Sets the simulation time for the simulation.
	 * @param time How long the simulation will run.
	 */
	@Override
	public void setSimulationTime(double time) {
		simulationTime = time;
	}

	/**
	 * Sets the delay for the animation of the simulation.
	 * @param delay Time delay for the animation.
	 */
	@Override
	public void setDelay(long delay) {
		this.delay = delay;
	}

	/**
	 * Gets the delay for the animation of the simulation.
	 * @return The delay for the animation.
	 */
	@Override
	public long getDelay() {
		return delay;
	}

	/**
	 * Runs the simulation. The simulation is divided into three phases: A-phase, B-phase and C-phase.
	 * The A-phase sets the time for the simulation. The B-phase runs the events in the event list.
	 * The C-phase attempts to run the events in the event list.
	 * Is defined in the simu.model package in a subclass of the Engine class.
	 * The results of the simulation are printed out when simulation time is over.
	 */
	@Override
	public void run(){
		init();
		while (simulating()){
			delay();

			Trace.out(Trace.Level.INFO, "\nA-phase: time is " + currentTime());
			clock.setTime(currentTime());

			Trace.out(Trace.Level.INFO, "\nB-phase:" );
			runBEvents();

			Trace.out(Trace.Level.INFO, "\nC-phase:" );
			attemptCEvents();
		}
		results();
		
	}

	/**
	 * Runs the B events in the simulation and moves the customer animation in the visualization.
	 */
	private void runBEvents(){
		controller.newAnimation();
		while (eventList.getNextTime() == clock.getTime()){
			runEvent(eventList.remove());
		}
		controller.moveCustomerAnimation();
	}

	/**
	 * Abstract method for the C events in the simulation. Defined in the simu.model package in a subclass of the Engine class.
	 */
	protected abstract void attemptCEvents();

	/**
	 * Gets the current time of the simulation from the event list (next B event).
	 * @return The new current time of the simulation.
	 */
	private double currentTime(){
			return eventList.getNextTime();
	}

	/**
	 * Checks if the simulation is still running.
	 * @return True if the simulation is still running, false otherwise.
	 */
	private boolean simulating(){
		//Trace.out(Trace.Level.INFO, "Kello on: " + clock.getTime());
		return clock.getTime() < simulationTime;
	}

	/**
	 * Delays the simulation for the animation.
 	 */
	private void delay() { // UUSI
		Trace.out(Trace.Level.INFO, "\nDelay " + delay);
		try {
			sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the simulation. Creates the first event for the simulation.
	 * Is defined in the simu.model package in a subclass of the Engine class.
	 */
	protected abstract void init(); // Määritellään simu.model-pakkauksessa Moottorin aliluokassa

	/**
	 * Runs the event in the simulation. Is defined in the simu.model package in a subclass of the Engine class.
	 * @param t The event to be run.
	 */
	protected abstract void runEvent(Event t);  // Määritellään simu.model-pakkauksessa Moottorin aliluokassa

	/**
	 * Prints out the results of the simulation.
	 * Is defined in the simu.model package in a subclass of the Engine class.
	 */
	protected abstract void results(); // Määritellään simu.model-pakkauksessa Moottorin aliluokassa
	
}
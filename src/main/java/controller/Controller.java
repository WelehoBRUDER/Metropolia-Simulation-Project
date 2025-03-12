package controller;

import javafx.application.Platform;
import simu.framework.IEngine;
import simu.model.OwnEngine;
import view.ISimulatorUI;
import java.util.TreeMap;
import java.util.HashMap;

/**
 * Controller is the class for the controller in the simulation. Implements ISettingsControllerForM.
 */
public class Controller implements ISettingsControllerForM {
	/**
	 * The user interface for the simulation.
	 */
	private ISimulatorUI ui;

	/**
	 * Constructor for the Controller class.
	 * @param ui The user interface for the simulation.
	 */
	public Controller(ISimulatorUI ui) {
		this.ui = ui;
	}

	/**
	 * Shows the end time of the simulation.
	 */
	@Override
	public void showEndTime(double time) {
		Platform.runLater(()->ui.setEndTime(time));
	}

	/**
	 * Shows the current time of the simulation.
	 * @param staticResults The static results of the simulation, that is the results that all simulations have.
	 * @param dynamicResults The dynamic results of the simulation, that is the results for all the service points in the simulation.
	 */
	@Override
	public void visualizeResults(HashMap<String, Double> staticResults, TreeMap<String, Double> dynamicResults) {

	}

	/**
	 * Moves the customer in the simulation.
	 */
	@Override
	public void moveCustomerAnimation() {

	}

	/**
	 * Creates a new animation.
	 */
	@Override
	public void newAnimation() {

	}

	/**
	 * Adds a customer to the animation.
	 * @param from The service point the customer is coming from.
	 * @param to The service point the customer is going to.
	 */
	@Override
	public void addCustomerToAnimation(int from, int to) {

	}

	/** Updates the time of the event.
	 * @param time The time of the event.
	 */
	@Override
	public void updateEventTime(double time) {

	}

	/**
	 * Closes the simulation.
	 */
	@Override
	public void closeSimulation() {

	}

	/**
	 * Updates the console with a message.
	 * @param msg The message to be displayed in the console.
	 */
	public void updateConsole(String msg) {
	}

}

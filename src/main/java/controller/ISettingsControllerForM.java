package controller;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * ISettingsControllerForM is the interface for the controller class for the model.
 */
public interface ISettingsControllerForM {

    /**
     * Shows the end time of the simulation.
     * @param time The end time of the simulation.
     */
    public void showEndTime(double time);

    /**
     * Visualizes the results of the simulation.
     * @param staticResults The static results of the simulation, that is the results that all simulations have.
     * @param dynamicResults The dynamic results of the simulation, that is the results for all the service points in the simulation.
     */
    public void visualizeResults(HashMap<String, Double> staticResults, TreeMap<String, Double> dynamicResults);
    /**
     * Moves the customer in the animation.
     */
    public void moveCustomerAnimation();
    /**
     * Creates a new animation.
     */
    public void newAnimation();

    /**
     * Adds a customer to the animation.
     * @param from
     * @param to
     */
    public void addCustomerToAnimation(int from, int to);
    /**
     * Updates the event time.
     * @param time The time of the event.
     */
    public void updateEventTime(double time);
    /**
     * Updates the console with a message.
     * @param msg The message to be displayed in the console.
     */
    public void updateConsole(String msg);
    /**
     * Closes the simulation.
     */
    public void closeSimulation();
}

package controller;

import java.util.HashMap;
import java.util.TreeMap;

public interface ISettingsControllerForM {

    // Rajapinta, joka tarjotaan moottorille:
    public void showEndTime(double aika);
    public void visualizeCustomer(int id, int rideid, boolean wristband);
    public void visualizeResults(HashMap<String, Double> staticResults, TreeMap<String, Double> dynamicResults);
    public void moveCustomerAnimation();
    public void newAnimation();
    public void addCustomerToAnimation(int from, int to);
    public void updateEventTime(double time);
}

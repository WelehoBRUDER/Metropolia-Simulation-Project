package controller;

import java.util.HashMap;
import java.util.TreeMap;

public interface ISettingsControllerForV {

    // Rajapinta, joka tarjotaan  käyttöliittymälle:
    public void slowDown();
    public void speedUp();
    public void startSimulation();
    public int getRideCount();
    public void setWristbandChance(double amount);
    public double getWristbandChance();
    public HashMap<String, Double> getStaticResults();
    public TreeMap<String, Double> getDynamicResults();
}

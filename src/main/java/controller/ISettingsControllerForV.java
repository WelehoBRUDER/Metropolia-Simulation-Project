package controller;

import java.util.HashMap;

public interface ISettingsControllerForV {

    // Rajapinta, joka tarjotaan  käyttöliittymälle:
    public void slowDown();
    public void speedUp();
    public void startSimulation();
    public int getRideCount();
    public void setWristbandChance(double amount);
    public double getWristbandChance();
    public HashMap<String, Double> getResults();
}

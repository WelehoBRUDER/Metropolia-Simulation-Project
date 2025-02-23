package controller;

public interface IControllerForV {

    // Rajapinta, joka tarjotaan  käyttöliittymälle:

    public void startSimulation();
    public void speedUp();
    public void slowDown();

    public int getRideCount();
    public void setWristbandChance(double amount);
    public double getWristbandChance();
}

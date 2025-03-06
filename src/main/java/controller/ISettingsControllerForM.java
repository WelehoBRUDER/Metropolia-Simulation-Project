package controller;

public interface ISettingsControllerForM {

    // Rajapinta, joka tarjotaan moottorille:
    public void showEndTime(double aika);
    public void visualizeCustomer(int id, int rideid, boolean wristband);
    public void visualizeResults();
    public void moveCustomerAnimation();
    public void newAnimation();
    public void addCustomerToAnimation(int from, int to);
}

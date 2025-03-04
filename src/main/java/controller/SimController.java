package controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import simu.framework.IEngine;
import simu.model.OwnEngine;

import java.util.ArrayList;

public class SimController implements ISettingsControllerForM {
    private IEngine engine;

    private int simTimeValue = 0;
    private int ticketBoothCountValue = 0;
    private int rideCountValue = 0;
    private int restaurantCapValue = 0;
    private long simDelayValue = 0;
    private int wristbandChanceValue = 0;
    private ArrayList<int[]> rideProperties = new ArrayList<>();

    @FXML
    private final Canvas servicePointCanvas;
    @FXML
    private final Canvas customerCanvas;
    private final GraphicsContext serviceCtx = servicePointCanvas.getGraphicsContext2D();
    private final GraphicsContext customerCtx = customerCanvas.getGraphicsContext2D();

    public void setSimulationParameters(int simTime, int ticketBoothCount, int rideCount, int restaurantCap, long simDelay, int wristbandChance, ArrayList<int[]> rideProperties) {
        simTimeValue = simTime;
        ticketBoothCountValue = ticketBoothCount;
        rideCountValue = rideCount;
        restaurantCapValue = restaurantCap;
        simDelayValue = simDelay;
        wristbandChanceValue = wristbandChance;
        this.rideProperties = rideProperties;
    }

    public void startSim() {
        engine = new OwnEngine(this, rideCountValue); // luodaan uusi moottorisäie jokaista simulointia varten
        engine.setSimulationTime(simTimeValue);
        engine.setDelay(simDelayValue);
        clearScreen();
        drawAllServicePoints();
        ((Thread)engine).start();
    }

    public void clearScreen() {
        serviceCtx.clearRect(0, 0, servicePointCanvas.getWidth(), servicePointCanvas.getHeight());
        customerCtx.clearRect(0, 0, customerCanvas.getWidth(), customerCanvas.getHeight());
    }

    public void drawServicePoint(int x, int y) {
        serviceCtx.setFill(Color.BLUE);
        serviceCtx.strokeRect(x, y, 20, 20);
        serviceCtx.fillText("0", x, y + 20);
    }

    public void drawAllServicePoints() {
        // Ticket booths
        for (int i = 0; i < ticketBoothCountValue; i++) {
            drawServicePoint(15, i * 30 + 15);
        }
        // Restaurant
        drawServicePoint(700, 100);
    }

    @Override
    public void showEndTime(double aika) {

    }

    @Override
    public void visualizeCustomer(int id, int rideid, boolean wristband) {

    }

    @Override
    public void visualizeResults() {

    }
}

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
    private ArrayList<int[][]> rideLocations = new ArrayList<>();

    private final int SERVICE_POINT_SIZE = 20;

    @FXML
    private Canvas servicePointCanvas;
    @FXML
    private Canvas customerCanvas;
    private GraphicsContext serviceCtx;
    private GraphicsContext customerCtx;

    // Leevin pätkä koodia
    public int[][] circleOfElements(int area, int size, int amount){
        int[][] coordinates = new int[amount][2];
        for(int i = 1; i <= amount; i++){
            coordinates[i-1][0] = (int) ((area-(0.5*size))/area * (area*Math.cos(2*Math.PI*i/amount)));
            coordinates[i-1][1] = (int) ((area-(0.5*size))/area * (area*Math.sin(2*Math.PI*i/amount)));
        }
        return coordinates;
    }

    public void setSimulationParameters(int simTime, int ticketBoothCount, int rideCount, int restaurantCap, long simDelay, int wristbandChance, ArrayList<int[]> rideProperties) {
        simTimeValue = simTime;
        ticketBoothCountValue = ticketBoothCount;
        rideCountValue = rideCount;
        restaurantCapValue = restaurantCap;
        simDelayValue = simDelay;
        wristbandChanceValue = wristbandChance;
        this.rideProperties = rideProperties;
        serviceCtx = servicePointCanvas.getGraphicsContext2D();
        customerCtx = customerCanvas.getGraphicsContext2D();
    }

    public void startSim() {
        //engine = new OwnEngine(this, rideCountValue); // luodaan uusi moottorisäie jokaista simulointia varten
        //engine.setSimulationTime(simTimeValue);
        //engine.setDelay(simDelayValue);
        clearScreen();
        drawAllServicePoints();
        //((Thread)engine).start();
    }

    public void clearScreen() {
        serviceCtx.clearRect(0, 0, servicePointCanvas.getWidth(), servicePointCanvas.getHeight());
        customerCtx.clearRect(0, 0, customerCanvas.getWidth(), customerCanvas.getHeight());
    }

    public void drawServicePoint(int x, int y) {
        serviceCtx.setFill(Color.BLUE);
        serviceCtx.strokeRect(x, y, SERVICE_POINT_SIZE, SERVICE_POINT_SIZE);
        serviceCtx.fillText("0", x, y);
    }

    public void drawAllServicePoints() {
        // Ticket booths
        for (int i = 0; i < ticketBoothCountValue; i++) {
            drawServicePoint(15, i * 30 + 15);
        }
        // Create ride service points
        rideLocations.clear();
        int[][] cords = circleOfElements(250, SERVICE_POINT_SIZE, rideCountValue);
        for (int i = 0; i < rideCountValue; i++) {
            rideLocations.add(new int[][]{cords[i]});
            drawServicePoint(cords[i][0] + 250, cords[i][1] + 250);
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

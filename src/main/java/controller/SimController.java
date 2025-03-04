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
    private Canvas servicePointCanvas;
    @FXML
    private Canvas customerCanvas;
    private GraphicsContext serviceCtx;
    private GraphicsContext customerCtx;

    // Cords
    private int[] entrance;
    private ArrayList<int[]> tickets = new ArrayList<>();
    private ArrayList<int[]> rides = new ArrayList<>();
    private int[] restaurant;

    // Canvas drawing parameters
    private final int CANVAS_WIDTH = 800;
    private final int CANVAS_HEIGHT = 500;
    private final int SERVICE_POINT_SIZE = 20;
    private final int RIDE_AREA_SIZE = 200;
    private final int TICKET_AREA_SIZE = 150;
    private final int ENTRANCE_AREA_SIZE = 50;
    private final int RESTAURANT_AREA_SIZE = 200;

    // Leevin pätkä koodia
    public int[][] circleOfElements(int area, int size, int amount) {
        int[][] coordinates = new int[amount][2];
        for (int i = 1; i <= amount; i++) {
            coordinates[i - 1][0] = (int) ((area - (0.5 * size)) / area * (area * Math.cos(2 * Math.PI * i / amount)));
            coordinates[i - 1][1] = (int) ((area - (0.5 * size)) / area * (area * Math.sin(2 * Math.PI * i / amount)));
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

    public int calcCenterX(int width, int offset) {
        return width / 2 - offset / 2;
    }

    public int calcCenterY(int offset) {
        return CANVAS_HEIGHT / 2 - offset / 2;
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


    public void setEntrance(int x, int y) {
        entrance = new int[]{x, y};
    }

    public void addToTickets(int x, int y) {
        tickets.add(new int[]{x, y});
    }

    public void addToRides(int x, int y) {
        rides.add(new int[]{x, y});
    }

    public void setRestaurant(int x, int y) {
        restaurant = new int[]{x, y};
    }

    public int[] getTicketBooth(int id) {
        return tickets.get(id);
    }

    public int[] getRide(int id) {
        return rides.get(id);
    }

    public void calcServicePointCords() {
        rides.clear();
        tickets.clear();

        setEntrance(ENTRANCE_AREA_SIZE / 2, calcCenterY(SERVICE_POINT_SIZE));

        int area = ticketBoothCountValue * SERVICE_POINT_SIZE * 2;
        int yStart = calcCenterY(area);

        for (int i = 0; i < ticketBoothCountValue; i++) {
            int x = calcCenterX(TICKET_AREA_SIZE + ENTRANCE_AREA_SIZE, SERVICE_POINT_SIZE);
            addToTickets(x, yStart + (i * SERVICE_POINT_SIZE * 2));
        }

        int[][] cords = circleOfElements(RIDE_AREA_SIZE, SERVICE_POINT_SIZE, rideCountValue);
        int xOffset = TICKET_AREA_SIZE + ENTRANCE_AREA_SIZE + RIDE_AREA_SIZE ;
        int yOffset = CANVAS_HEIGHT / 2 - RIDE_AREA_SIZE;
        for (int i = 0; i < rideCountValue; i++) {
            System.out.println(cords[i][0] + " " + cords[i][1]);
            addToRides(xOffset + cords[i][0], cords[i][1] + RIDE_AREA_SIZE + yOffset);
        }

        setRestaurant(xOffset + calcCenterX(RESTAURANT_AREA_SIZE, SERVICE_POINT_SIZE) + RIDE_AREA_SIZE, calcCenterY(SERVICE_POINT_SIZE));
    }

    public void drawAllServicePoints() {
        calcServicePointCords();

        // Entrance
        drawServicePoint(entrance[0], entrance[1]);

        // Ticket booths
        for (int i = 0; i < ticketBoothCountValue; i++) {
            int[] cords = getTicketBooth(i);
            drawServicePoint(cords[0], cords[1]);
        }
        for (int i = 0; i < rideCountValue; i++) {
            int[] cords = getRide(i);
            drawServicePoint(cords[0], cords[1]);
        }
        // Restaurant
        drawServicePoint(restaurant[0], restaurant[1]);
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

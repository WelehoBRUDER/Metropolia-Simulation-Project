package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import simu.framework.IEngine;
import simu.model.OwnEngine;

import java.util.ArrayList;
import java.util.concurrent.*;

public class SimController implements ISettingsControllerForM {
    private IEngine engine;

    private int simTimeValue = 0;
    private int ticketBoothCountValue = 0;
    private int rideCountValue = 0;
    private int restaurantCapValue = 0;
    private long simDelayValue = 0;
    private int wristbandChanceValue = 0;
    private ArrayList<int[]> rideProperties = new ArrayList<>();

    private int entranceID = -100;
    private int restaurantID = 100;
    private int exitID = -101;


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
    private int[] exit;

    // Canvas drawing parameters
    private final int CANVAS_WIDTH = 800;
    private final int CANVAS_HEIGHT = 500;
    private final int CUSTOMER_SIZE = 10;
    private final int SERVICE_POINT_SIZE = 24;
    private final int RIDE_AREA_SIZE = 200;
    private final int TICKET_AREA_SIZE = 150;
    private final int ENTRANCE_AREA_SIZE = 50;
    private final int RESTAURANT_AREA_SIZE = 200;
    private final int FONT_SIZE = SERVICE_POINT_SIZE / 2;
    private final int UPDATE_RATE_MS = 10;

    // Canvas style definitions
    private final Color ENTRANCE_COLOR = Color.GREEN;
    private final Color TICKET_COLOR = Color.BLUE;
    private final Color RIDE_COLOR = Color.ORANGE;
    private final Color RESTAURANT_COLOR = Color.RED;

    // Animation executor
    private ScheduledExecutorService executorService;

    // DEBUG PARAMS!!!

    private ArrayList<double[]> customerCords = new ArrayList<>();
    private ArrayList<int[]> customerDestination = new ArrayList<>();
    private int step = 0;

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
        clearScreen();
        drawAllServicePoints();
        engine = new OwnEngine(this, rideCountValue, ticketBoothCountValue, rideProperties, restaurantCapValue, wristbandChanceValue); // luodaan uusi moottorisäie jokaista simulointia varten
        engine.setSimulationTime(simTimeValue);
        engine.setDelay(simDelayValue);
        ((Thread) engine).start();
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

    public void drawServicePoint(int x, int y, Color color) {
        serviceCtx.setFill(color);
        serviceCtx.fillRect(x, y, SERVICE_POINT_SIZE, SERVICE_POINT_SIZE);
        serviceCtx.setFill(Color.BLACK);
        serviceCtx.setFont(new Font("Arial", FONT_SIZE));
        serviceCtx.setTextAlign(TextAlignment.CENTER);
        serviceCtx.fillText("1", x + calcCenterX(SERVICE_POINT_SIZE, 0), y - (double) FONT_SIZE / 2);
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

    public void setExit(int x, int y) {
        exit = new int[]{x, y};
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
        int xOffset = TICKET_AREA_SIZE + ENTRANCE_AREA_SIZE + RIDE_AREA_SIZE;
        int yOffset = CANVAS_HEIGHT / 2 - RIDE_AREA_SIZE;
        for (int i = 0; i < rideCountValue; i++) {
            addToRides(xOffset + cords[i][0], cords[i][1] + RIDE_AREA_SIZE + yOffset);
        }

        setRestaurant(xOffset + calcCenterX(RESTAURANT_AREA_SIZE, SERVICE_POINT_SIZE) + RIDE_AREA_SIZE, calcCenterY(SERVICE_POINT_SIZE));
        setExit(CANVAS_WIDTH, calcCenterY(SERVICE_POINT_SIZE));
    }

    public void drawAllServicePoints() {
        calcServicePointCords();

        // Entrance
        drawServicePoint(entrance[0], entrance[1], ENTRANCE_COLOR);

        // Ticket booths
        for (int i = 0; i < ticketBoothCountValue; i++) {
            int[] cords = getTicketBooth(i);
            drawServicePoint(cords[0], cords[1], TICKET_COLOR);
        }
        for (int i = 0; i < rideCountValue; i++) {
            int[] cords = getRide(i);
            drawServicePoint(cords[0], cords[1], RIDE_COLOR);
        }
        // Restaurant
        drawServicePoint(restaurant[0], restaurant[1], RESTAURANT_COLOR);
    }

    @Override
    public void moveCustomerAnimation() {
        customerCtx.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        int steps = (int) simDelayValue / UPDATE_RATE_MS;
        step = 0;
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::renderCustomers, 0, UPDATE_RATE_MS, TimeUnit.MILLISECONDS);
    }

    @Override
    public void newAnimation() {
        customerCords.clear();
        customerDestination.clear();
    }

    public int[] getLocation(int id) {
        if (id == entranceID) {
            return entrance;
        } else if (id == restaurantID) {
            return restaurant;
        } else if (id == exitID) {
            return exit;
        } else if (id < 0) {
            return getTicketBooth(Math.abs(id));
        } else {
            return getRide(id);
        }
    }

    @Override
    public void addCustomerToAnimation(int from, int to) {
        // extract details for this
        double x = getLocation(from)[0];
        double y = getLocation(from)[1];
        customerCords.add(new double[]{x, y});
        customerDestination.add(getLocation(to));
    }

    public void stopAnimation() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    public void renderCustomers() {
        if (step >= getAnimationSteps()) {
            stopAnimation();
        }
        Platform.runLater(() -> {
            customerCtx.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT); // Clear canvas
            for (int i = 0; i < customerCords.size(); i++) {
                customerCtx.setFill(Color.BLUE);
                customerCtx.fillOval(getCustomerCords(i)[0], getCustomerCords(i)[1], CUSTOMER_SIZE, CUSTOMER_SIZE); // Draw moving circle
            }
        });


        // update cords
        for (int i = 0; i < customerCords.size(); i++) {
            double[] nextStep = calculatePath(customerCords.get(i), customerDestination.get(i), getAnimationSteps());
            customerCords.get(i)[0] += nextStep[0];
            customerCords.get(i)[1] += nextStep[1];
        }
        step++;
    }

    public int[] getCustomerCords(int id) {
        return new int[]{(int) customerCords.get(id)[0], (int) customerCords.get(id)[1]};
    }

    public double[] calculatePath(double[] origin, int[] destination, int steps) {
        int midX = destination[0] - CUSTOMER_SIZE / 2;
        int midY = destination[1] - CUSTOMER_SIZE / 2;
        // Calculate delta x and y
        double dx = midX - origin[0];
        double dy = midY - origin[1];
        double x = dx / steps;
        double y = dy / steps;
        return new double[]{x, y};
    }

    public int getAnimationSteps() {
        return (int) (simDelayValue / UPDATE_RATE_MS);
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

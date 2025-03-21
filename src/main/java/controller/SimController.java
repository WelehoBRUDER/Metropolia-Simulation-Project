package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import simu.framework.Clock;
import simu.framework.IEngine;
import simu.model.OwnEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.*;

/**
 * SimController is a class that controls the simulation screen.
 */
public class SimController implements ISettingsControllerForM {
    private IEngine engine;
    private SettingsController settingsController;

    private int simTimeValue = 0;
    private double arrivalIntervalValue = 0;
    private int ticketBoothCountValue = 0;
    private int rideCountValue = 0;
    private int restaurantCapValue = 0;
    private long simDelayValue = 0;
    private double wristbandChanceValue = 0;
    private ArrayList<int[]> rideProperties = new ArrayList<>();
    private int servicePoints = 5;

    private int entranceID = -100;
    private int restaurantID = 100;
    private int exitID = -101;

    private boolean running = false;

    // UI Elements
    @FXML
    private Label time;
    @FXML
    private Button simPause;
    @FXML
    private Label currentDelay;
    @FXML
    private Label wristbandChanceLabel;
    @FXML
    private TextArea simulationConsole;

    // Canvas elements
    @FXML
    private Canvas servicePointCanvas;
    @FXML
    private Canvas customerCanvas;
    private GraphicsContext serviceCtx;
    private GraphicsContext customerCtx;

    // Customer numbers
    private ArrayList<ArrayList<Integer>> customerNumbers = new ArrayList<>();
    private int[] defaults = new int[]{0, -1, -1, -20, 0};

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
    private final Color TICKET_COLOR = Color.LIGHTBLUE;
    private final Color RIDE_COLOR = Color.ORANGE;
    private final Color RESTAURANT_COLOR = Color.RED;
    private final Color EXIT_COLOR = Color.PURPLE;

    // Animation executor
    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> future;

    // Animation parameters
    private ArrayList<double[]> customerCords = new ArrayList<>();
    private ArrayList<int[]> customerDestination = new ArrayList<>();
    private int step = 0;

    /**
     * Creates a circle of elements.
     *
     * @param area   The area of the circle.
     * @param size   The size of the elements.
     * @param amount The amount of elements.
     * @return The coordinates for the elements.
     */
    public int[][] circleOfElements(int area, int size, int amount) {
        int[][] coordinates = new int[amount][2];
        for (int i = 1; i <= amount; i++) {
            coordinates[i - 1][0] = (int) ((area - (0.5 * size)) / area * (area * Math.cos(2 * Math.PI * i / amount)));
            coordinates[i - 1][1] = (int) ((area - (0.5 * size)) / area * (area * Math.sin(2 * Math.PI * i / amount)));
        }
        return coordinates;
    }

    /**
     * Sets all parameters for the simulation.
     *
     * @param simTime              The total time the simulation should run for.
     * @param arrivalIntervalValue The arrival interval for customers (in simulation time).
     * @param ticketBoothCount     The number of ticket booths.
     * @param rideCount            The number of rides.
     * @param restaurantCap        The capacity of the restaurant.
     * @param simDelay             The delay between each event (in ms).
     * @param wristbandChance      The chance for a wristband on each new customer.
     * @param rideProperties       The properties for the rides (mean and variance).
     * @param settingsController   The settings controller for the simulation.
     */
    public void setSimulationParameters(int simTime, double arrivalIntervalValue, int ticketBoothCount, int rideCount, int restaurantCap, long simDelay, double wristbandChance, ArrayList<int[]> rideProperties, SettingsController settingsController) {
        this.settingsController = settingsController;
        this.simTimeValue = simTime;
        this.arrivalIntervalValue = arrivalIntervalValue;
        this.ticketBoothCountValue = ticketBoothCount;
        this.rideCountValue = rideCount;
        this.restaurantCapValue = restaurantCap;
        this.simDelayValue = simDelay;
        this.wristbandChanceValue = wristbandChance;
        this.rideProperties = rideProperties;
        this.serviceCtx = servicePointCanvas.getGraphicsContext2D();
        this.customerCtx = customerCanvas.getGraphicsContext2D();
        this.customerNumbers.clear();
        simulationConsole.appendText("Starting simulation...");
        this.currentDelay.setText("Current delay: " + this.simDelayValue + "ms");
        this.wristbandChanceLabel.setText(String.format("%.1f", this.wristbandChanceValue) + "%");
        this.defaults[3] = this.restaurantCapValue * -1;
        for (int i = 0; i < this.servicePoints; i++) {
            this.customerNumbers.add(new ArrayList<>());
        }
    }

    /**
     * Starts the simulation with current parameters.
     */
    public void startSim() {
        clearScreen();
        drawAllServicePoints();
        System.out.println("Starting simulation");
        this.engine = new OwnEngine(this, this.arrivalIntervalValue, this.rideCountValue, this.ticketBoothCountValue, this.rideProperties, this.restaurantCapValue, this.wristbandChanceValue); // luodaan uusi moottorisäie jokaista simulointia varten
        this.engine.setSimulationTime(this.simTimeValue);
        this.engine.setDelay(this.simDelayValue);
        ((Thread) this.engine).start();
        running = true;
    }

    /**
     * Stops the simulation.
     */
    public void stopSim() {
        stopAnimation();
        ((Thread) this.engine).stop();
        ((Thread) this.engine).interrupt();
    }

    /**
     * Toggles the simulation running state.
     */
    public void toggleSimRunning() {
        if (this.running) {
            pauseSim();

        } else {
            resumeSim();

        }
    }

    /**
     * Pauses the simulation. Uses the suspend method which is marked for removal.
     */
    public void pauseSim() {
        stopAnimation();
        running = false;
        changePauseButtonText("▶");
        try {
            ((Thread) this.engine).suspend();
        } catch (Exception e) {
            System.out.println("Can't pause simulation");
        }
    }

    /**
     * Resumes the simulation. Uses the resume method which is marked for removal.
     */
    public void resumeSim() {
        running = true;
        changePauseButtonText(" || ");
        try {
            ((Thread) this.engine).resume();
        } catch (Exception e) {
            System.out.println("Can't resume simulation");
        }
    }

    /**
     * Changes the text on the pause button.
     *
     * @param text The text to change to.
     */
    public void changePauseButtonText(String text) {
        this.simPause.setText(text);
    }

    /**
     * Calculates the center x coordinate for a point.
     *
     * @param width  The width of the area the point is in.
     * @param offset The offset for the point (its size).
     * @return The center x coordinate.
     */
    public int calcCenterX(int width, int offset) {
        return width / 2 - offset / 2;
    }


    /**
     * Calculates the center y coordinate for a point.
     *
     * @param offset The offset for the point (its size).
     * @return The center y coordinate on the canvas.
     */
    public int calcCenterY(int offset) {
        return this.CANVAS_HEIGHT / 2 - offset / 2;
    }

    /**
     * Clears the screen of all drawings.
     */
    public void clearScreen() {
        this.serviceCtx.clearRect(0, 0, this.servicePointCanvas.getWidth(), this.servicePointCanvas.getHeight());
        this.customerCtx.clearRect(0, 0, this.customerCanvas.getWidth(), this.customerCanvas.getHeight());
    }

    /**
     * Draws a service point on the canvas.
     *
     * @param x      The x coordinate for the service point.
     * @param y      The y coordinate for the service point.
     * @param color  The color for the service point.
     * @param number The number of customers at the service point.
     */
    public void drawServicePoint(int x, int y, Color color, int number) {
        this.serviceCtx.setFill(color);
        this.serviceCtx.fillRect(x, y, this.SERVICE_POINT_SIZE, this.SERVICE_POINT_SIZE);
        Platform.runLater(() -> drawServicePointNumber(x, y, number, 0));
    }

    /**
     * Draws the number of customers at a service point.
     *
     * @param x            The x coordinate for the service point.
     * @param y            The y coordinate for the service point.
     * @param number       The number of customers at the service point.
     * @param defaultValue The default value for the service point (essentially its capacity in reverse, like -20).
     */
    public void drawServicePointNumber(int x, int y, int number, int defaultValue) {
        if (serviceCtx == null) {
            return;
        }
        this.serviceCtx.setFont(new Font("Arial", this.FONT_SIZE));
        this.serviceCtx.setTextAlign(TextAlignment.CENTER);
        if (defaultValue == 0 && number > 0) {
            if (x == exit[0] && y == exit[1]) {
                this.serviceCtx.setFill(Color.WHITE);
                this.serviceCtx.clearRect(x - 10, y - this.FONT_SIZE - 4, this.SERVICE_POINT_SIZE * 1.3 + 10, this.FONT_SIZE + 4);
                this.serviceCtx.fillText(String.valueOf(number), x + calcCenterX(this.SERVICE_POINT_SIZE, 0), y - (double) this.FONT_SIZE / 2);
                return;
            } else {
                this.serviceCtx.setFill(Color.BLACK);
                this.serviceCtx.clearRect(x - 10, y - this.FONT_SIZE - 4, this.SERVICE_POINT_SIZE * 1.3 + 10, this.FONT_SIZE + 4);
                this.serviceCtx.fillText(String.valueOf(number), x + calcCenterX(this.SERVICE_POINT_SIZE, 0), y + (this.SERVICE_POINT_SIZE / 1.5));
                return;
            }
        } else if (defaultValue == 0 && number <= 0) {
            return;
        }
        this.serviceCtx.setFill(Color.WHITE);
        int beingServed = Math.min(number + Math.abs(defaultValue), Math.abs(defaultValue));
        int waiting = Math.max(number, 0);
        this.serviceCtx.clearRect(x - 10, y - this.FONT_SIZE - 4, this.SERVICE_POINT_SIZE * 1.3 + 10, this.FONT_SIZE + 4);
        this.serviceCtx.fillText(waiting + "(" + beingServed + ")", x + calcCenterX(this.SERVICE_POINT_SIZE, 0), y - (double) this.FONT_SIZE / 2);
    }

    /**
     * Draws an element on the info panel.
     */
    public void drawInfoPanelElement(Color color, int elementNumber, String elementText) {
        this.serviceCtx.setFill(color);
        this.serviceCtx.fillRect(this.CANVAS_WIDTH - (120), 4 + (4 + this.SERVICE_POINT_SIZE) * (elementNumber-1), this.SERVICE_POINT_SIZE, this.SERVICE_POINT_SIZE);
        this.serviceCtx.setFill(Color.WHITE);
        this.serviceCtx.fillText(elementText, 8 + this.SERVICE_POINT_SIZE + this.CANVAS_WIDTH - (120), 4 + (4 + this.SERVICE_POINT_SIZE) * (elementNumber-1) + (this.SERVICE_POINT_SIZE / 1.5));

    }

    /**
     * Draws the info panel on the canvas, showing the color of each service point.
     */
    public void drawInfoPanel() {
        this.serviceCtx.setFill(Paint.valueOf("#323232"));
        this.serviceCtx.setFont(new Font("Arial", this.FONT_SIZE));
        this.serviceCtx.setTextAlign(TextAlignment.LEFT);
        this.serviceCtx.fillRect(this.CANVAS_WIDTH - (124), 0, 120, 4 + (4 + this.SERVICE_POINT_SIZE) * 5);

        drawInfoPanelElement(ENTRANCE_COLOR, 1, "Entrance");
        drawInfoPanelElement(TICKET_COLOR, 2, "Ticket booths");
        drawInfoPanelElement(RIDE_COLOR, 3, "Rides");
        drawInfoPanelElement(RESTAURANT_COLOR, 4, "Restaurant");
        drawInfoPanelElement(EXIT_COLOR, 5, "Exit");
    }

    /**
     * Sets the entrance for the simulation.
     *
     * @param x The x coordinate for the entrance.
     * @param y The y coordinate for the entrance.
     */
    public void setEntrance(int x, int y) {
        entrance = new int[]{x, y};
    }

    /**
     * Adds a ticket booth to the simulation.
     *
     * @param x The x coordinate for the ticket booth.
     * @param y The y coordinate for the ticket booth.
     */
    public void addToTickets(int x, int y) {
        tickets.add(new int[]{x, y});
    }

    /**
     * Adds a ride to the simulation.
     *
     * @param x The x coordinate for the ride.
     * @param y The y coordinate for the ride.
     */
    public void addToRides(int x, int y) {
        rides.add(new int[]{x, y});
    }

    /**
     * Sets the restaurant for the simulation.
     *
     * @param x The x coordinate for the restaurant.
     * @param y The y coordinate for the restaurant.
     */
    public void setRestaurant(int x, int y) {
        restaurant = new int[]{x, y};
    }

    /**
     * Sets the exit for the simulation.
     *
     * @param x The x coordinate for the exit.
     * @param y The y coordinate for the exit.
     */
    public void setExit(int x, int y) {
        exit = new int[]{x, y};
    }

    /**
     * Gets the ticket booth coordinates for a ticket booth.
     *
     * @param id The ticket booth id.
     * @return The coordinates for the ticket booth.
     */
    public int[] getTicketBooth(int id) {
        if (id >= this.ticketBoothCountValue) {
            return this.tickets.get(this.ticketBoothCountValue - 1);
        }
        return this.tickets.get(id);
    }

    /**
     * Gets the ride coordinates for a ride.
     *
     * @param id The ride id.
     * @return The coordinates for the ride.
     */
    public int[] getRide(int id) {
        if (id >= this.rideCountValue) {
            return this.rides.get(this.rideCountValue - 1);
        }
        return this.rides.get(id);
    }

    /**
     * Calculates the coordinates for all the service points.
     */
    public void calcServicePointCords() {
        this.rides.clear();
        this.tickets.clear();

        setEntrance(this.ENTRANCE_AREA_SIZE / 2, calcCenterY(this.SERVICE_POINT_SIZE));

        int area = this.ticketBoothCountValue * this.SERVICE_POINT_SIZE * 2;
        int yStart = calcCenterY(area);

        for (int i = 0; i < this.ticketBoothCountValue; i++) {
            int x = calcCenterX(this.TICKET_AREA_SIZE + this.ENTRANCE_AREA_SIZE, this.SERVICE_POINT_SIZE);
            addToTickets(x, yStart + (i * this.SERVICE_POINT_SIZE * 2));
        }

        int[][] cords = circleOfElements(this.RIDE_AREA_SIZE, this.SERVICE_POINT_SIZE, this.rideCountValue);
        int xOffset = this.TICKET_AREA_SIZE + this.ENTRANCE_AREA_SIZE + this.RIDE_AREA_SIZE;
        int yOffset = this.CANVAS_HEIGHT / 2 - this.RIDE_AREA_SIZE;
        for (int i = 0; i < this.rideCountValue; i++) {
            addToRides(xOffset + cords[i][0], cords[i][1] + this.RIDE_AREA_SIZE + yOffset);
        }

        setRestaurant(xOffset + calcCenterX(this.RESTAURANT_AREA_SIZE, this.SERVICE_POINT_SIZE) + this.RIDE_AREA_SIZE, calcCenterY(0));
        setExit(this.CANVAS_WIDTH - (int) (this.SERVICE_POINT_SIZE * 1.5), calcCenterY(0));
    }

    /**
     * Draws all service points on the canvas.
     */
    public void drawAllServicePoints() {
        calcServicePointCords();

        // Entrance
        drawServicePoint(this.entrance[0], this.entrance[1], this.ENTRANCE_COLOR, 0);

        // Ticket booths
        for (int i = 0; i < this.ticketBoothCountValue; i++) {
            int[] cords = getTicketBooth(i);
            drawServicePoint(cords[0], cords[1], this.TICKET_COLOR, i + 1);
        }
        for (int i = 0; i < this.rideCountValue; i++) {
            int[] cords = getRide(i);
            drawServicePoint(cords[0], cords[1], this.RIDE_COLOR, i + 1);
        }
        // Restaurant
        drawServicePoint(this.restaurant[0], this.restaurant[1], this.RESTAURANT_COLOR, 0);

        // Exit
        drawServicePoint(this.exit[0], this.exit[1], this.EXIT_COLOR, 0);

        // Info panel
        drawInfoPanel();
    }

    /**
     * Updates the simulation console with each event that has passed.
     *
     * @param msg The newest event message.
     */
    @Override
    public void updateConsole(String msg) {
        Platform.runLater(() -> simulationConsole.appendText("\n" + msg));
    }

    /**
     * Closes the simulation screen
     */
    public void closeSimulation() {
        settingsController.closeSimulation();
    }


    /**
     * Draws customers moving on the canvas.
     * Starts an interval that calls the renderCustomers method.
     */
    @Override
    public void moveCustomerAnimation() {
        stopAnimation();
        if (this.simDelayValue < this.UPDATE_RATE_MS * 2.5) {
            return;
        }
        this.customerCtx.clearRect(0, 0, this.CANVAS_WIDTH, this.CANVAS_HEIGHT);
        this.step = 0;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.future = this.executorService.scheduleAtFixedRate(
                this::renderCustomers, 0, this.UPDATE_RATE_MS, TimeUnit.MILLISECONDS
        );
    }

    /**
     * Clears customer cords and destinations.
     */
    @Override
    public void newAnimation() {
        this.customerCords.clear();
        this.customerDestination.clear();
    }

    /**
     * Gets the location of a service point.
     *
     * @param id The service point id.
     * @return The coordinates for the service point.
     */
    public int[] getLocation(int id) {
        if (id == this.entranceID) {
            return this.entrance;
        } else if (id == this.restaurantID) {
            return this.restaurant;
        } else if (id == this.exitID) {
            return this.exit;
        } else if (id < 0) {
            return getTicketBooth(Math.abs(id) - 1);
        } else {
            return getRide(id - 1);
        }
    }

    /**
     * Gets the index for a service point.
     *
     * @param id The service point id.
     * @return The index for the service point.
     */
    public int[] getIndex(int id) {
        if (id == this.entranceID) {
            return new int[]{0, 0};
        } else if (id == this.restaurantID) {
            return new int[]{3, 0};
        } else if (id == this.exitID) {
            return new int[]{4, 0};
        } else if (id < 0) {
            return new int[]{1, Math.abs(id)};
        } else {
            return new int[]{2, id};
        }
    }

    /**
     * Adds a customer to the animation. Stored in a list of cords and destinations.
     *
     * @param from The service point id the customer is coming from.
     * @param to   The service point id the customer is going to.
     */
    @Override
    public void addCustomerToAnimation(int from, int to) {
        changeCustomerNumber(from, -1);
        changeCustomerNumber(to, 1);
        // extract details for this
        double x = getLocation(from)[0];
        double y = getLocation(from)[1];
        int toX = getLocation(to)[0];
        int toY = getLocation(to)[1];
        int defaultFrom = this.defaults[getIndex(from)[0]];
        int defaultTo = this.defaults[getIndex(to)[0]];
        Platform.runLater(() -> {
            drawServicePointNumber((int) x, (int) y, this.customerNumbers.get(getIndex(from)[0]).get(getIndex(from)[1]), defaultFrom);
            drawServicePointNumber(toX, toY, this.customerNumbers.get(getIndex(to)[0]).get(getIndex(to)[1]), defaultTo);
        });
        if (this.simDelayValue < this.UPDATE_RATE_MS * 2.5) {
            if (!this.customerCords.isEmpty()) {
                this.customerCords.clear();
                this.customerDestination.clear();
            }
            return;
        }
        // s = step
        double sx = calculatePath(new double[]{x, y}, getLocation(to), getAnimationSteps())[0];
        double sy = calculatePath(new double[]{x, y}, getLocation(to), getAnimationSteps())[1];
        this.customerCords.add(new double[]{x + (this.SERVICE_POINT_SIZE / 2) - (this.CUSTOMER_SIZE / 2), y + (this.SERVICE_POINT_SIZE / 2) - (this.CUSTOMER_SIZE / 2), sx, sy});
        this.customerDestination.add(getLocation(to));
    }

    /**
     * Changes the customer number for a service point.
     *
     * @param id     The service point id.
     * @param amount The amount to change the customer number by.
     */
    public void changeCustomerNumber(int id, int amount) {
        int[] index = getIndex(id);
        int row = index[0];
        int col = index[1];
        int defaultValue = this.defaults[row];
        if (col >= this.customerNumbers.get(row).size()) {
            while (col >= this.customerNumbers.get(row).size()) {
                this.customerNumbers.get(row).add(defaultValue);
            }
        }
        this.customerNumbers.get(row).set(col, this.customerNumbers.get(row).get(col) + amount);
    }

    /**
     * Stops all animation execution, current and interval.
     */
    public void stopAnimation() {
        if (this.future != null) {
            this.future.cancel(true); // Cancel the scheduled task
        }
        if (this.executorService != null) {
            this.executorService.shutdownNow();
            try {
                if (!this.executorService.awaitTermination(this.simDelayValue, TimeUnit.MILLISECONDS)) {
                    this.executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                this.executorService.shutdownNow();
            }
        }
    }

    /**
     * Renders the customers moving from one service point to another.
     */
    public void renderCustomers() {
        if (this.step >= getAnimationSteps()) {
            stopAnimation();
            return;
        }

        Platform.runLater(() -> {
            this.customerCtx.clearRect(0, 0, this.CANVAS_WIDTH, this.CANVAS_HEIGHT); // Clear canvas
            for (int i = 0; i < customerCords.size(); i++) {
                this.customerCtx.setFill(Color.BLUE);
                this.customerCtx.fillOval(getCustomerCords(i)[0], getCustomerCords(i)[1], this.CUSTOMER_SIZE, this.CUSTOMER_SIZE); // Draw moving circle
            }
        });


        // update cords
        for (int i = 0; i < this.customerCords.size(); i++) {
            this.customerCords.get(i)[0] += this.customerCords.get(i)[2];
            this.customerCords.get(i)[1] += this.customerCords.get(i)[3];
        }
        this.step++;
    }

    /**
     * Gets the customer cords for a customer.
     *
     * @param id The customer id.
     * @return The cords for the customer.
     */
    public int[] getCustomerCords(int id) {
        if (this.customerCords.isEmpty()) {
            return new int[]{0, 0};
        }
        return new int[]{(int) this.customerCords.get(id)[0], (int) this.customerCords.get(id)[1]};
    }

    /**
     * Calculates the path for a customer to move from one service point to another.
     *
     * @param origin      The origin cords of the customer.
     * @param destination The destination cords of the customer.
     * @param steps       How many steps the customer needs to take in total, affects dx and dy.
     * @return The next coordinate point for the customer to move to.
     */
    public double[] calculatePath(double[] origin, int[] destination, int steps) {
        int midX = destination[0] + (this.SERVICE_POINT_SIZE / 4) - this.CUSTOMER_SIZE / 2;
        int midY = destination[1] + (this.SERVICE_POINT_SIZE / 4) - this.CUSTOMER_SIZE / 2;
        // Calculate delta x and y
        double dx = midX - origin[0];
        double dy = midY - origin[1];
        double x = dx / steps;
        double y = dy / steps;
        return new double[]{x, y};
    }

    /**
     * Gets the amount of steps for the animation.
     *
     * @return The amount of steps for the animation.
     */
    public int getAnimationSteps() {
        return (int) (this.simDelayValue / this.UPDATE_RATE_MS) + 1;
    }

    /**
     * Changes the delay for the simulation.
     *
     * @param e    The mouse event.
     * @param type The type of change (positive or negative).
     */
    public void changeDelay(MouseEvent e, long type) {
        if (e.isShiftDown() && !e.isControlDown()) {
            simDelayValue += 5 * type;
        } else if (e.isControlDown() && !e.isShiftDown()) {
            simDelayValue += 25 * type;
        } else if (e.isControlDown() && e.isShiftDown()) {
            simDelayValue += 100 * type;
        } else {
            simDelayValue += type;
        }
        if (this.simDelayValue < 1) {
            this.simDelayValue = 1;
        }
        this.engine.setDelay(this.simDelayValue);
        this.currentDelay.setText("Current delay: " + this.simDelayValue + "ms");
    }

    /**
     * Increases the delay for the simulation.
     *
     * @param e The mouse event.
     */
    public void increaseDelay(MouseEvent e) {
        changeDelay(e, 1);
    }

    /**
     * Decreases the delay for the simulation.
     *
     * @param e The mouse event.
     */
    public void decreaseDelay(MouseEvent e) {
        changeDelay(e, -1);
    }

    /**
     * Changes the wristband occurrence chance for the simulation.
     *
     * @param e    The mouse event.
     * @param type The type of change (positive or negative).
     */
    public void changeWristbandChance(MouseEvent e, int type) {
        if (e.isShiftDown() && !e.isControlDown()) {
            this.wristbandChanceValue += type;
        } else if (e.isControlDown()) {
            this.wristbandChanceValue += 5 * type;
        } else {
            this.wristbandChanceValue += 0.1 * type;
        }
        if (this.wristbandChanceValue < 0) {
            this.wristbandChanceValue = 0;
        } else if (this.wristbandChanceValue > 100) {
            this.wristbandChanceValue = 100;
        }
        this.engine.setWristbandChance(this.wristbandChanceValue);
        this.wristbandChanceLabel.setText(String.format("%.1f", this.wristbandChanceValue) + "%");
    }

    /**
     * Increases the wristband occurrence chance for the simulation.
     *
     * @param e The mouse event.
     */
    public void increaseWristbandChance(MouseEvent e) {
        changeWristbandChance(e, 1);
    }

    /**
     * Decreases the wristband occurrence chance for the simulation.
     *
     * @param e The mouse event.
     */
    public void decreaseWristbandChance(MouseEvent e) {
        changeWristbandChance(e, -1);
    }

    /**
     * Updates total time shown in the simulation screen
     *
     * @param time Current time on the internal clock
     */
    @Override
    public void updateEventTime(double time) {
        Platform.runLater(() -> {
            this.time.setText("Total time:\n" + String.format("%.2f", time) + " / " + this.simTimeValue);
        });
    }

    @Override
    public void showEndTime(double time) {

    }


    @Override
    public void visualizeResults(HashMap<String, Double> staticResults, TreeMap<String, Double> dynamicResults) {

    }
}

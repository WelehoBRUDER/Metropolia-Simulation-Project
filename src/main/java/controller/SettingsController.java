package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import org.checkerframework.checker.units.qual.A;
import simu.framework.IEngine;
import simu.model.OwnEngine;

import java.util.ArrayList;
import java.util.Locale;

public class SettingsController {
    @FXML
    private TextField simTime;
    @FXML
    private TextField arrivalInterval;
    @FXML
    private TextField ticketBoothCount;
    @FXML
    private TextField rideCount;
    @FXML
    private VBox rides;
    @FXML
    private TextField restaurantCap;
    @FXML
    private TextField simDelay;
    @FXML
    private TextField wristbandChance;

    private final boolean GUI_DEBUG = false;

    private int simTimeValue = 250;
    private double arrivalIntervalValue = 5.0;
    private int ticketBoothCountValue = 4;
    private int rideCountValue = 9;
    private int restaurantCapValue = 20;
    private long simDelayValue = 100;
    private double wristbandChanceValue = 30;
    private ArrayList<int[]> rideProperties = new ArrayList<>();

    private final int maxTicketBoothCount = 9;
    private final int maxRideCount = 25;
    private final int minDelay = 20;

    private Stage stage;

    /**
     * Initializes the settings controller.
     *
     * @throws Exception If the initialization fails.
     */
    public void initialize() throws Exception {
        // Add focusout listeners to each input field
        simTime.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                setSimTime();
            }
        });
        arrivalInterval.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                setArrivalInterval();
            }
        });
        ticketBoothCount.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                setTicketBoothCount();
            }
        });
        rideCount.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                setRideCount();
            }
        });
        restaurantCap.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                setRestaurantCap();
            }
        });
        simDelay.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                setSimDelay();
            }
        });
        wristbandChance.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                setWristbandChance();
            }
        });
        // Add sanitization listeners to each input field
        sanitizeInput(simTime);
        sanitizeInput(arrivalInterval);
        sanitizeInput(ticketBoothCount);
        sanitizeInput(rideCount);
        sanitizeInput(restaurantCap);
        sanitizeInput(simDelay);
        sanitizeInput(wristbandChance);
        // Set default values
        simTime.setText(String.valueOf(simTimeValue));
        arrivalInterval.setText(String.valueOf(arrivalIntervalValue));
        ticketBoothCount.setText(String.valueOf(ticketBoothCountValue));
        rideCount.setText(String.valueOf(rideCountValue));
        restaurantCap.setText(String.valueOf(restaurantCapValue));
        simDelay.setText(String.valueOf(simDelayValue));
        wristbandChance.setText(String.valueOf(wristbandChanceValue));
        setRideCount();
        if (GUI_DEBUG) {
            startSimulation();
        }
    }

    /**
     * Creates a number input field.
     *
     * @param index The index of the ride.
     * @param type  The type of the ride.
     * @return The number input field.
     */
    @FXML
    public HBox createNumberInput(int index, int type) {
        HBox numberInput = new HBox();
        numberInput.getStyleClass().add("num-setting-small");
        TextField number = new TextField();
        Button increment = new Button("+");
        Button decrement = new Button("-");
        numberInput.setMaxWidth(70);
        number.setText(String.valueOf(rideProperties.get(index)[type]));
        sanitizeInput(number);
        numberInput.getChildren().addAll(decrement, number, increment);
        number.setOnAction(e -> setRideParam(index, type, number));
        number.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) setRideParam(index, type, number);
        });
        decrement.setOnMouseClicked(e -> changeRideParam(index, type, e, number, -1));
        increment.setOnMouseClicked(e -> changeRideParam(index, type, e, number, 1));
        return numberInput;
    }

    /**
     * Changes the ride parameters.
     *
     * @param index The index of the ride.
     * @param type  The type of the ride.
     * @param e     The mouse event.
     * @param field The text field.
     * @param power The power of the change.
     */
    public void changeRideParam(int index, int type, MouseEvent e, TextField field, int power) {
        int value = power;
        if (e.isShiftDown() && !e.isControlDown()) {
            value = 5 * power;
        } else if (e.isControlDown()) {
            value = 25 * power;
        }
        rideProperties.get(index)[type] += value;
        if (rideProperties.get(index)[type] < 1) {
            rideProperties.get(index)[type] = 1;
        } else if (rideProperties.get(index)[type] > 100) {
            rideProperties.get(index)[type] = 100;
        }
        field.setText(String.valueOf(rideProperties.get(index)[type]));
    }

    /**
     * Sets the ride parameters.
     *
     * @param index The index of the ride.
     * @param type  The type of the ride.
     * @param field The text field.
     */
    public void setRideParam(int index, int type, TextField field) {
        if (!field.getText().isEmpty()) {
            if (field.getText().length() > 3) {
                field.setText(field.getText().substring(0, 3));
            }
            int value = Integer.parseInt(field.getText());
            if (value < 1 || value > 100) {
                if (value < 1) {
                    value = 1;
                } else {
                    value = 100;
                }
                field.setText(String.valueOf(value));
            }
            rideProperties.get(index)[type] = value;
        }
    }

    /**
     * Displays the rides.
     */
    @FXML
    public void displayRides() {
        rides.getChildren().clear();
        for (int i = 0; i < rideCountValue; i++) {
            HBox ride = new HBox(5);
            Label rideLabel = new Label("Ride " + (i + 1));
            Label varianceLabel = new Label("Variance:");
            Label meanLabel = new Label("Mean:");

            //FIXME: joku joka osaa niin pls korjaa tooltipit
            // en tajuu miten variance ja mean toimii lmao
            Tooltip varianceTip = new Tooltip("Variance of the ride time");
            Tooltip meanTip = new Tooltip("Mean of the ride time");

            Tooltip.install(varianceLabel, varianceTip);
            Tooltip.install(meanLabel, meanTip);
            rideLabel.getStyleClass().add("ride-label");
            HBox variance = createNumberInput(i, 0);
            HBox mean = createNumberInput(i, 1);
            ride.setId("ride" + i);
            ride.getStyleClass().add("ride");
            ride.setPrefHeight(25);
            ride.setMinHeight(25);
            ride.getChildren().addAll(rideLabel, meanLabel, mean, varianceLabel, variance);
            rides.getChildren().add(ride);
        }
    }

    /**
     * Changes the ride properties.
     */
    public void changeRideProperties() {
        if (rideProperties.size() < rideCountValue) {
            for (int i = rideProperties.size(); i < rideCountValue; i++) {
                rideProperties.add(new int[]{2, 5});
            }
        } else if (rideProperties.size() > rideCountValue) {
            for (int i = rideProperties.size(); i > rideCountValue; i--) {
                rideProperties.remove(i - 1);
            }
        }
    }

    /**
     * Sanitizes the input of a text field (removes all non-numeric characters).
     *
     * @param field The text field to sanitize.
     */
    public void sanitizeInput(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                field.setText(newValue.replaceAll("[^\\d.]", ""));
            }
        });
    }

    /**
     * Increments the simulation time.
     *
     * @param e The mouse event.
     */
    public void incrementSimTime(MouseEvent e) {
        if (e.isShiftDown() && !e.isControlDown()) {
            simTimeValue += 5;
        } else if (e.isControlDown() && !e.isShiftDown()) {
            simTimeValue += 25;
        } else if (e.isControlDown() && e.isShiftDown()) {
            simTimeValue += 100;
        } else {
            simTimeValue++;
        }
        simTime.setText(String.valueOf(simTimeValue));
        setSimTime();
    }

    /**
     * Decrements the simulation time.
     *
     * @param e The mouse event.
     */
    public void decrementSimTime(MouseEvent e) {
        if (e.isShiftDown() && !e.isControlDown()) {
            simTimeValue -= 5;
        } else if (e.isControlDown() && !e.isShiftDown()) {
            simTimeValue -= 25;
        } else if (e.isControlDown() && e.isShiftDown()) {
            simTimeValue -= 100;
        } else {
            simTimeValue--;
        }
        simTime.setText(String.valueOf(simTimeValue));
        setSimTime();
    }

    /**
     * Increments the ticket booth count.
     *
     * @param e The mouse event.
     */
    public void incrementTicketBooths(MouseEvent e) {
        if (e.isShiftDown()) {
            ticketBoothCountValue += 5;
        } else {
            ticketBoothCountValue++;
        }
        ticketBoothCount.setText(String.valueOf(ticketBoothCountValue));
        setTicketBoothCount();
    }

    /**
     * Decrements the ticket booth count.
     *
     * @param e The mouse event.
     */
    public void decrementTicketBooths(MouseEvent e) {
        if (e.isShiftDown()) {
            ticketBoothCountValue -= 5;
        } else {
            ticketBoothCountValue--;
        }
        ticketBoothCount.setText(String.valueOf(ticketBoothCountValue));
        setTicketBoothCount();
    }

    /**
     * Increments the ride count.
     *
     * @param e The mouse event.
     */
    public void incrementRideCount(MouseEvent e) {
        if (e.isShiftDown()) {
            rideCountValue += 5;
        } else {
            rideCountValue++;
        }
        rideCount.setText(String.valueOf(rideCountValue));
        setRideCount();
    }

    /**
     * Decrements the ride count.
     *
     * @param e The mouse event.
     */
    public void decrementRideCount(MouseEvent e) {
        if (e.isShiftDown()) {
            rideCountValue -= 5;
        } else {
            rideCountValue--;
        }
        rideCount.setText(String.valueOf(rideCountValue));
        setRideCount();
    }

    /**
     * Increments the restaurant capacity.
     *
     * @param e The mouse event.
     */
    public void incrementRestaurantCap(MouseEvent e) {
        if (e.isShiftDown() && !e.isControlDown()) {
            restaurantCapValue += 5;
        } else if (e.isControlDown() && !e.isShiftDown()) {
            restaurantCapValue += 25;
        } else if (e.isControlDown() && e.isShiftDown()) {
            restaurantCapValue += 100;
        } else {
            restaurantCapValue++;
        }
        restaurantCap.setText(String.valueOf(restaurantCapValue));
        setRestaurantCap();
    }

    /**
     * Decrements the restaurant capacity.
     *
     * @param e The mouse event.
     */
    public void decrementRestaurantCap(MouseEvent e) {
        if (e.isShiftDown() && !e.isControlDown()) {
            restaurantCapValue -= 5;
        } else if (e.isControlDown() && !e.isShiftDown()) {
            restaurantCapValue -= 25;
        } else if (e.isControlDown() && e.isShiftDown()) {
            restaurantCapValue -= 100;
        } else {
            restaurantCapValue--;
        }
        restaurantCap.setText(String.valueOf(restaurantCapValue));
        setRestaurantCap();
    }

    /**
     * Increments the simulation delay.
     *
     * @param e The mouse event.
     */
    public void incrementSimDelay(MouseEvent e) {
        if (e.isShiftDown() && !e.isControlDown()) {
            simDelayValue += 5;
        } else if (e.isControlDown() && !e.isShiftDown()) {
            simDelayValue += 25;
        } else if (e.isControlDown() && e.isShiftDown()) {
            simDelayValue += 100;
        } else {
            simDelayValue++;
        }
        simDelay.setText(String.valueOf(simDelayValue));
        setSimDelay();
    }

    /**
     * Decrements the simulation delay.
     *
     * @param e The mouse event.
     */
    public void decrementSimDelay(MouseEvent e) {
        if (e.isShiftDown() && !e.isControlDown()) {
            simDelayValue -= 5;
        } else if (e.isControlDown() && !e.isShiftDown()) {
            simDelayValue -= 25;
        } else if (e.isControlDown() && e.isShiftDown()) {
            simDelayValue -= 100;
        } else {
            simDelayValue--;
        }
        simDelay.setText(String.valueOf(simDelayValue));
        setSimDelay();
    }

    /**
     * Increments the wristband chance.
     *
     * @param e The mouse event.
     */
    public void incrementWristbandChance(MouseEvent e) {
        if (e.isShiftDown() && !e.isControlDown()) {
            wristbandChanceValue++;
        } else if (e.isControlDown()) {
            wristbandChanceValue += 5;
        } else {
            wristbandChanceValue += 0.1;
        }
        wristbandChance.setText(String.valueOf(wristbandChanceValue));
        setWristbandChance();
    }

    /**
     * Decrements the wristband chance.
     *
     * @param e The mouse event.
     */
    public void decrementWristbandChance(MouseEvent e) {
        if (e.isShiftDown() && !e.isControlDown()) {
            wristbandChanceValue--;
        } else if (e.isControlDown()) {
            wristbandChanceValue -= 5;
        } else {
            wristbandChanceValue -= 0.1;
        }
        wristbandChance.setText(String.valueOf(wristbandChanceValue));
        setWristbandChance();
    }

    /**
     * Sets the simulation time.
     */
    public void setSimTime() {
        if (!simTime.getText().isEmpty()) {
            if (simTime.getText().length() > 10) {
                simTime.setText(simTime.getText().substring(0, 9));
            }
            simTimeValue = Integer.parseInt(simTime.getText());
            if (simTimeValue < minDelay) {
                simTimeValue = minDelay;
                simTime.setText(String.valueOf(simTimeValue));
            } else if (simTimeValue > 10000) {
                simTimeValue = 10000;
                simTime.setText("10000");
            }
        }
    }

    /**
     * Sets the arrival interval.
     */
    public void setArrivalInterval() {
        if (!arrivalInterval.getText().isEmpty()) {
            if (arrivalInterval.getText().length() > 10) {
                arrivalInterval.setText(arrivalInterval.getText().substring(0, 9));
            }
            arrivalIntervalValue = Double.parseDouble(arrivalInterval.getText());
            if (arrivalIntervalValue < 0) {
                arrivalIntervalValue = 0.01;
                arrivalInterval.setText(String.valueOf(arrivalIntervalValue));
            } else if (arrivalIntervalValue > 1000) {
                arrivalIntervalValue = 1000;
                arrivalInterval.setText("1000");
            }
            arrivalInterval.setText(String.format(Locale.US, "%.2f", arrivalIntervalValue));
        }
    }

    /**
     * Increments or decrements the arrival interval.
     *
     * @param e     The mouse event.
     * @param value The value to increment or decrement by.
     */
    public void incrementDecrementArrivalInterval(MouseEvent e, double value) {
        if (e.isShiftDown() && !e.isControlDown()) {
            arrivalIntervalValue += value * 5;
        } else if (e.isControlDown() && !e.isShiftDown()) {
            arrivalIntervalValue += value * 25;
        } else if (e.isControlDown() && e.isShiftDown()) {
            arrivalIntervalValue += value * 100;
        } else {
            arrivalIntervalValue += value;
        }
        if (arrivalIntervalValue < 0) {
            arrivalIntervalValue = 0.01;
        } else if (arrivalIntervalValue > 1000) {
            arrivalIntervalValue = 1000;
        }
        arrivalInterval.setText(String.valueOf(arrivalIntervalValue));
        setArrivalInterval();
    }

    /**
     * Increments the arrival interval.
     *
     * @param e The mouse event.
     */
    public void incrementArrivalInterval(MouseEvent e) {
        incrementDecrementArrivalInterval(e, 0.1);
    }

    /**
     * Decrements the arrival interval.
     *
     * @param e The mouse event.
     */
    public void decrementArrivalInterval(MouseEvent e) {
        incrementDecrementArrivalInterval(e, -0.1);
    }

    /**
     * Sets the ticket booth count based on the input.
     */
    public void setTicketBoothCount() {
        if (!ticketBoothCount.getText().isEmpty()) {
            if (ticketBoothCount.getText().length() > 3) {
                ticketBoothCount.setText(ticketBoothCount.getText().substring(0, 3));
            }
            ticketBoothCountValue = Integer.parseInt(ticketBoothCount.getText());
            if (ticketBoothCountValue < 1 || ticketBoothCountValue > maxTicketBoothCount) {
                if (ticketBoothCountValue < 1) {
                    ticketBoothCountValue = 1;
                } else {
                    ticketBoothCountValue = maxTicketBoothCount;
                }
                ticketBoothCount.setText(String.valueOf(ticketBoothCountValue));
            }
        }
    }

    /**
     * Sets the ride count based on the input.
     */
    public void setRideCount() {
        if (!rideCount.getText().isEmpty()) {
            if (rideCount.getText().length() > 3) {
                rideCount.setText(rideCount.getText().substring(0, 3));
            }
            rideCountValue = Integer.parseInt(rideCount.getText());
            if (rideCountValue < 1 || rideCountValue > maxRideCount) {
                if (rideCountValue < 1) {
                    rideCountValue = 1;
                } else {
                    rideCountValue = maxRideCount;
                }
                rideCount.setText(String.valueOf(rideCountValue));
            }
            changeRideProperties();
            displayRides();
        }
    }

    /**
     * Sets the restaurant capacity based on the input.
     */
    public void setRestaurantCap() {
        if (!restaurantCap.getText().isEmpty()) {
            if (restaurantCap.getText().length() > 3) {
                restaurantCap.setText(restaurantCap.getText().substring(0, 3));
            }
            restaurantCapValue = Integer.parseInt(restaurantCap.getText());
            if (restaurantCapValue < 1) {
                restaurantCapValue = 1;
                restaurantCap.setText("1");
            }
        }
    }

    /**
     * Sets the simulation delay based on the input.
     */
    public void setSimDelay() {
        if (!simDelay.getText().isEmpty()) {
            if (simDelay.getText().length() > 5) {
                simDelay.setText(simDelay.getText().substring(0, 5));
            }
            simDelayValue = Long.parseLong(simDelay.getText());
            if (simDelayValue < 0) {
                simDelayValue = 0;
                simDelay.setText("0");
            }
        }
    }

    /**
     * Sets the wristband chance based on the input.
     */
    public void setWristbandChance() {
        if (!wristbandChance.getText().isEmpty()) {
            if (wristbandChance.getText().length() > 5) {
                wristbandChance.setText(wristbandChance.getText().substring(0, 5));
            }
            wristbandChanceValue = Double.parseDouble(wristbandChance.getText());
            if (wristbandChanceValue < 1 || wristbandChanceValue > 100) {
                if (wristbandChanceValue < 1) {
                    wristbandChanceValue = 1;
                } else {
                    wristbandChanceValue = 100;
                }
                wristbandChance.setText(String.valueOf(wristbandChanceValue));
            }
        }
    }


    /**
     * Starts the simulation.
     * The simulation is displayed in a new window.
     *
     * @throws Exception If the simulation fails to start.
     */
    public void startSimulation() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/simulation.fxml"));
        Parent root = loader.load();
        SimController simController = loader.getController();

        System.out.println(simController);

        simController.setSimulationParameters(simTimeValue, arrivalIntervalValue, ticketBoothCountValue, rideCountValue, restaurantCapValue, simDelayValue, wristbandChanceValue, rideProperties, this);

        // Show new stage
        stage = new Stage();
        stage.setTitle("Simulation running...");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();

        stage.setOnHidden(e -> {
            try {
                simController.stopSim();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        simController.startSim();
    }

    /**
     * Shows the simulation history.
     *
     * @throws Exception If the history fails to load.
     */
    public void viewHistory() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/history.fxml"));
        Parent root = loader.load();
        HistoryController historyController = loader.getController();

        // Show new stage
        stage = new Stage();
        stage.setTitle("Simulation history");
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Closes the simulation.
     */
    public void closeSimulation() {
        Platform.runLater(() -> {
            stage.close();
        });
    }

}

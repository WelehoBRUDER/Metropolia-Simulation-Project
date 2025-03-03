package controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsController {
    @FXML
    private TextField simTime;
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

    private int simTimeValue = 0;
    private int rideCountValue = 1;
    private int restaurantCapValue = 0;
    private double simDelayValue = 0;
    private int wristbandChanceValue = 0;
    private ArrayList<int[]> rideProperties = new ArrayList<>();

    private final int maxRideCount = 25;

    public void initialize() {
        sanitizeInput(simTime);
        sanitizeInput(rideCount);
        sanitizeInput(restaurantCap);
        sanitizeInput(simDelay);
        sanitizeInput(wristbandChance);
        rideCount.setText(String.valueOf(rideCountValue));
        setRideCount();
    }

    @FXML
    public FlowPane createNumberInput() {
        FlowPane numberInput = new FlowPane();
        numberInput.getStyleClass().add("num-setting-small");
        TextField number = new TextField();
        Button increment = new Button("+");
        Button decrement = new Button("-");
        sanitizeInput(number);
        numberInput.getChildren().addAll(decrement, number, increment);
        return numberInput;
    }

    @FXML
    public void displayRides() {
        rides.getChildren().clear();
        for (int i = 0; i < rideCountValue; i++) {
            FlowPane ride = new FlowPane();
            Label rideLabel = new Label("Ride " + (i + 1));
            Label varianceLabel = new Label("Variance:");
            Label meanLabel = new Label("Mean:");
            rideLabel.getStyleClass().add("ride-label");
            FlowPane variance = createNumberInput();
            FlowPane mean = createNumberInput();
            ride.setId("ride" + i);
            ride.setPrefHeight(40);
            ride.setMinHeight(40);
            ride.hgapProperty().setValue(5);
            ride.alignmentProperty().setValue(javafx.geometry.Pos.CENTER);
            ride.setRowValignment(javafx.geometry.VPos.CENTER);
            ride.getChildren().addAll(rideLabel, varianceLabel, variance, meanLabel, mean);
            rides.getChildren().add(ride);
        }
    }

    public void sanitizeInput(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                field.setText(newValue.replaceAll("[^\\d.]", ""));
            }
        });
    }

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

    public void incrementRideCount(MouseEvent e) {
        if (e.isShiftDown()) {
            rideCountValue += 5;
        } else {
            rideCountValue++;
        }
        rideCount.setText(String.valueOf(rideCountValue));
        setRideCount();
    }

    public void decrementRideCount(MouseEvent e) {
        if (e.isShiftDown()) {
            rideCountValue -= 5;
        } else {
            rideCountValue--;
        }
        rideCount.setText(String.valueOf(rideCountValue));
        setRideCount();
    }

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

    public void incrementWristbandChance(MouseEvent e) {
        if (e.isShiftDown() && !e.isControlDown()) {
            wristbandChanceValue += 5;
        } else if (e.isControlDown()) {
            wristbandChanceValue += 25;
        } else {
            wristbandChanceValue++;
        }
        wristbandChance.setText(String.valueOf(wristbandChanceValue));
        setWristbandChance();
    }

    public void decrementWristbandChance(MouseEvent e) {
        if (e.isShiftDown() && !e.isControlDown()) {
            wristbandChanceValue -= 5;
        } else if (e.isControlDown()) {
            wristbandChanceValue -= 25;
        } else {
            wristbandChanceValue--;
        }
        wristbandChance.setText(String.valueOf(wristbandChanceValue));
        setWristbandChance();
    }

    public void setSimTime() {
        if (!simTime.getText().isEmpty()) {
            simTimeValue = Integer.parseInt(simTime.getText());
            if (simTimeValue < 0) {
                simTimeValue = 0;
                simTime.setText("0");
            }
        }
    }

    public void setRideCount() {
        if (!rideCount.getText().isEmpty()) {
            rideCountValue = Integer.parseInt(rideCount.getText());
            if (rideCountValue < 1 || rideCountValue > maxRideCount) {
                if (rideCountValue < 1) {
                    rideCountValue = 1;
                } else if (rideCountValue > maxRideCount) {
                    rideCountValue = maxRideCount;
                }
                rideCount.setText(String.valueOf(rideCountValue));
            }
            displayRides();
        }
    }

    public void setRestaurantCap() {
        if (!restaurantCap.getText().isEmpty()) {
            restaurantCapValue = Integer.parseInt(restaurantCap.getText());
            if (restaurantCapValue < 0) {
                restaurantCapValue = 0;
                restaurantCap.setText("0");
            }
        }
    }

    public void setSimDelay() {
        if (!simDelay.getText().isEmpty()) {
            simDelayValue = Double.parseDouble(simDelay.getText());
            if (simDelayValue < 0) {
                simDelayValue = 0;
                simDelay.setText("0");
            }
        }
    }

    public void setWristbandChance() {
        if (!wristbandChance.getText().isEmpty()) {
            wristbandChanceValue = Integer.parseInt(wristbandChance.getText());
            if (wristbandChanceValue < 1 || wristbandChanceValue > 100) {
                wristbandChance.setText(String.valueOf(wristbandChanceValue));
                if (wristbandChanceValue < 1) {
                    wristbandChanceValue = 1;
                } else if (wristbandChanceValue > 100) {
                    wristbandChanceValue = 100;
                }
            }
        }
    }


    public void startSimulation() {

    }


    public int getRideCount() {
        return rideCountValue;
    }


    public void setWristbandChance(double amount) {

    }


    public double getWristbandChance() {
        return wristbandChanceValue;
    }
}

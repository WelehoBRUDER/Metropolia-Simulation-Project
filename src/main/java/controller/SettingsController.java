package controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
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
    private ArrayList<Integer> rideCapacities = new ArrayList<Integer>();

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
    public void displayRides() {
        rides.getChildren().clear();
        for (int i = 0; i < rideCountValue; i++) {
            TextField ride = new TextField();
            ride.setPromptText("Ride " + (i + 1));
            ride.setId("ride" + i);
            ride.setPrefWidth(100);
            ride.setPrefHeight(25);
            ride.setStyle("-fx-font-size: 12px;");
            ride.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    ride.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
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
        simTimeValue = Integer.parseInt(simTime.getText());
    }

    public void setRideCount() {
        rideCountValue = Integer.parseInt(rideCount.getText());
        if (rideCountValue < 1) {
            rideCountValue = 1;
        } else if (rideCountValue > maxRideCount) {
            rideCountValue = maxRideCount;
        }
        rideCount.setText(String.valueOf(rideCountValue));
        displayRides();
    }

    public void setRestaurantCap() {
        restaurantCapValue = Integer.parseInt(restaurantCap.getText());
    }

    public void setSimDelay() {
        simDelayValue = Double.parseDouble(simDelay.getText());
    }

    public void setWristbandChance() {
        wristbandChanceValue = Integer.parseInt(wristbandChance.getText());
        if (wristbandChanceValue < 1) {
            wristbandChanceValue = 1;
        } else if (wristbandChanceValue > 100) {
            wristbandChanceValue = 100;
        }
        wristbandChance.setText(String.valueOf(wristbandChanceValue));
    }


    public void startSimulation() {

    }


    public int getRideCount() {
        return 0;
    }


    public void setWristbandChance(double amount) {

    }


    public double getWristbandChance() {
        return 0;
    }
}

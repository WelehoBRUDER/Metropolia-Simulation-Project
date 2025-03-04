package controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import simu.framework.IEngine;
import simu.model.OwnEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsController {
    @FXML
    private TextField simTime;
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

    private int simTimeValue = 250;
    private int ticketBoothCountValue = 1;
    private int rideCountValue = 3;
    private int restaurantCapValue = 20;
    private long simDelayValue = 50;
    private int wristbandChanceValue = 30;
    private ArrayList<int[]> rideProperties = new ArrayList<>();

    private IEngine engine;

    private final int maxTicketBoothCount = 18;
    private final int maxRideCount = 25;

    public void initialize() {
        sanitizeInput(simTime);
        sanitizeInput(ticketBoothCount);
        sanitizeInput(rideCount);
        sanitizeInput(restaurantCap);
        sanitizeInput(simDelay);
        sanitizeInput(wristbandChance);
        simTime.setText(String.valueOf(simTimeValue));
        ticketBoothCount.setText(String.valueOf(ticketBoothCountValue));
        rideCount.setText(String.valueOf(rideCountValue));
        restaurantCap.setText(String.valueOf(restaurantCapValue));
        simDelay.setText(String.valueOf(simDelayValue));
        wristbandChance.setText(String.valueOf(wristbandChanceValue));
        setRideCount();
    }

    @FXML
    public FlowPane createNumberInput(int index, int type) {
        FlowPane numberInput = new FlowPane();
        numberInput.getStyleClass().add("num-setting-small");
        TextField number = new TextField();
        Button increment = new Button("+");
        Button decrement = new Button("-");
        number.setText(String.valueOf(rideProperties.get(index)[type]));
        sanitizeInput(number);
        numberInput.getChildren().addAll(decrement, number, increment);
        number.setOnKeyTyped(e -> setRideParam(index, type, number));
        decrement.setOnMouseClicked(e -> changeRideParam(index, type, e, number, -1));
        increment.setOnMouseClicked(e -> changeRideParam(index, type, e, number, 1));
        return numberInput;
    }

    public void changeRideParam(int index, int type, MouseEvent e, TextField field, int power) {
        int value = power;
        if (e.isShiftDown() && !e.isControlDown()) {
            value = 5 * power;
        } else if (e.isControlDown()) {
            value = 25 * power;
        }
        rideProperties.get(index)[type] += value;
        if (rideProperties.get(index)[type] < 0) {
            rideProperties.get(index)[type] = 0;
        } else if (rideProperties.get(index)[type] > 100) {
            rideProperties.get(index)[type] = 100;
        }
        field.setText(String.valueOf(rideProperties.get(index)[type]));
    }

    public void setRideParam(int index, int type, TextField field) {
        if (!field.getText().isEmpty()) {
            int value = Integer.parseInt(field.getText());
            if (value < 0 || value > 100) {
                if (value < 0) {
                    value = 0;
                } else {
                    value = 100;
                }
                field.setText(String.valueOf(value));
            }
            rideProperties.get(index)[type] = value;
        }
    }

    @FXML
    public void displayRides() {
        rides.getChildren().clear();
        for (int i = 0; i < rideCountValue; i++) {
            FlowPane ride = new FlowPane();
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
            FlowPane variance = createNumberInput(i, 0);
            FlowPane mean = createNumberInput(i, 1);
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

    public void changeRideProperties() {
        if (rideProperties.size() < rideCountValue) {
            for (int i = rideProperties.size(); i < rideCountValue; i++) {
                rideProperties.add(new int[]{5, 2});
            }
        } else if (rideProperties.size() > rideCountValue) {
            for (int i = rideProperties.size(); i > rideCountValue; i--) {
                rideProperties.remove(i - 1);
            }
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

    public void incrementTicketBooths(MouseEvent e) {
        if (e.isShiftDown()) {
            ticketBoothCountValue += 5;
        } else {
            ticketBoothCountValue++;
        }
        ticketBoothCount.setText(String.valueOf(ticketBoothCountValue));
        setTicketBoothCount();
    }

    public void decrementTicketBooths(MouseEvent e) {
        if (e.isShiftDown()) {
            ticketBoothCountValue -= 5;
        } else {
            ticketBoothCountValue--;
        }
        ticketBoothCount.setText(String.valueOf(ticketBoothCountValue));
        setTicketBoothCount();
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

    public void setTicketBoothCount() {
        if (!ticketBoothCount.getText().isEmpty()) {
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

    public void setRideCount() {
        if (!rideCount.getText().isEmpty()) {
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
            simDelayValue = Long.parseLong(simDelay.getText());
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


    public void startSimulation() throws Exception {
//        Stage stage = (Stage) simTime.getScene().getWindow();
//        stage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/simulation.fxml"));
        Parent root = loader.load();
        SimController simController = loader.getController();

        // Show new stage
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }


    public int getRideCount() {
        return rideCountValue;
    }


    public void setWristbandChance(long amount) {

    }


    public long getWristbandChance() {
        return wristbandChanceValue;
    }
}

package view;


import controller.IControllerForV;
import controller.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import simu.framework.Trace;
import simu.framework.Trace.Level;

import java.text.DecimalFormat;



public class SimulatorGUI extends Application implements ISimulatorUI {

    //Kontrollerin esittely (tarvitaan käyttöliittymässä)
    private IControllerForV controller;

    // Käyttöliittymäkomponentit:
    private TextField time;
    private TextField delay;
    private TextField rideCount;
    private Label result;
    private Label timeLabel;
    private Label rideCountLabel;
    private Label delayLabel;
    private Label resultLabel;
    private Label wristbandLabel;
    private Label wristbandChanceLabel;

    private Button startButton;
    private Button slowDownButton;
    private Button SpeedUpButton;
    private Button lessWristbandsButton;
    private Button moreWristbandsButton;

    private IVisualization screen;


    @Override
    public void init() {

        Trace.setTraceLevel(Level.INFO);

        controller = new Controller(this);
    }

    @Override
    public void start(Stage primaryStage) {
        // Käyttöliittymän rakentaminen
        try {

            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    Platform.exit();
                    System.exit(0);
                }
            });


            primaryStage.setTitle("Huvipuistosimulaattori");

            startButton = new Button();
            startButton.setText("Käynnistä simulointi");
            startButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    int rideCount = controller.getRideCount();
                    controller.startSimulation();
                    screen.rides(rideCount);
                    updateWristbandLabel();
                    startButton.setDisable(true);
                }
            });

            slowDownButton = new Button();
            slowDownButton.setText("Hidasta");
            slowDownButton.setOnAction(e -> controller.slowDown());

            SpeedUpButton = new Button();
            SpeedUpButton.setText("Nopeuta");
            SpeedUpButton.setOnAction(e -> controller.speedUp());

            lessWristbandsButton = new Button();
            lessWristbandsButton.setText("Vähemmän rannekkeita");
            lessWristbandsButton.setOnAction(e -> {
                controller.setWristbandChance(-0.1);
                updateWristbandLabel();
            });

            moreWristbandsButton = new Button();
            moreWristbandsButton.setText("Enemmän rannekkeita");
            moreWristbandsButton.setOnAction(e -> {
                controller.setWristbandChance(0.1);
                updateWristbandLabel();
            });

            timeLabel = new Label("Simulointiaika:");
            timeLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            time = new TextField("Syötä aika");
            time.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            time.setPrefWidth(150);

            rideCountLabel = new Label("Laitemäärä:");
            rideCountLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            rideCount = new TextField("Syötä laitemäärä");
            rideCount.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            rideCount.setPrefWidth(200);

            delayLabel = new Label("Viive:");
            delayLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            delay = new TextField("Syötä viive");
            delay.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            delay.setPrefWidth(150);

            resultLabel = new Label("Kokonaisaika:");
            resultLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            result = new Label();
            result.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            result.setPrefWidth(150);

            wristbandLabel = new Label("Rannekkeiden todennäk.: ");
            wristbandLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 10));
            wristbandChanceLabel = new Label();
            wristbandChanceLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            wristbandLabel.setPrefWidth(200);

            HBox hBox = new HBox();
            hBox.setPadding(new Insets(15, 12, 15, 12)); // marginaalit ylÃ¤, oikea, ala, vasen
            hBox.setSpacing(10);   // noodien välimatka 10 pikseliä

            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setVgap(10);
            grid.setHgap(5);

            grid.add(timeLabel, 0, 0);   // sarake, rivi
            grid.add(time, 1, 0);          // sarake, rivi
            grid.add(rideCountLabel, 0, 1);      // sarake, rivi
            grid.add(rideCount, 1, 1);      // sarake, rivi

            grid.add(delayLabel, 0, 2);      // sarake, rivi
            grid.add(delay, 1, 2);      // sarake, rivi

            grid.add(resultLabel, 0, 3);      // sarake, rivi
            grid.add(result, 1, 3);           // sarake, rivi
            grid.add(startButton, 0, 4);  // sarake, rivi
            grid.add(SpeedUpButton, 0, 5);   // sarake, rivi
            grid.add(slowDownButton, 1, 5);   // sarake, rivi
            grid.add(moreWristbandsButton, 0, 6);   // sarake, rivi
            grid.add(lessWristbandsButton, 1, 6);// sarake, rivi

            grid.add(wristbandLabel, 0, 7); // sarake, rivi
            grid.add(wristbandChanceLabel, 1, 7); // sarake, rivi

            screen = new VisualizationNL(800, 400);

            // TÃ¤ytetÃ¤Ã¤n boxi:
            hBox.getChildren().addAll(grid, (Canvas) screen);

            Scene scene = new Scene(hBox);
            primaryStage.setScene(scene);
            primaryStage.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Käyttöliittymän rajapintametodit (kutsutaan kontrollerista)

    @Override
    public double getTime() {
        return Double.parseDouble(time.getText());
    }

    @Override
    public long getDelay() {
        return Long.parseLong(delay.getText());
    }

    @Override
    public void setEndTime(double time) {
        DecimalFormat formatter = new DecimalFormat("#0.00");
        this.result.setText(formatter.format(time));
    }

    @Override
    public IVisualization getVisualization() {
        return screen;
    }

    public int getRideCount() {
        return Integer.parseInt(rideCount.getText());
    }

    public void updateWristbandLabel() {
        double wristbandChance = controller.getWristbandChance();
        wristbandChanceLabel.setText(String.format("%.2f", wristbandChance));
    }
}





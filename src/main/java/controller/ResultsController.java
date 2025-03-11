package controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import simu.framework.Trace;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * ResultsController is the class for the results controller in the simulation. The class is used to visualize the results of the simulation. Implements ISettingsControllerForM.
 */
public class ResultsController implements ISettingsControllerForM{
    /**
     * The text field for the simulation time result.
     */
    @FXML
    private TextField simTimeRes;
    /**
     * The text field for the ready customers result. Shows the number of customers that have finished the simulation.
     */
    @FXML
    private TextField readyCustomersRes;
    /**
     * The text field for the ticket customers result. Shows the number of customers that used tickets and have finished the simulation.
     */
    @FXML
    private TextField ticketCustomersRes;
    /**
     * The text field for the wristband customers result. Shows the number of customers that used wristbands and have finished the simulation.
     */
    @FXML
    private TextField wristbandCustomersRes;
    /**
     * The text field for the unready customers result. Shows the number of customers that are in the middle of the simulation when the simulation ends.
     */
    @FXML
    private TextField unreadyCustomersRes;
    /**
     * The text field for the ticket booth average result. Shows how many times the ticket booth was visited on average.
     */
    @FXML
    private TextField ticketBoothAverageRes;
    /**
     * The text field for the total ticket count result. Shows the total amount of tickets purchased in the simulation.
     */
    @FXML
    private TextField totalTicketCountRes;
    /**
     * The text field for the wristband average result. Shows the average time a customer with a wristband spent in the system
     */
    @FXML
    private TextField wristbandAverageRes;
    /**
     * The text field for the ticket average result. Shows the average time a customer with a ticket spent in the system.
     */
    @FXML
    private TextField ticketAverageRes;
    /**
     * The text field for the average result. Shows the average time all customer spent in the system.
     */
    @FXML
    private TextField averageRes;
    /**
     * The text field for the wristband/ticket ratio result. Shows the ratio of the time ticket customers spent in the system compared to the time wristband customers spent in the system.
     */
    @FXML
    private TextField ratioRes;
    /**
     * The table view for the results of the simulation.
     */
    @FXML
    private TableView<ObservableList<String>> resultTableView;
    /**
     * The table column for the service point in the results table. Shows the service point name (e.g., "Ticket booth 1", "Ride 1", "Restaurant").
     */
    @FXML
    private TableColumn<ObservableList<String>, String> servicePointCol;
    /**
     * The table column for the count in the results table. Shows the amount of customers served in the service point.
     */
    @FXML
    private TableColumn<ObservableList<String>, String> countCol;
    /**
     * The table column for the average service time in the results table. Shows the average service time for the customers in the service point.
     */
    @FXML
    private TableColumn<ObservableList<String>, String> averageServiceTimeCol;
    /**
     * The table column for the average queue time in the results table. Shows the average queue time for the customers in the service point.
     */
    @FXML
    private TableColumn<ObservableList<String>, String> averageQueueTimeCol;
    /**
     * The decimal format for the results. Used to format the results to two decimal places.
     */
    private static final DecimalFormat df = new DecimalFormat("#.##");
    /**
     * The decimal format for the integer results. Used to format the integer results.
     */
    private static final DecimalFormat intF = new DecimalFormat("#");

    /**
     * Initializes the controller. Sets the cell value factories for the table columns.
     */
    @FXML
    public void initialize() {
        if (resultTableView == null) {
            Trace.out(Trace.Level.WAR, "Table view is null");
        }
        servicePointCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
        countCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
        averageServiceTimeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));
        averageQueueTimeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3)));
    }

    /**
     * Shows the static results of the simulation in the text fields in the user interface. Static results are the results that all simulations have.
     * @param staticResults Map of the static results of the simulation.
     */
    private void showStaticResults(Map<String, Double> staticResults) {
        simTimeRes.setText(df.format(staticResults.get("End time")));
        readyCustomersRes.setText(intF.format(staticResults.get("Ready customers")));
        ticketCustomersRes.setText(intF.format(staticResults.get("Ticket customers")));
        wristbandCustomersRes.setText(intF.format(staticResults.get("Wristband customers")));
        unreadyCustomersRes.setText(intF.format(staticResults.get("Unready customers")));
        ticketBoothAverageRes.setText(df.format(staticResults.get("Ticket booth average")));
        totalTicketCountRes.setText(df.format(staticResults.get("Total ticket count")));
        wristbandAverageRes.setText(df.format(staticResults.get("Wristband average time")));
        ticketAverageRes.setText(df.format(staticResults.get("Ticket average time")));
        averageRes.setText(df.format(staticResults.get("Whole average time")));
        ratioRes.setText(df.format(staticResults.get("Wristband ticket ratio")));
    }

    /**
     * Shows the dynamic results of the simulation in the table view in the user interface. Dynamic results are the results for all the service points in the simulation.
     * @param dynamicResults Map of the dynamic results of the simulation.
     */
    private void showDynamicResults(TreeMap<String, Double> dynamicResults) {
        ObservableList<ObservableList<String>> resultsList = FXCollections.observableArrayList();

        for (Map.Entry<String, Double> entry : dynamicResults.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();

            String[] parts = key.split(" ");

            if (key.contains("Ticket booth")) {
                String ticketBoothNumber = parts[2];

                if (key.contains("count")) {
                    double count = value;
                    double averageServiceTime = dynamicResults.getOrDefault("Ticket booth " + ticketBoothNumber + " average service time", 0.0);
                    double averageQueueTime = dynamicResults.getOrDefault("Ticket booth " + ticketBoothNumber + " average queue time", 0.0);

                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add("Ticket booth " + ticketBoothNumber);
                    row.add(intF.format((int) count));
                    row.add(df.format(averageServiceTime));
                    row.add(df.format(averageQueueTime));
                    resultsList.add(row);
                }
            }

            else if (key.contains("Ride")) {
                String rideNumber = parts[1];

                if (key.contains("count")) {
                    double count = value;
                    double averageServiceTime = dynamicResults.getOrDefault("Ride " + rideNumber + " average service time", 0.0);
                    double averageQueueTime = dynamicResults.getOrDefault("Ride " + rideNumber + " average queue time", 0.0);

                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add("Ride " + rideNumber);
                    row.add(intF.format(count));
                    row.add(df.format(averageServiceTime));
                    row.add(df.format(averageQueueTime));
                    resultsList.add(row);
                }
            }

            else if (key.contains("Restaurant")) {

                if (key.contains("count")) {

                    double count = value;
                    double averageServiceTime = dynamicResults.getOrDefault("Restaurant average service time", 0.0);
                    double averageQueueTime = dynamicResults.getOrDefault("Restaurant average queue time", 0.0);


                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add("Restaurant");
                    row.add(intF.format(count));
                    row.add(df.format(averageServiceTime));
                    row.add(df.format(averageQueueTime));
                    resultsList.add(row);
                }
            }
        }
        resultTableView.setItems(resultsList);
    }

    /**
     * Visualizes the results of the simulation in the user interface. Shows the static results in the text fields and the dynamic results in the table view.
     * @param staticResults Map of the static results of the simulation.
     * @param dynamicResults Map of the dynamic results of the simulation.
     */
    @Override
    public void visualizeResults(HashMap<String, Double> staticResults, TreeMap<String, Double> dynamicResults){
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/results.fxml"));
                Parent root = loader.load();
                ResultsController controller = loader.getController();
                if (controller == null) {
                    System.out.println("Controller is null. Make sure FXML is properly linked to controller.");
                    return;
                }
                controller.showStaticResults(staticResults);
                controller.showDynamicResults(dynamicResults);
                Stage stage = new Stage();
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
                stage.setTitle("Results");
                stage.setResizable(false);
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("FXML Loading failed");
            }
        });
    }

    /**
     * Shows the end time of the simulation.
     * @param time The end time of the simulation.
     */
    @Override
    public void showEndTime(double time) {

    }

    /**
     * Moves the customer in the simulation.
     */
    @Override
    public void moveCustomerAnimation() {

    }

    /**
     * Creates a new animation.
     */
    @Override
    public void newAnimation() {

    }

    /**
     * Adds a customer to the animation.
     * @param from
     * @param to
     */
    @Override
    public void addCustomerToAnimation(int from, int to) {

    }

    /**
     * Updates the time of the event.
     * @param time The time of the event.
     */
    @Override
    public void updateEventTime(double time) {

    }

    /**
     * Updates the console with a message.
     * @param msg The message to be shown in the console.
     */
    @Override
    public void updateConsole(String msg) {
    }

    /**
     * Closes the simulation.
     */
    @Override
    public void closeSimulation() {
    }
}

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
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ResultsController implements ISettingsControllerForM{
    @FXML
    private TextField simTimeRes;
    @FXML
    private TextField readyCustomersRes;
    @FXML
    private TextField ticketCustomersRes;
    @FXML
    private TextField wristbandCustomersRes;
    @FXML
    private TextField unreadyCustomersRes;
    @FXML
    private TextField ticketBoothAverageRes;
    @FXML
    private TextField totalTicketCountRes;
    @FXML
    private TextField wristbandAverageRes;
    @FXML
    private TextField ticketAverageRes;
    @FXML
    private TextField averageRes;
    @FXML
    private TextField ratioRes;
    @FXML
    private TableView<ObservableList<String>> resultTableView;
    @FXML
    private TableColumn<ObservableList<String>, String> servicePointCol;
    @FXML
    private TableColumn<ObservableList<String>, String> countCol;
    @FXML
    private TableColumn<ObservableList<String>, String> averageServiceTimeCol;
    @FXML
    private TableColumn<ObservableList<String>, String> averageQueueTimeCol;
    private static final DecimalFormat df = new DecimalFormat("#.##");
    private static final DecimalFormat intF = new DecimalFormat("#");

    @FXML
    public void initialize() {
        System.out.println("Initializing the controller...");
        if (resultTableView != null) {
            System.out.println("TableView initialized");
        } else {
            System.out.println("TableView is null");
        }
        servicePointCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
        countCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
        averageServiceTimeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));
        averageQueueTimeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3)));
    }

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

    private void showDynamicResults(TreeMap<String, Double> dynamicResults) {
        ObservableList<ObservableList<String>> resultsList = FXCollections.observableArrayList();

        for (Map.Entry<String, Double> entry : dynamicResults.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();

            // Split the key into parts to identify categories like "Ticket booth", "Ride", "Restaurant"
            String[] parts = key.split(" ");

            // Handling "Ticket booth" keys
            if (key.contains("Ticket booth")) {
                // Extract the ticket booth number from the key
                String ticketBoothNumber = parts[2];  // Get the number part (e.g., "1", "2", "3")

                if (key.contains("count")) {
                    // For count, we gather service and queue times
                    double count = value;
                    double averageServiceTime = dynamicResults.getOrDefault("Ticket booth " + ticketBoothNumber + " average service time", 0.0);
                    double averageQueueTime = dynamicResults.getOrDefault("Ticket booth " + ticketBoothNumber + " average queue time", 0.0);

                    // Add to the results list
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add("Ticket booth " + ticketBoothNumber);  // Service Point Name
                    row.add(intF.format((int) count));  // Count
                    row.add(df.format(averageServiceTime));  // Average Service Time
                    row.add(df.format(averageQueueTime));  // Average Queue Time
                    resultsList.add(row);
                }
            }
            // Handling "Ride" keys
            else if (key.contains("Ride")) {
                // Extract the ride number from the key
                String rideNumber = parts[1];  // Get the ride ID (e.g., "1", "2", "3")

                if (key.contains("count")) {
                    // For count, we gather service and queue times
                    double count = value;
                    double averageServiceTime = dynamicResults.getOrDefault("Ride " + rideNumber + " average service time", 0.0);
                    double averageQueueTime = dynamicResults.getOrDefault("Ride " + rideNumber + " average queue time", 0.0);

                    // Add to the results list
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add("Ride " + rideNumber);  // Service Point Name
                    row.add(intF.format(count));  // Count
                    row.add(df.format(averageServiceTime));  // Average Service Time
                    row.add(df.format(averageQueueTime));  // Average Queue Time
                    resultsList.add(row);
                }
            }
            // Handling "Restaurant" keys
            else if (key.contains("Restaurant")) {
                // For the restaurant, there is no number, so it's handled directly
                if (key.contains("count")) {
                    // For count, we gather service and queue times
                    double count = value;
                    double averageServiceTime = dynamicResults.getOrDefault("Restaurant average service time", 0.0);
                    double averageQueueTime = dynamicResults.getOrDefault("Restaurant average queue time", 0.0);

                    // Add to the results list
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add("Restaurant");  // Service Point Name
                    row.add(intF.format(count));  // Count
                    row.add(df.format(averageServiceTime));  // Average Service Time
                    row.add(df.format(averageQueueTime));  // Average Queue Time
                    resultsList.add(row);
                }
            }
        }

        // Set the items to the TableView
        resultTableView.setItems(resultsList);
    }
    @Override
    public void visualizeResults(HashMap<String, Double> staticResults, TreeMap<String, Double> dynamicResults){
        System.out.println("Pääsi tänne");
        System.out.println("End time: " + staticResults.get("End time"));
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

    @Override
    public void showEndTime(double aika) {

    }

    @Override
    public void visualizeCustomer(int id, int rideid, boolean wristband) {

    }

    @Override
    public void moveCustomerAnimation() {

    }

    @Override
    public void newAnimation() {

    }

    @Override
    public void addCustomerToAnimation(int from, int to) {

    }

    @Override
    public void updateEventTime(double time) {

    }

    @Override
    public void updateConsole(String msg) {
    }

    @Override
    public void closeSimulation() {
    }
}

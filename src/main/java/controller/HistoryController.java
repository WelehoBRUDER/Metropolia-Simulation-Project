package controller;

import database.Dao;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * HistoryController is the class that controls the history selection screen.
 */
public class HistoryController {
    private Dao dao = new Dao();
    private int sims = 0;
    private String pattern = "MM/dd/yyyy HH:mm:ss";
    private DateFormat df = new SimpleDateFormat(pattern);
    @FXML
    private FlowPane allResults;

    /**
     * Setter for the sims attribute.
     * @param sims The number of simulations in the database.
     */
    public void setSims(int sims) {
        this.sims = sims;
    }

    /**
     * Initializes the history screen by setting the number of simulations and creating the history links.
     */
    public void initialize(){
        this.setSims(dao.getSimId());
        this.createHistoryLinks();
    }

    /**
     * Opens the results screen for the selected simulation.
     * @param index The index of the simulation to be visualized.
     */
    public void unearth(String index){

        int s = Integer.parseInt(index.trim());

        ResultsController resultsController = new ResultsController();
        resultsController.visualizeResults(dao.getStaticResultsById(s), dao.getDynamicResultsById(s));
    }

    /**
     * Creates the history links for the simulations.
     */
    public void createHistoryLinks() {
        this.allResults.getChildren().clear();
        for (int i = 1; i <= this.sims; i++) {
            VBox container = new VBox();
            Label timestamp = new Label(df.format(dao.getSimTimestampById(i)));
            timestamp.setPadding(new Insets(0, 0, 4, 0));
            HBox link = new HBox();
            container.getStyleClass().add("history-link");
            link.setAlignment(javafx.geometry.Pos.CENTER);
            container.setAlignment(javafx.geometry.Pos.CENTER);
            link.setPrefWidth(200);
            Label title = new Label("Simulation " + i + "  ");
            Button clickable = new Button("View");
            clickable.setOnAction(e -> {
                this.unearth(title.getText().substring(11));
            });
            link.getChildren().addAll(title, clickable);
            container.getChildren().add(timestamp);
            container.getChildren().add(link);
            this.allResults.getChildren().add(container);
        }
    }
}

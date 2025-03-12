package controller;

import database.Dao;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class HistoryController {
    private Dao dao = new Dao();
    private int sims = 0;
    @FXML
    private VBox allResults;

    public void setSims(int sims) {
        this.sims = sims;
    }

    @FXML
    private TextField searchBar;

    public void initialize(){
        this.setSims(dao.getSimId());
        this.createHistoryLinks();
    }

    public void unearth(){

        int s = Integer.parseInt(searchBar.getText());
        System.out.println(s);
        //System.out.println(dao.getRestaurantById(s));
        System.out.println(dao.getStaticResultsById(s));
        System.out.println(dao.getDynamicResultsById(s));

        ResultsController resultsController = new ResultsController();
        resultsController.visualizeResults(dao.getStaticResultsById(s), dao.getDynamicResultsById(s));
    }

    public void createHistoryLinks() {
        this.allResults.getChildren().clear();
        for (int i = 1; i <= this.sims; i++) {
            HBox link = new HBox();
            link.getStyleClass().add("history-link");
            link.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            Label title = new Label("Simulation " + i);
            Button clickable = new Button("View");
            clickable.setOnAction(e -> {
                this.searchBar.setText(title.getText().substring(11));
                this.unearth();
            });
            link.getChildren().addAll(title, clickable);
            this.allResults.getChildren().add(link);
        }
    }
}

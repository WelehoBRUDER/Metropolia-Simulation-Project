package controller;

import database.Dao;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class HistoryController {
    private Dao dao = new Dao();

    @FXML
    private TextField searchBar;

    public void initialize(){
        //dao.getSimId();
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

}

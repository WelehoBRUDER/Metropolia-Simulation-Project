package view;

import controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import simu.framework.Trace;

/**
 * SettingsView is a class that launches the settings.fxml file.
 */
public class SettingsView extends Application {

    /**
     * Initializes the settings view.
     */
    @Override
    public void init() {
        Trace.setTraceLevel(Trace.Level.INFO);

    }

    /**
     * Starts the settings view.
     * @param stage the current stage
     * @throws Exception if something goes wrong
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/settings.fxml"));
        Parent root = fxmlLoader.load();

        stage.setTitle("Simulation settings");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Launches the settings view.
     * @param args the arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
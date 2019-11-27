package project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import project.ui.UIController;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/project.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Tower Defence");
        primaryStage.setScene(new Scene(root, 600, 480));
        primaryStage.show();
        UIController appController = (UIController)loader.getController();
        appController.createArena();
        SpringApplication.main(new String[]{});
    }

    public static void main(String[] args) {
        launch(args);
    }
}

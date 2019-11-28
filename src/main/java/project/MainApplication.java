package project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import project.database.config.Config;
import project.database.controller.Manager;
import project.ui.UIController;

import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class MainApplication extends Application {

    public static ConfigurableApplicationContext context;

    @Override
    public void init() throws Exception {
        CompletableFuture.supplyAsync(() -> {
            ConfigurableApplicationContext ctx = org.springframework.boot.SpringApplication.run(this.getClass());
            return ctx;
        }).thenAccept(this::launchApplicationView);
    }

    private void launchApplicationView(ConfigurableApplicationContext ctx) {
        context = ctx;
        Config hc = context.getBean(Config.class);
        Manager.setEntityManager(hc.getEntityManagerFactory().createEntityManager());
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/project.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Tower Defence");
        primaryStage.setScene(new Scene(root, 600, 480));
        primaryStage.show();
        UIController appController = (UIController)loader.getController();
        appController.createArena();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

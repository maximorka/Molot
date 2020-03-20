package com.molot;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import com.molot.ui.MainController;

import java.io.IOException;

public class MolotApp extends Application {
    private static Stage stage;

    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        show();
    }

    private static void show() {
        Platform.runLater(new Runnable() {
            public void run() {
                try {
                    Parent page = FXMLLoader.<Parent>load(MainController.class.getResource("/StartWindow.fxml"));
                    Scene scene = new Scene(page);
                    stage.setScene(scene);
                    stage.show();
                    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        public void handle(WindowEvent event) {
                            System.out.println("platform exit");
                            Platform.exit();
                            System.exit(0);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

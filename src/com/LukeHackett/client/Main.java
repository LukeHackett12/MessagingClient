package com.LukeHackett.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Main.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("clientWindow.fxml"));
        primaryStage.setTitle("Messaging Client");
        Scene scene = new Scene(root, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();

        ClientController.messageDisplay = (TextArea) scene.lookup("#messageDisplay");
        ClientController.clientList = (ListView) scene.lookup("#clientList");
        ClientController.userInput = (TextArea) scene.lookup("#userInput");
    }


    public static void main(String[] args) {
        launch(args);
    }
}

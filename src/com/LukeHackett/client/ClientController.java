package com.LukeHackett.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ClientController {

    public static HashMap<Long, String> messageDisplays;
    public static long currentID;

    @FXML public static ListView clientList;
    @FXML public static  TextArea messageDisplay;
    @FXML public static TextArea userInput;

    public ClientController(){
        messageDisplays = new HashMap<>();
    }

    @FXML protected void handleAddClientAction() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(Main.primaryStage);
        GridPane gridPane = new GridPane();

        Text prompt = new Text("Enter the client number: ");
        GridPane.setConstraints(prompt, 0, 0);
        gridPane.getChildren().add(prompt);

        TextField input = new TextField();
        GridPane.setConstraints(input, 0, 1);
        gridPane.getChildren().add(input);

        Button enterButton = new Button("Add");
        enterButton.setOnAction(e->{
            String s = input.getText();
            try {
                addClient(Long.parseLong(s));
            } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e1) {
                e1.printStackTrace();
            }
            dialog.setOnCloseRequest(t -> Platform.exit());
            dialog.close();
        });
        GridPane.setConstraints(enterButton, 1, 1);
        gridPane.getChildren().add(enterButton);

        Scene dialogScene = new Scene(gridPane, 200, 50);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @FXML protected void handleAddServerAction() {
        clientList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //setDisplay(newValue.toString());
            currentID = Long.parseLong(newValue.toString().split(" ")[1]);
            setDisplay(currentID);
        });

        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(Main.primaryStage);
        GridPane gridPane = new GridPane();

        Text prompt = new Text("Enter the server ip: ");
        GridPane.setConstraints(prompt, 0, 0);
        gridPane.getChildren().add(prompt);

        TextField input = new TextField();
        GridPane.setConstraints(input, 0, 1);
        gridPane.getChildren().add(input);

        Button enterButton = new Button("Add");
        enterButton.setOnAction(e->{
            String s = input.getText();
            Messenger m = new Messenger(s);
            Thread t = new Thread(m);
            t.start();
            dialog.setOnCloseRequest(t1 -> Platform.exit());
            dialog.close();
        });
        GridPane.setConstraints(enterButton, 1, 1);
        gridPane.getChildren().add(enterButton);

        Scene dialogScene = new Scene(gridPane, 200, 50);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @FXML protected void handleSendMessageAction() throws IOException {
        String s = userInput.getText();
        userInput.setText("");
        String alter = messageDisplays.get(currentID) + '\n' + "Client " + Messenger.client.getId() + ": " + s;
        Message message = new Message(Messenger.client.getId(), currentID, s);

        messageDisplays.put(currentID, alter);
        Messenger.sendMessage(message, currentID, Messenger.client);
    }

    public static void addClient(Long newClient) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        PublicKey key = Messenger.receiveKey(Messenger.client, newClient);
        System.out.println("Adding key for Client" + newClient);
        Messenger.recipients.put(newClient, key);

        String messageD = " ";
        messageDisplays.put(newClient, messageD);

        ObservableList<String> data = FXCollections.observableArrayList(clientList.getItems());
        data.add("Client " + newClient);
        clientList.setItems(data);
    }

    private void setDisplay(Long clientID){
        currentID = clientID;
        Timer t = new Timer();
        t.scheduleAtFixedRate( new TimerTask() {
            public void run() {
                messageDisplay.setText(messageDisplays.get(currentID));
            }
        }, 0, 100);
    }
}

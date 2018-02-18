package com.LukeHackett.client;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

public class ClientWindow {
    JTextField userInput;
    JTextField addClientText;
    JList<String> clientList;
    JButton addClientButton;
    JButton sendButton;
    JPanel contentPane;

    private static DefaultListModel<String> model;
    private long currentID;

    public JTextArea messageDisplay;
    public static HashMap<String, String> messageDisplays;
    public static String currentHash;

    public static void main(String[] args){
        Messenger m = new Messenger();
        Thread thread = new Thread(m);
        thread.start();

        new ClientWindow();
    }

    private ClientWindow(){
        JFrame frame = new JFrame("Client");

        model = new DefaultListModel<>();
        messageDisplays = new HashMap<>();
        messageDisplay.setFont(messageDisplay.getFont().deriveFont(12f));
        sendButton.setFont(sendButton.getFont().deriveFont(12f));

        clientList.setModel(model);
        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        ActionListener updateText = evt -> {
            if(clientList.getSelectedIndex() >= 0) setClientView(clientList.getSelectedIndex());
        };
        Timer timer = new Timer(100 ,updateText);
        timer.setRepeats(true);
        timer.start();

        addClientButton.addActionListener(e -> {
            try {
                Long newClient = Long.parseLong(addClientText.getText());
                addClient(newClient);
            } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e1) {
                e1.printStackTrace();
            }
        });
        sendButton.addActionListener(e -> {
            try {
                sendMessage();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        clientList.addListSelectionListener(e -> {
            if(e.getValueIsAdjusting()) setClientView(clientList.getSelectedIndex());
        });
    }

    public static void addClient(Long newClient) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        PublicKey key = Messenger.receiveKey(Messenger.testClient, newClient);
        System.out.println("Adding key for Client" + newClient);
        Messenger.recipients.put(newClient, key);

        model.addElement("Client " + newClient);
        String messageD = "Client " + newClient;

        messageDisplays.put(messageD, messageD);
    }

    private void sendMessage() throws IOException {
        String s = userInput.getText();
        userInput.setText("");
        String alter = messageDisplays.get(currentHash) + '\n' +
                "Client " + Messenger.testClient.getId() + ": " + s;
        Message message = new Message(Messenger.testClient.getId(), currentID, s);

        messageDisplays.put(currentHash, alter);
        Messenger.sendMessage(message, currentID, Messenger.testClient);
    }

    private void setClientView(int selectedIndex){
        //Set client view
        String clientID = clientList.getModel().getElementAt(selectedIndex);
        messageDisplay.setText(messageDisplays.get(clientID));

        //Parse the long from the client + long title
        String[] split = clientID.split(" ");
        currentID = Long.parseLong(split[1]);
        currentHash = clientID;
    }
}

package com.LukeHackett.client;

import javafx.application.Platform;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;


public class Messenger implements Runnable{

    public static HashMap<Long, PublicKey> recipients;
    public static Client client;

    private static String server;

    Messenger(String server){
        Messenger.server = server;
    }

    public void run(){
        recipients = new HashMap<>();
        client = new Client(server, 5050);
        try {
            client.setListenerSocket(server, 5050);
        } catch (IOException e) {
            e.printStackTrace();
        }

        keyExchange(client);
        System.out.println("Client " + client.getId() + "pub id is: " + client.getPublicKey());
        System.out.println("Client " + client.getId());

        Platform.runLater(
                () -> {
                    ClientController.showID();
                }
        );

        //Start listener receiving any incoming messages
        Listener receiveMessage = new Listener(client, client.getPrivateKey());
        Thread receiveMessageThread = new Thread(receiveMessage);
        receiveMessageThread.start();
    }

    public static void sendMessage(Message message, Long recipient, Client testClient) throws IOException {
        DataOutputStream clientOutput = null;
        try {
            clientOutput = new DataOutputStream(testClient.getListenerSocket().getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] encryptedMessage = new byte[0];
        PublicKey key = recipients.get(recipient);
        System.out.print("Encrypting with key: " + key);
        try {
            if (message.message == null) throw new AssertionError();
            encryptedMessage = testClient.encryptClientMessage(key, message.message.getBytes());
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        //Send recipient and sender
        clientOutput.writeLong(message.recipientID);
        clientOutput.writeLong(message.senderID);
        //Send length of encrypted message and the encrypted message itself
        clientOutput.writeInt(encryptedMessage.length);
        clientOutput.write(encryptedMessage);
    }

    private static void keyExchange(Client client){
        try {
            //send public key to server
            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.putInt(client.getPublicKey().getEncoded().length);
            client.getListenerSocket().getOutputStream().write(bb.array());
            client.getListenerSocket().getOutputStream().write(client.getPublicKey().getEncoded());
            client.getListenerSocket().getOutputStream().flush();

            //set id
            DataInputStream dIn = new DataInputStream(client.getListenerSocket().getInputStream());
            client.setId(dIn.readLong());
        } catch (IOException e) {
            System.out.println("I/O Error");
            System.exit(0);
        }
    }

    public static PublicKey receiveKey(Client client, Long recipientID) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        DataOutputStream clientOutput = new DataOutputStream(client.getServerSocket().getOutputStream());
        clientOutput.writeLong(recipientID);
        System.out.println("Output ID " + recipientID);

        //receive public key of recipient
        byte[] lenb = new byte[4];
        client.getServerSocket().getInputStream().read(lenb, 0, 4);
        ByteBuffer bb = ByteBuffer.wrap(lenb);
        int len = bb.getInt();

        byte[] servPubKeyBytes = new byte[len];
        client.getServerSocket().getInputStream().read(servPubKeyBytes);
        X509EncodedKeySpec ks = new X509EncodedKeySpec(servPubKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pub = kf.generatePublic(ks);
        System.out.println("Client " + recipientID + "pub id is: " + pub);
        return pub;
    }
}

package com.LukeHackett.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;


public class Main {
    public static void main(String argv[]) throws Exception {
        Client testClient = new Client("localhost", 5050);

        Socket clientSocket = testClient.getSocket();
        keyExchange(testClient);

        DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        long recipientID = Long.parseLong(inFromUser.readLine());
        String message = inFromUser.readLine();

        //TODO query server for user public keys
        byte[] encryptedMessage = testClient.encryptClientMessage(testClient.getPublicKey(), message.getBytes());
        Message serverPackage = new Message(testClient.getId(), recipientID, encryptedMessage);

        sendMessage(serverPackage, clientOutput);

        testClient.closeSocket();
    }

    public static void sendMessage(Message message, DataOutputStream clientOutput)throws IOException {
        clientOutput.writeInt(message.message.length);
        clientOutput.write(message.message);
    }

    public static void keyExchange(Client client){
        try {
            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.putInt(client.getPublicKey().getEncoded().length);
            client.getSocket().getOutputStream().write(bb.array());
            client.getSocket().getOutputStream().write(client.getPublicKey().getEncoded());
            client.getSocket().getOutputStream().flush();
        } catch (IOException e) {
            System.out.println("I/O Error");
            System.exit(0);
        }
    }
}

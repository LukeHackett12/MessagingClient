package com.LukeHackett.client;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.PrivateKey;


public class Listener implements Runnable {

    private Client client;
    private PrivateKey privkey;

    Listener(Client client, PrivateKey privKey){
        this.client = client;
        this.privkey = privKey;
    }

    public void run(){
        boolean done = false;
        while(!done) {
            try {
                receiveMessage(client, privkey);
            } catch (IOException | NoSuchPaddingException | BadPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | InvalidKeySpecException e) {
                done = false;
                e.printStackTrace();
            }
        }
    }

    private void receiveMessage(Client client, PrivateKey privKey) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException {
        DataInputStream dIn = new DataInputStream(client.getListenerSocket().getInputStream());

        Long sender = dIn.readLong();
        int length = dIn.readInt();
        byte[] message = new byte[length];
        dIn.readFully(message, 0, length); // read the message

        Cipher decrypt=Cipher.getInstance("RSA");
        decrypt.init(Cipher.DECRYPT_MODE, privKey);
        String decryptedMessage = new String(decrypt.doFinal(message));
        String senderID = "Client " + sender;

        //Check if the client already has an open chat with the
        if(ClientWindow.messageDisplays.containsKey(senderID)){
            String newText = ClientWindow.messageDisplays.get(senderID) + "\n" + senderID + ": " + decryptedMessage;
            ClientWindow.messageDisplays.put(senderID, newText);
        }
        else{
            //Add client
            ClientWindow.addClient(sender);
            String newText = ClientWindow.messageDisplays.get(senderID) + "\n" + senderID + ": " + decryptedMessage;
            ClientWindow.messageDisplays.put(senderID, newText);
        }
    }
}

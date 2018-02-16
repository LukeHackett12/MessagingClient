package com.LukeHackett.client;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
            } catch (IOException | NoSuchPaddingException | BadPaddingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException e) {
                done = false;
                e.printStackTrace();
            }
        }
    }

    private void receiveMessage(Client client, PrivateKey privKey) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        DataInputStream dIn = new DataInputStream(client.getListenerSocket().getInputStream());

        //TODO Figure out why there is no return even though the socket port seems to be right
        int length = dIn.readInt();
        byte[] message = new byte[length];
        dIn.readFully(message, 0, length); // read the message

        Cipher decrypt=Cipher.getInstance("RSA");
        decrypt.init(Cipher.DECRYPT_MODE, privKey);
        String decryptedMessage = new String(decrypt.doFinal(message));

        String newText = ClientWindow.messageDisplays.get(ClientWindow.currentHash) + "\n"
                + ClientWindow.currentHash + ": " + decryptedMessage;
        ClientWindow.messageDisplays.put(ClientWindow.currentHash, newText);
    }
}

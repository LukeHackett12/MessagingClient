package com.LukeHackett.client;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.*;

class Client {
    private long id;
    private KeyPair keypair;
    private Socket serverSocket;
    private Socket listenerSocket;

    Client(String requestedIP, int port){
        try{
            serverSocket = new Socket(requestedIP, port);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        kpg.initialize(1024);
        keypair = kpg.genKeyPair();
    }

    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return id;
    }

    public Socket getServerSocket(){
        return serverSocket;
    }

    public Socket getListenerSocket(){
        return listenerSocket;
    }

    public void setListenerSocket(String requestedIP, int port) throws IOException {
        listenerSocket = new Socket(requestedIP, port);
    }

    public void closeServerSocket(){
        try{
            serverSocket.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void closeListenerSocket(){
        try{
            listenerSocket.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public PublicKey getPublicKey(){
        return keypair.getPublic();
    }

    public PrivateKey getPrivateKey(){
        return keypair.getPrivate();
    }

    public byte[] encryptClientMessage(PublicKey pubKey, byte[] message) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(message);
    }
}

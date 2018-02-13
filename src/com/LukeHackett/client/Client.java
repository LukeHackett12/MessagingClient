package com.LukeHackett.client;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

class Client {
    private long id;
    private KeyPair keypair;
    private Socket socket;

    Client(String requestedIP, int port){
        this.id = id;
        try{
            socket = new Socket(requestedIP, port);
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

    public Socket getSocket(){
        return socket;
    }

    public void closeSocket(){
        try{
            socket.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public PublicKey getPublicKey(){
        return keypair.getPublic();
    }

    public byte[] encryptClientMessage(PublicKey pubKey, byte[] message) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(message);
    }
}

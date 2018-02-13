package com.LukeHackett.client;

public class Message {
    long senderID;
    long recipientID;
    byte[] message;

    Message(long senderID, long recipientID, byte[] message){
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.message = message;
    }
}

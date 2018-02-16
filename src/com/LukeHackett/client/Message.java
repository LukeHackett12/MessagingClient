package com.LukeHackett.client;

public class Message {
    long senderID;
    long recipientID;
    String message;

    Message(long senderID, long recipientID, String message){
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.message = message;
    }
}

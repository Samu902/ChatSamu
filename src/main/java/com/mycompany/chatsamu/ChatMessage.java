package com.mycompany.chatsamu;

//This packet can be sent and received from server and client:
//in particular client sends the filled packet to the server and the server (more correctly the clientHandler associated with that client) forwards it to the receiver
/**
 * This class represents a text message between two or all users
 * @author Samu
 */
public class ChatMessage extends Message
{
    private String senderClient;
    private String receiverClient;  //null for all
    
    private String text;
    
    /**
     * Initialize a chat message
     * @param sender nickname of sender client
     * @param receiver nickname of receiver client (null for all)
     * @param text content of the message
     */
    public ChatMessage(String sender, String receiver, String text)
    {
        super(Message.CHAT);
        senderClient = sender;
        receiverClient = receiver;
        this.text = text;
    }    

    /**
     * @return nickname of sender  client
     */
    public String getSenderClient()
    {
        return senderClient;
    }

    /**
     * @return nickname of receiver client (null for all)
     */
    public String getReceiverClient()
    {
        return receiverClient;
    }

    /**
     * @return content of the message
     */
    public String getText()
    {
        return text;
    }
}

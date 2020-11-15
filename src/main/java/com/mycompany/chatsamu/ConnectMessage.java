package com.mycompany.chatsamu;

//This type of message is sent from the client to the server at first to login with nickname
//if server doesn't accept due to duplicate nickname, it sends back a message with error code (1),
//so client must login again
/**
 * Type of message used to connect to the chat
 * @author Samu
 */
public class ConnectMessage extends Message
{
    private String connectedClient;
    private int code;
    
    /**
     * Initialize a connect message
     * @param connectedClient nickname of the connected client
     */
    public ConnectMessage(String connectedClient)
    {
        super(Message.CONNECT);
        this.connectedClient = connectedClient;
        this.code = 0;
    }
    
    /**
     * Initialize a connect message
     * @param connectedClient nickname of the connected client
     * @param code result code (0=success, 1=error), put by server
     */
    public ConnectMessage(String connectedClient, int code)
    {
        super(Message.CONNECT);
        this.connectedClient = connectedClient;
        this.code = code;
    }    

    /**
     * 
     * @return nickname of the connected client
     */
    public String getConnectedClient()
    {
        return connectedClient;
    }

    /**
     * 
     * @return result code (0=success, 1=error), put by server
     */
    public int getCode()
    {
        return code;
    }
}

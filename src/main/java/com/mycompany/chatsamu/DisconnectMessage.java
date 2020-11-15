package com.mycompany.chatsamu;

//This message is sent from a client to the server which sends a user list packet update to all other clients
/**
 * Type of message used to disconnect from the chat
 * @author Samu
 */
public class DisconnectMessage extends Message
{
    private String disconnectedClient;

    /**
    * Initialize a disconnect message
    * @param disconnectedClient nickname of the disconnected client
    */
    public DisconnectMessage(String disconnectedClient)
    {
        super(Message.DISCONNECT);
        this.disconnectedClient = disconnectedClient;
    }

    /**
     *
     * @return nickname of the disconnected client
     */
    public String getDisconnectedClient()
    {
        return disconnectedClient;
    }
}

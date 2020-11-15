package com.mycompany.chatsamu;

import java.util.ArrayList;

//This message is sent from the server to all clients
/**
 * Type of message that carries the list of users
 * @author Samu
 */
public class UserListMessage extends Message
{
    private ArrayList<String> users;
    
    /**
     * Initialize a connect message
     * @param users list of user nicknames
     */
    public UserListMessage(ArrayList<String> users)
    {
        super(Message.USER_LIST);
        this.users = users;
    }

    /**
     * 
     * @return list of user nicknames
     */
    public ArrayList<String> getUsers()
    {
        return users;
    }
}

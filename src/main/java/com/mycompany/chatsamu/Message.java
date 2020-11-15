package com.mycompany.chatsamu;

//Base class for message: it contains only the type.
//Even if not abstract, this class should not be used as is, you should instantiate a child message instead
/**
 * Base class for message
 * @author Samu
 */
public class Message
{
    /**
     * Constant for chat message type
     */
    public static final int CHAT = 0;
    
    /**
     * Constant for connect message type
     */
    public static final int CONNECT = 1;
    
    /**
     * Constant for disconnect message type
     */
    public static final int DISCONNECT = 2;
    
    /**
     * Constant for user list message type
     */
    public static final int USER_LIST = 3;
    
    protected int type;
    
    /**
     * Initialize a message
     * @param type type of the message
     */
    public Message(int type)
    {
        this.type = type;
    }

    /**
     * 
     * @return type of the message
     */
    public int getType()
    {
        return type;
    }
}

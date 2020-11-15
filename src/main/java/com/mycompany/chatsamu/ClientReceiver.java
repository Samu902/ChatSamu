package com.mycompany.chatsamu;

/**
 * This thread based class handles the input messages
 * @author Samu
 */
public class ClientReceiver extends Thread
{
    private Client parent;

    /**
     * Initialize a client receiver thread (client IN thread)
     * @param parent the client which this object belongs to
     */
    public ClientReceiver(Client parent)
    {
        this.parent = parent;
        start();
    }

    @Override
    public void run()
    {
        while(true)
        {
            //Receive message
            parent.receive();
        }
    }
}

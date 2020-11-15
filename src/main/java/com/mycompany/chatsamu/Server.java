package com.mycompany.chatsamu;

import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * This class handles initial connection between server and client: it assigns a client handler to every client
 * @author Samu
 */
public class Server
{
    private ServerSocket server;
    private int port;
    
    private ArrayList<ClientHandler> clients;
    private int maxClients;

    /**
     * Initialize a server
     * @param port port used to open service
     * @param maxClients number of connections allowed
     */
    public Server(int port, int maxClients)
    {
        this.port = port;
        this.maxClients = maxClients;
    }
    
    /**
     * Start to listen for connections
     */
    public void listen()
    {
        try
        {
            System.out.println("SERVER partito in esecuzione");
            server = new ServerSocket(port);
            
            clients = new ArrayList<ClientHandler>();
            
            for(int i = 0; i < maxClients; i++)
                new ClientHandler(server.accept(), clients);
            
            server.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Start point for server program
     * @param args command line args
     */
    public static void main(String[] args)
    {
        Server server = new Server(6789, 10);
        server.listen();
    }
}

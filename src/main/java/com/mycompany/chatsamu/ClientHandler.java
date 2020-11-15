package com.mycompany.chatsamu;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class manages a client from the server
 * @author Samu
 */
public class ClientHandler extends Thread
{
    private Socket client;
    private DataOutputStream outputStream;
    private BufferedReader inputStream;
    private String clientNickname;
    
    private ArrayList<ClientHandler> allClients;

    private Gson gson;

    /**
     * Initialize a client handler
     * @param client socket between server and associated client
     * @param allClients list of all client handlers
     */
    public ClientHandler(Socket client, ArrayList<ClientHandler> allClients)
    {
        try 
        {
            this.client = client;
            this.allClients = allClients;
            outputStream = new DataOutputStream(client.getOutputStream());
            inputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
            gson = new Gson();

            start();
        } 
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void run()
    {
        try
        {
            System.out.println("Nuovo client handler");
            
            //Receive message, check what type is and do the correct action based on the type
            while (true)
            {                
                String json = inputStream.readLine();

                switch (gson.fromJson(json, Message.class).getType())
                {
                    //Forward message to destination clients
                    case Message.CHAT:
                        ChatMessage cmsg = gson.fromJson(json, ChatMessage.class);

                        if(cmsg.getReceiverClient() == null)
                            sendToAllOtherClients(cmsg);
                        else
                            sendToOtherClient(cmsg, cmsg.getReceiverClient());
                        
                        break;
                    //Receive nickname and check if it already exists
                    //if yes, send a connect message with code 1 (error) and wait for new login
                    //if no, send a success connect message (code=0) and a userlist message
                    case Message.CONNECT:
                        ConnectMessage comsg = gson.fromJson(json, ConnectMessage.class);
                        clientNickname = comsg.getConnectedClient();

                        boolean exists = false;
                        for (ClientHandler c : allClients)
                        {
                            if(c.clientNickname.equals(clientNickname))
                            {
                                exists = true;

                                ConnectMessage nmsg = new ConnectMessage(clientNickname, 1);
                                sendToMyClient(nmsg);
                                break;
                            }
                        }
                        
                        if(!exists)
                        {
                            allClients.add(this);
                            
                            ConnectMessage ymsg = new ConnectMessage(clientNickname, 0);
                            sendToMyClient(ymsg);

                            UserListMessage umsg = new UserListMessage(nicknamesFromClientHandlers(false));
                            sendToAllClients(umsg);
                        }
                        break;
                    case Message.DISCONNECT:
                        DisconnectMessage dmsg = gson.fromJson(json, DisconnectMessage.class);
                        UserListMessage umsg2 = new UserListMessage(nicknamesFromClientHandlers(true));
                        sendToAllOtherClients(umsg2);
                        close();
                        break;
                    default:
                        break;
                }
            }
        }
        catch (JsonSyntaxException | IOException e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    
    //Utility method to get list of nicknames from list of client handlers
    private ArrayList<String> nicknamesFromClientHandlers(boolean withoutMe)
    {
        ArrayList<String> allUsers = new ArrayList<String>();
        for (ClientHandler c : allClients)
        {
            if(withoutMe && c == this)
                continue;

            allUsers.add(c.clientNickname);
        }
        
        return allUsers;
    }
    
    /**
     * Send a message to my client
     * @param msg the message to send
     * @throws IOException cannot write to stream
     */
    public void sendToMyClient(Message msg) throws IOException
    {
        String json = gson.toJson(msg);
        outputStream.writeBytes(json + "\n");
    }
    
    /**
     * Send a message to other client
     * @param msg the message to send
     * @param nickname client nickname
     * @throws IOException cannot write to stream
     */
    public void sendToOtherClient(Message msg, String nickname) throws IOException
    {
        for (ClientHandler c : allClients)
        {
            if(c.clientNickname.equals(nickname))
            {
                String json = gson.toJson(msg);
                c.outputStream.writeBytes(json + "\n");
                break;
            }
        }
    }
    
    /**
     * Send a message to all clients except me
     * @param msg the message to send
     * @throws IOException cannot write to stream
     */
    public void sendToAllOtherClients(Message msg) throws IOException
    {
        for (ClientHandler c : allClients)
        {
            if(c == this)
                continue;

            String json = gson.toJson(msg);
            c.outputStream.writeBytes(json + "\n");
        }
    }
    
    /**
     * Send a message to all clients
     * @param msg the message to send
     * @throws IOException cannot write to stream
     */
    public void sendToAllClients(Message msg) throws IOException
    {
        for (ClientHandler c : allClients)
        {
            String json = gson.toJson(msg);
            c.outputStream.writeBytes(json + "\n");
        }
    }

    /**
     * Close socket and remove client from array
     */
    public void close()
    {
        try
        {
            System.out.println("Client handler termina elaborazione");
            allClients.remove(this);
            //FONTE DI OGNI MALE - chiuderebbe il data socket, ma fa crashare tutti i client e il server
//            client.close();
        } 
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}

package com.mycompany.chatsamu;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * This class represents a client in his logic.
 * It connects and starts in and out threads
 * @author Samu
 */
public class Client
{
    private String serverIP;
    private int serverPort;
    
    private Socket socket;
    private DataOutputStream outputStream;
    private BufferedReader inputStream;
    
    private ClientReceiver receiverThread;
    
    private String nickname;
    private ArrayList<String> allUsers;
    
    private Gson gson;
    
    /**
     * @param ip server IP
     * @param port server port
     */
    public Client(String ip, int port)
    {
        serverIP = ip;
        serverPort = port;
    }
    
    /**
     * Connect to the server at the specified IP and port in constructor and start threads
     */
    public void connect()
    {
        System.out.println("CLIENT partito in esecuzione");
        try
        {
            //Initialize socket variables and json formatter
            socket = new Socket(serverIP, serverPort);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            gson = new Gson();
            
            receiverThread = new ClientReceiver(this);
        } 
        catch (UnknownHostException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Host sconosciuto");
            System.exit(1);
        }
        catch (ConnectException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Non c'è posto per te, mi spiace");
            System.exit(1);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Send a message based on the given message type and content of fields in GUI
     * @param messageType type of message
     */
    public void send(int messageType)
    {
        //CHAT message: sends the message
        //CONNECT message: sends the message
        //DISCONNECT message: sends the message, then closes application
        //USER_LIST message: client can't send a user list message
        try
        {
            switch(messageType)
            {
                case Message.CHAT:
                    //Split text into receiver and message text
                    String completeText = ChatGUI.getInstance().getMessageField().getText();
                    String receiver = completeText.startsWith("@") ? completeText.substring(1, completeText.indexOf(" ")) : null;
                    String text = completeText.startsWith("@") ? completeText.substring(completeText.indexOf(" ") + 1): completeText;
                    
                    //Validate message receiver
                    if(nickname.equals(receiver))
                        ChatGUI.getInstance().getChatHistory().append("YOU (TO YOU): " + text + "\n");
                    else if(!allUsers.contains(receiver) && receiver != null)
                        ChatGUI.getInstance().getChatHistory().append("SYSTEM: this user isn't connected\n");
                    else
                    {
                        ChatMessage cmsg = new ChatMessage(nickname, receiver, text);
                        
                        if(receiver == null)
                            ChatGUI.getInstance().getChatHistory().append("YOU (TO ALL): " + text + "\n");
                        else
                            ChatGUI.getInstance().getChatHistory().append("YOU (TO " + receiver + "): " + text + "\n");

                        outputStream.writeBytes(gson.toJson(cmsg) + "\n");
                    }
                    break;
                case Message.CONNECT:
                    ConnectMessage comsg = new ConnectMessage(ChatGUI.getInstance().getLoginField().getText());
                    outputStream.writeBytes(gson.toJson(comsg) + "\n");
                    break;
                case Message.DISCONNECT:
                    DisconnectMessage dmsg = new DisconnectMessage(nickname);
                    outputStream.writeBytes(gson.toJson(dmsg) + "\n");
                    close();
                    break;
            }
        } 
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Receive json from input stream and parse it to the correct type, then it behaves differently based on the message
     * 
     */
    public void receive()
    {
        //Client receives a message: it finds out which type of message is and so it behaves differently
        //CHAT message: prints the message in the area
        //CONNECT message: client can't receive a connect message from the server once it's connected properly
        //DISCONNECT message: client can't receive a disconnect message from the server
        //USER_LIST message: it shows the list of connected users
        try
        {
            String json = inputStream.readLine();

            switch (gson.fromJson(json, Message.class).getType())
            {
                case Message.CHAT:
                    ChatMessage cmsg = gson.fromJson(json, ChatMessage.class);

                    if(cmsg.getReceiverClient() == null)
                        ChatGUI.getInstance().getChatHistory().append(cmsg.getSenderClient() + " (TO ALL): " + cmsg.getText() + "\n");
                    else
                        ChatGUI.getInstance().getChatHistory().append(cmsg.getSenderClient() + " (TO YOU): " + cmsg.getText() + "\n");
                    break;
                case Message.CONNECT:
                    //Send login nickname and wait for permission to join
                    //if message code is 1, nickname is a duplicate and must be changed, then login again
                    //(send call is inside button event)
                    ConnectMessage comsg = gson.fromJson(json, ConnectMessage.class);

                    if(comsg.getCode() == 1)
                    {
                        //Show error
                        ChatGUI.getInstance().getLoginErrorLabel().setVisible(true);
                    }
                    else
                    {
                        //Save nickname
                        nickname = ChatGUI.getInstance().getLoginField().getText();

                        //Hide this panel and show main panel
                        ChatGUI.getInstance().getLoginPanel().setVisible(false);
                        ChatGUI.getInstance().getMainPanel().setVisible(true);
                    }
                    break;
                case Message.USER_LIST:
                    UserListMessage umsg2 = gson.fromJson(json, UserListMessage.class);
                    fillUserList(umsg2);
                    break;
            }
        } 
        catch (JsonSyntaxException | IOException e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    
    //Utility method to fill UI with nickname of users
    private void fillUserList(UserListMessage msg)
    {
        allUsers = msg.getUsers();
        String userListText = "Connected users:\n";

        for (String user : allUsers)
        {
            if(user.equals(nickname))
                userListText += user + " (YOU)\n";
            else
                userListText += user + "\n";
        }
        ChatGUI.getInstance().getUserList().setText(userListText);
    }
    
    /**
     * Close connection with server
     */
    public void close()
    {
        try
        {
            System.out.println("CLIENT chiude connessione");
            receiverThread.interrupt();
            socket.close();
            System.exit(0);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
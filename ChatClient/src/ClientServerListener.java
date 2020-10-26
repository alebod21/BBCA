import java.io.*;
import java.net.Socket;

public class ClientServerListener implements Runnable {
    private ObjectInputStream socketIn;
    private ChatClient client;
    private PrintStream visibleOut;



    public ClientServerListener(Socket socketIn, ChatClient client, PrintStream visibleOut) throws Exception{
        this.socketIn = new ObjectInputStream(socketIn.getInputStream());
        this.client = client;
        this.visibleOut = visibleOut;
    }

    @Override
    public void run() {
        try {
            String incoming = "";

            while( (incoming = ((ChatMessage)(socketIn.readObject())).getMessage()) != null) {

                System.out.println("client server listener incoming: " + incoming);

                if(incoming.startsWith("CHAT")){
                    visibleOut.println(incoming.substring(4));
                }
                else if(incoming.startsWith("PCHAT")) {
                    visibleOut.println(incoming.substring(5));
                }
                else if(incoming.startsWith("SERVER")){
                    visibleOut.println("Server: " + incoming.substring(6));
                }
                else if(incoming.startsWith("KICK")){
                    visibleOut.println("Server: A vote to kick user: "+incoming.substring(4) + " has begun. You have 30 seconds to vote yes (/y) or no (/n).");
                }
                else if(incoming.startsWith("SUBMITNAME")){
                    visibleOut.println("Chat session has started - enter a user name: ");
                   client.sendName();
                }
                else if(incoming.startsWith("WELCOME")){
                    visibleOut.println(incoming.substring(7) + " has joined.");
                }
                else if(incoming.startsWith("ACCEPTED")){
                    client.nameReceived();
                }
                else if(incoming.startsWith("EXIT")){
                    visibleOut.println(incoming.substring(4)+" has left.");
                }

            }
        } catch (Exception ex) {
            visibleOut.println("Exception caught in listener - " + ex);
            ex.printStackTrace();

        } finally{
            visibleOut.println("Client Listener exiting");
        }
    }
}
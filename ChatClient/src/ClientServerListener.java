import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class ClientServerListener implements Runnable {
    private BufferedReader socketIn;
    private ChatClient client;



    public ClientServerListener(Socket socketIn,ChatClient client) throws Exception{
        this.socketIn = new BufferedReader(new InputStreamReader(socketIn.getInputStream()));
        this.client = client;
    }

    @Override
    public void run() {
        try {
            String incoming = "";

            while( (incoming = socketIn.readLine()) != null) {

                if(incoming.startsWith("CHAT")){
                    System.out.println(incoming.substring(4));
                }
                if(incoming.startsWith("SERVER")){
                    System.out.println("Server: " + incoming.substring(6));
                }
                if(incoming.startsWith("KICK")){
                    System.out.println("A vote to kick user: "+incoming.substring(4) + " has begun. You have 30 seconds to vote yes (/y) or no (/n).");
                }
                if(incoming.startsWith("SUBMITNAME")){
                    System.out.print("Chat session has started - enter a user name: ");
                   client.sendName();
                }
                if(incoming.startsWith("WELCOME")){
                    System.out.println(incoming.substring(7) + " has joined.");
                }
                if(incoming.startsWith("ACCEPTED")){
                    client.nameRecieved();
                }

            }
        } catch (Exception ex) {
            System.out.println("Exception caught in listener - " + ex);
            ex.printStackTrace();

        } finally{
            System.out.println("Client Listener exiting");
        }
    }
}
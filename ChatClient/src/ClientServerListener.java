import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

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
                else if(incoming.startsWith("PCHAT")) {
                    System.out.println(incoming.substring(5));
                }
                else if(incoming.startsWith("SERVER")){
                    System.out.println("Server: " + incoming.substring(6));
                }
                else if(incoming.startsWith("KICK")){
                    System.out.println("Server: A vote to kick user: "+incoming.substring(4) + " has begun. You have 30 seconds to vote yes (/y) or no (/n).");
                }
                else if(incoming.startsWith("SUBMITNAME")){
                    System.out.print("Chat session has started - enter a user name: ");
                   client.sendName();
                }
                else if(incoming.startsWith("WELCOME")){
                    System.out.println(incoming.substring(7) + " has joined.");
                }
                else if(incoming.startsWith("ACCEPTED")){
                    client.nameReceived();
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
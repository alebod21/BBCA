import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class ClientServerListener implements Runnable {
    // private BufferedReader socketIn;
    private ObjectInputStream socketIn;
    private ChatClient client;
    private PrintStream visibleOut;
    // private ObjectOutputStream visibleOut;

    // visibleOut was type PrintStream
    public ClientServerListener(Socket in, ChatClient client, PrintStream visibleOut) throws Exception {
        // this.socketIn = new BufferedReader(new
        // InputStreamReader(socketIn.getInputStream()));
        this.socketIn = new ObjectInputStream(in.getInputStream());
        this.client = client;
        this.visibleOut = visibleOut;
    }

    @Override
    public void run() {
        try {
            // String incoming = "";
            String incoming = null;

            // while( (incoming = socketIn.readLine() != null) {
            
            while ((incoming = ((ChatMessage) socketIn.readObject()).getMessage()) != null) {
                System.out.println("bababooey");
                System.out.println(incoming);
                // System.out.println(((ChatMessage) socketIn.readObject()).getMessage());

                // System.out.println("client server listener incoming: " + incoming);

                if (incoming.startsWith("CHAT")) {
                    visibleOut.println(incoming.substring(4));
                    // visibleOut.writeObject(new ChatMessage(incoming.substring(4)));
                } else if (incoming.startsWith("PCHAT")) {
                    visibleOut.println(incoming.substring(5));
                    // visibleOut.writeObject(new ChatMessage(incoming.substring(5)));
                } else if (incoming.startsWith("SERVER")) {
                    visibleOut.println("Server: " + incoming.substring(6));
                    // visibleOut.writeObject(new ChatMessage(incoming.substring(6)));
                } else if (incoming.startsWith("KICK")) {
                    visibleOut.println("Server: A vote to kick user: "+incoming.substring(4) + " has begun. You have 30 seconds to vote yes (/y) or no (/n).");
                    // visibleOut.writeObject(new ChatMessage("Server: A vote to kick user: " + incoming.substring(4) + " has begun. You have 30 seconds to vote yes (/y) or no (/n)."));
                } else if (incoming.startsWith("SUBMITNAME")) {
                    visibleOut.println("Chat session has started - enter a user name: ");
                    // visibleOut.writeObject(new ChatMessage("Chat session has started - enter a username"));
                    client.sendName();
                } else if (incoming.startsWith("WELCOME")) {
                    visibleOut.println(incoming.substring(7) + " has joined.");
                    // visibleOut.writeObject(new ChatMessage(incoming.substring(7) + " has joined."));
                } else if (incoming.startsWith("ACCEPTED")) {
                    client.nameReceived();
                } else if (incoming.startsWith("EXIT")) {
                    visibleOut.println(incoming.substring(4)+" has left.");
                    // visibleOut.writeObject(new ChatMessage(incoming.substring(4) + " has left."));
                }
                incoming = ((ChatMessage) socketIn.readObject()).getMessage();
                System.out.println(incoming);

            }
        } catch (Exception ex) {
            visibleOut.println("Exception caught in listener - " + ex);

        } finally {
            visibleOut.println("Client Listener exiting");
        }
    }
}
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static Socket socket;
    private static PrintWriter out;
    private Scanner userInput;
    private boolean serverAccepted;

    public ChatClient(String ip, int port, Scanner userIn) throws Exception{
        socket = new Socket(ip, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        userInput = userIn;
    }

    public void startClient() throws Exception {

        // start a thread to listen for server messages
        ClientServerListener listener = new ClientServerListener(socket,this);
        Thread t = new Thread(listener);
        t.start();


        String line = userInput.nextLine().trim();
        while(!line.toLowerCase().startsWith("/quit")) {

            if(!serverAccepted){
                out.println(String.format("NAME%s",line));
            }

            else{
            String msg = String.format("CHAT%s", line);
            out.println(msg);
            }

            line = userInput.nextLine().trim();
        }
        out.println("QUIT");
        out.close();
        userInput.close();
        socket.close();
        
    }

    public void sendName(){
        serverAccepted = false;
    }
    public void nameRecieved(){
        serverAccepted = true;
    }
}

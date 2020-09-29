import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static Socket socket;
    private static PrintWriter out;
    
    public static void main(String[] args) throws Exception {

        Scanner userInput = new Scanner(System.in);
        
        System.out.println("What's the server IP? ");
        String serverip = userInput.nextLine();
        System.out.println("What's the server port? ");
        int port = userInput.nextInt();
        userInput.nextLine();

        socket = new Socket(serverip, port);
        out = new PrintWriter(socket.getOutputStream(), true);

        // start a thread to listen for server messages
        ClientServerListener listener = new ClientServerListener(socket);
        Thread t = new Thread(listener);
        t.start();

        //to be rewritten as a server/client communication
        System.out.print("Chat sessions has started - enter a user name: ");
        String name = userInput.nextLine().trim();
        out.println(name); //out.flush();

        //change to allow for other headers as needed
        String line = userInput.nextLine().trim();
        while(!line.toLowerCase().startsWith("/quit")) {
            String msg = String.format("CHAT %s", line); 
            out.println(msg);
            line = userInput.nextLine().trim();
        }
        out.println("QUIT");
        out.close();
        userInput.close();
        socket.close();
        
    }
}

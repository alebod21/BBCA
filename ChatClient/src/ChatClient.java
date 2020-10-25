import java.io.BufferedReader;
import java.io.ObjectOutput;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.ObjectOutputStream;

public class ChatClient {
    private static Socket socket;
    // private static PrintWriter out;
    private static ObjectOutputStream out;
    private Scanner userInput;
    private boolean serverAccepted;

    public ChatClient(String ip, int port, Scanner userIn) throws Exception{
        socket = new Socket(ip, port);
        // out = new PrintWriter(socket.getOutputStream(), true);
        out = new ObjectOutputStream(socket.getOutputStream());
        userInput = userIn;
    }

    // public void startClient(PrintStream visibleOut) throws Exception {
    public void startClient(PrintStream visibleOut) throws Exception {    

        // start a thread to listen for server messages
        ClientServerListener listener = new ClientServerListener(socket,this,visibleOut);
        Thread t = new Thread(listener);
        t.start();


        String line = userInput.nextLine().trim();
        while(!line.toLowerCase().startsWith("/quit")) {
            // System.out.println(line);

            if(!serverAccepted){
                // out.println(String.format("NAME%s",line));
                out.writeObject(new ChatMessage(String.format("NAME%s", line)));
            }

            //pchat
            else if (line.startsWith("@")) {

                ArrayList<String> pchatNames = new ArrayList<>();
                String message = "";

                String[] words = line.split(" ");
                for(int i = 0; i < words.length; i++){
                    if(words[i].strip().startsWith("@")){
                        pchatNames.add(words[i].strip().substring(1));
                    }
                    else{
                        for(int r = i; r < words.length; r++){
                            message += words[r]+" ";
                        }
                        break;
                    }
                }

                for (String name : pchatNames){
                    System.out.println(name);
                }

                for(String name : pchatNames){
                String msg = String.format("PCHAT%s%s",name,message);
                out.println("PCHAT"+ name+" " + message);
                }
            }


            else if(line.equals("/y")){
                // out.println("VOTEy");
                out.writeObject(new ChatMessage("VOTEy"));
            }
            else if(line.equals("/n")){
                // out.println("VOTEn");
                out.writeObject(new ChatMessage("VOTEn"));
            }
            else if(line.startsWith("/ban") && line.length() > 5){
                // out.println("BAN"+line.substring(5));
                out.writeObject(new ChatMessage("BAN" + line.substring(5)));
            }
            else if(line.startsWith("/whoishere")){
                // out.println("WHO");
                out.writeObject(new ChatMessage("WHO"));
            }
            else{
                System.out.println("working good!");
                String msg = String.format("CHAT%s", line);
                // out.println(msg);
                out.writeObject(new ChatMessage(msg));
            }
            out.flush();

            line = userInput.nextLine().trim();
        }
        // out.println("QUIT");
        out.writeObject(new ChatMessage("QUIT"));
        out.close();
        userInput.close();
        socket.close();
        
    }

    public void sendName(){
        serverAccepted = false;
    }
    public void nameReceived(){
        serverAccepted = true;
    }
}

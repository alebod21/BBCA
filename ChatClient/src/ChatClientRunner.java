import java.io.ObjectOutputStream;
import java.util.Scanner;

public class ChatClientRunner {

    public static void main(String[] args) {

        Scanner userInput = new Scanner(System.in);
        ChatClient client;

        //get IP and port from user
        System.out.println("What's the server IP? ");
        String serverip = userInput.nextLine();
        System.out.println("What's the server port? ");
        int port = userInput.nextInt();
        userInput.nextLine();
        
        try{
        client = new ChatClient(serverip,port,userInput);
        client.startClient(System.out);}catch (Exception ex){
            System.out.println(ex.getMessage());
            System.out.println(ex.getStackTrace());
        }
    }
}

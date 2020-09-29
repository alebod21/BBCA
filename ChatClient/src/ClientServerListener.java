import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientServerListener implements Runnable {
    private BufferedReader socketIn;



    public ClientServerListener(Socket socketIn) throws Exception{
        this.socketIn = new BufferedReader(new InputStreamReader(socketIn.getInputStream()));
    }

    @Override
    public void run() {
        try {
            String incoming = "";

            while( (incoming = socketIn.readLine()) != null) {
                //handle different headers
                //WELCOME
                //CHAT
                //EXIT
                System.out.println(incoming);
            }
        } catch (Exception ex) {
            System.out.println("Exception caught in listener - " + ex);
        } finally{
            System.out.println("Client Listener exiting");
        }
    }
}
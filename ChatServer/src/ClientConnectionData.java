import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnectionData {
    private Socket socket;
    // private BufferedReader input;
    private ObjectInputStream input;
    //private PrintWriter out;
    private ObjectOutputStream out;
    private String name;
    private String userName;

    public ClientConnectionData(Socket socket) throws Exception{
        this.socket = socket;
        // this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.input = new ObjectInputStream(socket.getInputStream());
        // this.out = new PrintWriter(socket.getOutputStream(), true);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.name = socket.getInetAddress().getHostName();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    // public BufferedReader getInput() {
    public ObjectInputStream getInput(){
        return input;
    }

    // public void setInput(BufferedReader input) {
    public void setInput(ObjectInputStream input){
        this.input = input;
    }

    //Returned PrintWriter
    public ObjectOutputStream getOut() {
        return out;
    }

    //takes PrintWriter out
    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    
}

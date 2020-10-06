import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ChatServer {
    public static final int PORT = 54321;
    public static final ArrayList<ClientConnectionData> clientList = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(100);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            //server initialization
            System.out.println("Chat Server started.");
            System.out.println("Local IP: "
                    + Inet4Address.getLocalHost().getHostAddress());
            System.out.println("Local Port: " + serverSocket.getLocalPort());

            //adds clients
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.printf("Connected to %s:%d on local port %d\n", socket.getInetAddress(), socket.getPort(), socket.getLocalPort());

                    ClientConnectionData client = new ClientConnectionData(socket);
                    synchronized (clientList) {
                        clientList.add(client);
                    }

                    System.out.println("added client " + client.getName());

                    //handle client business in another thread
                    pool.execute(new ServerClientHandler(client));
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }
}
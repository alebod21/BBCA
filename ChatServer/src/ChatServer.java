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
    public static final int PORT = 59010;
    public static final ArrayList<ClientConnectionData> clientList = new ArrayList<>();
    //private static final ArrayList<Integer> bannedIPs = new ArrayList<>();
    protected static final ArrayList<String> bannedNames = new ArrayList<>();
    public static Boolean voteInProgress = new Boolean(false);
    public static Integer voteYes = 0;
    public static Integer voteNo = 0;
    public static ChatServer theServer;

    public ChatServer(){
        if(theServer != null);
        else theServer = this;
    }

    public void startServer() throws Exception {
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

    public void startBan(String user){
        voteKicker kicker = new voteKicker(user);
        Thread kickThread = new Thread(kicker);
        kickThread.start();
    }

    class voteKicker implements Runnable{

        private String name;
        public voteKicker(String name){this.name = name;}

        @Override
        public void run() {
            System.out.println("Vote kick started");
            int timeStart = (int)(System.currentTimeMillis()/1000);
            voteNo = 0;
            voteYes = 0;
            voteInProgress = true;

            int currentUsers = 0;
            synchronized (clientList){
            currentUsers = clientList.size();
            }

            while(((int)(System.currentTimeMillis()/1000) - timeStart < 30)){
                //lol just a countdown, what can ya do?
            }

            voteInProgress = false;

            if(voteYes > currentUsers/2){
                ServerClientHandler.publicBroadcast("SERVERVote succeeded, user " + name + " will be banned.");
                bannedNames.add(name);

                synchronized (clientList){
                    for(ClientConnectionData c : clientList){
                        if(c.getUserName().equals(name)){
                            try{
                            c.getSocket().close();}catch (Exception ex){}
                            clientList.remove(c);
                        }
                    }
                }
            }

            else if(voteNo > currentUsers/2){
                ServerClientHandler.publicBroadcast("SERVERVote Failed, too many users voted no.");
            }
            else{
                ServerClientHandler.publicBroadcast("SERVERVote Failed, timed out.");
            }

            System.out.println("Vote kick ended.");
        }
    }
}


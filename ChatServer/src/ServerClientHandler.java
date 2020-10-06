import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

class ServerClientHandler implements Runnable{
    ClientConnectionData client;

    ServerClientHandler(ClientConnectionData client) {
        this.client = client;
    }

    public void broadcast(String msg) {
        try {
            System.out.println("Broadcasting -- " + msg);
            synchronized (ChatServer.clientList) {
                for (ClientConnectionData c : ChatServer.clientList){
                    if(c.getUserName()==null || c.equals(client) && !msg.startsWith("WELCOME")) {
                    } else {
                        c.getOut().println(msg);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("broadcast caught exception: " + ex);
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            BufferedReader in = client.getInput();

            String userName;
            boolean nameNotUsed;

            while (true){
                client.getOut().println("SUBMITNAME");
                userName = client.getInput().readLine();
                nameNotUsed = true;

                //regex code borrowed from https://www.techiedelight.com/check-string-contains-alphanumeric-characters-java/
                if(userName.startsWith("NAME") && userName.length() > 4 && userName.matches("^[a-zA-Z0-9]*$")){
                    userName = userName.substring(4);

                    synchronized (ChatServer.clientList){
                    for(ClientConnectionData c : ChatServer.clientList){
                        if(!c.equals(client) && c.getUserName().equals(userName)){nameNotUsed = false; break;}
                    }}
                    if(nameNotUsed)break;
                }
            }

            //set client's username and notify client of acceptance
            client.setUserName(userName);
            client.getOut().print("ACCEPTED");
            //notify all that client has joined
            broadcast(String.format("WELCOME %s", client.getUserName()));

            String incoming = "";

            while( (incoming = in.readLine()) != null) {

                if (incoming.toUpperCase().startsWith("CHAT")) {
                    String chat = incoming.substring(4).trim();
                    if (chat.length() > 0) {
                        String msg = String.format("CHAT %s: %s", client.getUserName(), chat);
                        broadcast(msg);
                    }
                }

                else if (incoming.startsWith("QUIT")){
                    break;
                }
            }



        } catch (Exception ex) {
            if (ex instanceof SocketException) {
                System.out.println("Caught socket ex for " +
                        client.getName());
            } else {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        } finally {
            //Remove client from clientList, notify all
            synchronized (ChatServer.clientList) {
                ChatServer.clientList.remove(client);
            }
            System.out.println(client.getName() + " has left.");
            broadcast(String.format("EXIT %s", client.getUserName()));
            try {
                client.getSocket().close();
            } catch (IOException ex) {}
        }
    }
}
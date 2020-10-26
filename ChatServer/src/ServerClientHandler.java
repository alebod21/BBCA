import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;

class ServerClientHandler implements Runnable{
    ClientConnectionData client;
    private boolean hasVoted;

    ServerClientHandler(ClientConnectionData client) {
        this.client = client;
    }

    protected void newVote(){
        hasVoted = false;
    }

    public static void publicBroadcast(String msg) {
        try {
            System.out.println("Broadcasting -- " + msg);
            synchronized (ChatServer.clientList) {
                for (ClientConnectionData c : ChatServer.clientList){
                    if(c.getUserName()!=null) {
                        c.getOut().writeObject(new ChatMessage((msg)));
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("broadcast caught exception: " + ex);
            ex.printStackTrace();
        }
    }

    public void broadcast(String msg) {
        try {
            System.out.println("Broadcasting -- " + msg);
            synchronized (ChatServer.clientList) {
                for (ClientConnectionData c : ChatServer.clientList){
                    if(c.getUserName()==null || c.equals(client) && !msg.startsWith("WELCOME")) {
                    } else {
                        c.getOut().writeObject(new ChatMessage((msg)));
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("broadcast caught exception: " + ex);
            ex.printStackTrace();
        }
    }

    public void privateBroadcast(String msg, String recipient) {
        try {
            System.out.println("Broadcasting -- " + msg);
            synchronized (ChatServer.clientList) {
                for(ClientConnectionData c : ChatServer.clientList) {
                    if(c.getUserName().equals(recipient)){
                        c.getOut().writeObject(new ChatMessage((msg)));
                    }
                }
            }    
            // client.getOut().println("PRIVATE: " + msg);
        } catch (Exception ex) {
            System.out.println("broadcast caught exception: " + ex);
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {

            String userName;
            boolean nameNotUsed;

            while (true){
                client.getOut().writeObject(new ChatMessage(("SUBMITNAME")));
                userName = ((ChatMessage)(client.getInput().readObject())).getMessage();
                nameNotUsed = true;

                //regex code borrowed from https://www.techiedelight.com/check-string-contains-alphanumeric-characters-java/
                if(userName.startsWith("NAME") && userName.length() > 4 && userName.matches("^[a-zA-Z0-9]*$")){
                    userName = userName.substring(4);

                    if(ChatServer.bannedNames.contains(userName)){
                        client.getOut().writeObject(new ChatMessage(("SERVERThat User has Been Banned.")));
                        nameNotUsed = false;
                    }

                    synchronized (ChatServer.clientList){
                    for(ClientConnectionData c : ChatServer.clientList){

                        if((c.getUserName() != null) && !c.equals(client) && c.getUserName().equals(userName)){nameNotUsed = false; break;}
                    }}
                    if(nameNotUsed)break;
                }
            }

            //set client's username and notify client of acceptance
            client.setUserName(userName);
            client.getOut().writeObject(new ChatMessage(("ACCEPTED")));
            client.getOut().flush();
            //notify all that client has joined
            broadcast(String.format("WELCOME %s", client.getUserName()));

            String allNames = "CHATusers - ";
            synchronized (ChatServer.clientList){
                for(ClientConnectionData c : ChatServer.clientList){
                    if(c.getUserName() != null)
                    allNames += c.getUserName() + " ";}
                }
            broadcast(allNames);
            client.getOut().writeObject(new ChatMessage((allNames)));

            String incoming = "";

            while( (incoming = ((ChatMessage)(client.getInput().readObject())).getMessage()) != null) {


                if(!ChatServer.voteInProgress)newVote();

                if (incoming.toUpperCase().startsWith("CHAT")) {
                    String chat = incoming.substring(4).trim();
                    if (chat.length() > 0) {
                        String msg = String.format("CHAT %s: %s", client.getUserName(), chat);
                        broadcast(msg);
                    }
                }

                else if(incoming.toUpperCase().startsWith("PCHAT")) {
                    String chat = incoming.substring(5).trim();
                    String recipient = chat.split(" ")[0];
                    chat = chat.substring(chat.split(" ")[0].length()+1);
                    if (chat.length() > 0) {
                        String msg = String.format("PCHAT %s (private): %s", client.getUserName(), chat);
                        privateBroadcast(msg, recipient);
                    }
                }

                else if(!hasVoted && ChatServer.voteInProgress && incoming.equals("VOTEy")){
                   synchronized (ChatServer.voteYes){
                       ChatServer.voteYes++;
                       hasVoted = true;
                   }
                }

                else if(!hasVoted && ChatServer.voteInProgress && incoming.equals("VOTEn")){
                    synchronized (ChatServer.voteNo){
                        ChatServer.voteNo++;
                        hasVoted = true;
                    }
                }

                else if(incoming.equals("WHO")){
                    String names = "CHATusers - ";
                    synchronized (ChatServer.clientList){
                        for(ClientConnectionData c : ChatServer.clientList){
                            if(c.getUserName() != null)
                            names += c.getUserName() + " ";
                        }
                    }
                    client.getOut().writeObject(new ChatMessage((names)));
                }

                else if(incoming.toUpperCase().startsWith("BAN") && incoming.length() > 3){
                    boolean nameexists = false;

                    synchronized (ChatServer.clientList){
                        for(ClientConnectionData c : ChatServer.clientList){
                            if(incoming.substring(3).equals(c.getUserName())){
                                nameexists = true;
                                break;
                            }
                        }
                    }

                    if(!nameexists){
                        client.getOut().writeObject(new ChatMessage(("SERVERCannot vote to ban; user doesn't exist")));
                    }
                    else if(ChatServer.voteInProgress){
                        client.getOut().writeObject(new ChatMessage(("SERVERCannot vote to ban; vote already in progress.")));
                    }
                    else{
                        publicBroadcast("KICK"+incoming.substring(3));
                        ChatServer.theServer.startBan(incoming.substring(3));
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

//            String allNames = "CHATusers - ";
//            synchronized (ChatServer.clientList){
//                for(ClientConnectionData c : ChatServer.clientList){
//                    allNames += c.getUserName() + " ";}
//            }
//            broadcast(allNames);

            try {
                client.getSocket().close();
            } catch (IOException ex) {}
        }
    }
}
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatClientGuiRunner extends Application{

    public static void main(String[] args) {
        launch(args);
    }


    HBox messageArea = new HBox();
    TextField messageField = new TextField();
    Button sendButton= new Button("Send");
    TextArea userAreaText = new TextArea();
    TextArea chatText = new TextArea();
    VBox userArea = new VBox();
    HBox banBox = new HBox();
    Button banButton = new Button("Ban");

    public static final ObservableList<String> USERS = FXCollections.observableArrayList();
    ComboBox userBox = new ComboBox(USERS);


    private static final String USERS_HEADER = "               Users:               \n\n";
    private ChatClient client;
    public static final String SERVER_IP = "localhost";
    public static final int SERVER_PORT = 54321;

    //I/O stuff. jeebus crust this hurt my head to write
    public static PipedInputStream textIn = new PipedInputStream();
    public static PipedOutputStream textOut = new PipedOutputStream();
    public static Scanner messageScanner = new Scanner(textIn);
    public static PrintStream messageWriter = new PrintStream(textOut);
    public static PipedInputStream chatIn = new PipedInputStream();
    public static PipedOutputStream chatOut = new PipedOutputStream();
    public static Scanner chatReader = new Scanner(chatIn);
    public static PrintStream chatWriter = new PrintStream(chatOut);




    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("Better Basic Chat App");

        BorderPane layout = new BorderPane();

        messageArea.setAlignment(Pos.BOTTOM_RIGHT);
        messageArea.getChildren().add(messageField);
        messageField.setPrefWidth(455.0);
        messageArea.getChildren().add(sendButton);
        textIn.connect(textOut);
        chatIn.connect(chatOut);
        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                chatText.appendText( messageField.getText() + "\n");
                 messageWriter.println(messageField.getText());
                messageField.setText("");
            }
        });
        userAreaText.setText(USERS_HEADER);
        userAreaText.setMaxWidth(150);
        banBox.getChildren().add(banButton);
        banButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                messageWriter.println("/ban " + userBox.getValue());
            }
        });
        userBox.setMaxWidth(115);
        userBox.setPrefWidth(115);

        banBox.getChildren().add(userBox);
        chatText.setEditable(false);
        chatText.setWrapText(true);
        userArea.getChildren().add(banBox);
        userArea.getChildren().add(userAreaText);
        userArea.setMaxWidth(150);
        userAreaText.setPrefHeight(550);
        userAreaText.setWrapText(true);


        layout.setBottom(messageArea);
        layout.setCenter(chatText);
        layout.setRight(userArea);

        Scene scene = new Scene(layout,500,600);
        primaryStage.setScene(scene);
        primaryStage.show();


        //starting servery things
        Thread chatBoxThread = new Thread(new TextFieldUpdater(chatText, userAreaText,chatReader));
        chatBoxThread.start();
        Thread IOThread = new Thread(new IORunner());
        IOThread.start();


    }

    class IORunner implements Runnable{

        public void run(){
//            chatText.appendText("What is the Server IP? \n");
//            String IP = messageScanner.nextLine();
//            chatText.appendText("What is the port?\n");
//            int port = Integer.parseInt(messageScanner.nextLine());

//             try{
//                 client = new ChatClient(SERVER_IP,SERVER_PORT, messageScanner);
//                 client.startClient(chatWriter);}catch (Exception ex){
//                 System.out.println(ex.getMessage());
//                 System.out.println(ex.getStackTrace());
//             }
//         }
//     }

//     class TextFieldUpdater implements Runnable{

//         private TextArea area;
//         private Scanner incomingTextReader;
//         private TextArea users;
//         private ArrayList<String> usernames = new ArrayList<>();
//         public TextFieldUpdater(TextArea area, TextArea users, Scanner textReader) {
//             this.area = area;
//             incomingTextReader = textReader;
//             this.users = users;
//         }

//         public void run(){
//             while(incomingTextReader.hasNext()){

                String incoming = incomingTextReader.nextLine();

                if(!incoming.startsWith("users - ")){
                area.appendText(incoming.strip() + "\n");}

                if(incoming.endsWith("has left.") && !incoming.contains(":")){
                    System.out.println("read someone left: " + incoming);
                    System.out.println("Their name was: "+ incoming.split(" ")[1]);
                    USERS.remove(incoming.split(" ")[1]);
                    users.setText(USERS_HEADER);
                    for(String name:USERS){
                        users.appendText(name+"\n");
                    }
                }

                if(incoming.endsWith("has joined.") && !incoming.contains(":")){
                    System.out.println("read someone joined: " + incoming);
                    USERS.add(incoming.split(" ")[0]);
                    users.setText(USERS_HEADER);
                    for(String name:USERS){
                        users.appendText(name+"\n");
                    }
                }


                if(incoming.startsWith("users - ")){
                    users.setText(USERS_HEADER);
                    String[] newnames = incoming.split("\\s+");
                    USERS.clear();

                    for(int i = 2; i < newnames.length; i++){
                        USERS.add(newnames[i].strip());
                        users.appendText(newnames[i]+"\n");
                    }
                }
            }
        }
    }
}



















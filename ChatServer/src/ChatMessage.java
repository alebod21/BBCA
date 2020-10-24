import java.io.Serializable;

public class ChatMessage implements Serializable{
    public static final long serialVersionUID = 1L;
    private String message = "";

    public ChatMessage(){}

    public ChatMessage(String Message){
        this.message = Message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    
}

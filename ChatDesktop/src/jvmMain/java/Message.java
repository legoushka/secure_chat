import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    private MessageType messageType;
    private String messageContent;
    private User user;
    private List<User> userList;

    public Message(MessageType messageType, String messageContent, User user, List<User> userList) {
        this.messageType = messageType;
        this.messageContent = messageContent;
        this.user = user;
        this.userList = userList;
    }

    public Message(MessageType messageType){ //Login request
        this.messageType = messageType;
    }

    public User getUser() {
        return user;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
    public MessageType getMessageType() {
        return messageType;
    }

    public String getContent() {
        return messageContent;
    }

    public List<User> getUserList() {
        return userList;
    }

}

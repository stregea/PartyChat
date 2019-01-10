package Chat;

import java.awt.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Observable;

/**
 * The ChatRoom Object. Contains a String that will hold all of the
 * messages sent from client-to-server-to-client.
 *
 * @author Samuel Tregea
 * <p>
 * Last Date Modified: January 2, 2019
 */
public class ChatRoom extends Observable implements Serializable {

    /**
     * String that contains the entire chat
     */
    private String chat_messages;

    /**
     * Default constructor
     */
    public ChatRoom() {
        this.chat_messages = "";
    }

    /**
     * Create the ChatRoom
     *
     * @param chat_messages the messages to be places in the chat
     */
    public ChatRoom(String chat_messages) {
        this.chat_messages = chat_messages;
    }

    /**
     * Telling the ChatRoom that there was a new
     * message that has been sent
     */
    public void messageSent() {
        super.setChanged();
        super.notifyObservers();
    }

    /**
     * Reformat and add a message to the String of the chat
     *
     * @param message - the message to be added
     */
    public void addMessage(UserMessage message) {
        String currentTime = new SimpleDateFormat("HH:mm:ss").format(message.getTime());
        this.chat_messages += currentTime + " | " + message.getUsername() + ": " + message.getMessage();
    }

    /**
     * Set the message
     *
     * @param message the message to be set
     */
    public void setMessage(String message) {
        this.chat_messages = message;
    }

    /**
     * Retrieve the chat messages
     *
     * @return the chat
     */
    public String getMessages() {
        return this.chat_messages;
    }

    /**
     * Automatically display the chat when a ChatRoom object is printed
     *
     * @return the chat's String
     */
    @Override
    public String toString() {
        return getMessages();
    }
}

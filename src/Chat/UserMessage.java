package Chat;

import java.io.Serializable;

/**
 * This class will create a Chat.UserMessage object that will
 * contain the information of the message itself, the user who
 * sent the message, and the time the message was sent.
 *
 * @author Samuel Tregea
 * <p>
 * Last Date Modified: January 2, 2019
 */
public class UserMessage implements Serializable {

    /**
     * The username of the client
     **/
    private Users user;


    /**
     * The time the message was sent
     **/
    private long time;

    /**
     * The contents of the message
     **/
    private String message_contents;

    /**
     * The Default Constructor
     */
    public UserMessage() {

    }

    /**
     * Create the message
     *
     * @param user             - the user who sent the message
     * @param time             - the time the message was sent
     * @param message_contents - the actual message itself
     */
    public UserMessage(Users user, long time, String message_contents) {
        this.user = user;
        this.time = time;
        this.message_contents = message_contents;
    }


    /**
     * Retrieve the username of the user who sent the message
     *
     * @return the username
     */
    public String getUsername() {
        return this.user.getUsername();
    }

    /**
     * Retrieve the time the message was sent
     *
     * @return the time
     */
    public long getTime() {
        return this.time;
    }

    /**
     * Retrieve the message
     *
     * @return the message that was sent
     */
    public String getMessage() {
        return this.message_contents;
    }

    @Override
    public String toString() {
        return getMessage();
    }
}

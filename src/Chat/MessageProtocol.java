package Chat;

/**
 * The MessageProtocol interface provides constants for all of the
 * messages that are communicated between the server and the client's.
 *
 * @author Samuel Tregea
 * <p>
 * Last Date Modified: January 2, 2019
 */
public interface MessageProtocol {

    /**
     * Message sent from the server to the client
     * that will tell the client that they have successfully
     * logged in.
     */
    String LOGIN_SUCCESS = "LOGIN_SUCCESS";

    /**
     * Message sent from the server to the client
     * that will tell the client that they have to choose
     * another username.
     */
    String INVALID_USERNAME = "INVALID_USERNAME";

    /**
     * Message sent from the server to the client
     * that will tell the client there was an error.
     */
    String ERROR = "ERROR";

    /**
     * Message sent from the server to the client
     * that will tell the client that they have to
     * choose another username due to that name already
     * being taken.
     */
    String USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
}

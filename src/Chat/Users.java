package Chat;

import java.io.Serializable;

/**
 * The user themselves. User will create a
 * username and this class will store it.
 *
 * @author Samuel Tregea
 * <p>
 * Last Date Modified: January 2, 2019
 */
public class Users implements Serializable {

    /**
     * The username of the user
     */
    private String username;

    /**
     * Default Constructor
     */
    public Users() {

    }

    /**
     * Create the user with a specified username
     *
     * @param username - the username of the user
     */
    public Users(String username) {
        this.username = username;
    }

    /**
     * Retrieve the username
     *
     * @return the username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Set the username
     *
     * @param username - the username to be changed to
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Create a String version of this object
     *
     * @return the String form.
     */
    @Override
    public String toString() {
        return this.username;
    }
}

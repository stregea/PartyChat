package Chat.Client.CommandLine;

import Chat.ChatRoom;
import Chat.Client.Network.ServerConnection;
import Chat.MessageRequest;
import Chat.UserMessage;
import Chat.Users;

import java.util.*;

/**
 * Application that allows for a user to communicate using commandline.
 * Mainly meant for the admins use, but any user can use it.
 *
 * @author Samuel Tregea
 * <p>
 * Last Date Modified: January 2, 2019
 */
public class ConsoleApplication implements Observer {

    /**
     * Allows for the client to connect to the server
     */
    private ServerConnection serverConnection;

    /**
     * The ChatRoom containing the chat room messages
     * and messages from the server.
     */
    private ChatRoom chat;

    /**
     * The user communicating with the server
     */
    private Users user;

    /**
     * Run the admin program
     * @param args -  not used.
     */
    public static void main(String[] args) {
        ConsoleApplication c = new ConsoleApplication("ADMIN", 12345, "localhost");
    }

    /**
     * Constructor that will take in the user information, and then send it
     * to the server. If the chat room exists, start the program, otherwise,
     * do nothing.
     *
     * @param username   - the username
     * @param port - the port the server is on
     * @param host   - the IP of the host / localhost
     */
    public ConsoleApplication(String username, int port, String host) {
        this.user = new Users(username);
        this.serverConnection = new ServerConnection(host, port, this.user);
        this.chat = serverConnection.getChatRoom();

        if (this.chat != null) {
            this.chat.addObserver(this);
            this.startChat();
        }
        else {
            System.out.println("Could not connect to server.");
        }
    }

    /**
     * returns the current time
     */
    private long getCurrentTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        return calendar.getTime().getTime();
    }

    /**
     * Print out the conversation
     */
    private void refresh() {
        System.out.println(chat);
    }

    /**
     * updates the observable
     */
    @Override
    public void update(Observable o, Object t) {
        assert o == this.chat : "Update from non-model Observable";

        refresh();
    }

    /**
     * Prompt the user to chat with other users.
     */
    private void startChat() {
        Scanner sc = new Scanner(System.in);
        while (serverConnection.isRunning()) {
            System.out.println("Enter a message to the server:");

            String clientMessage = sc.nextLine() + "\n";

            UserMessage message = new UserMessage(this.user, getCurrentTime(), clientMessage); // creating a new message object every time a user enters a message

            MessageRequest<UserMessage> req = new MessageRequest<>(MessageRequest.RequestType.SEND_MESSAGE, message);

            serverConnection.sendMessage(req);
        }
        sc.close();

    }

}

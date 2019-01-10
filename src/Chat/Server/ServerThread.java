package Chat.Server;

import Chat.MessageRequest;
import Chat.UserMessage;
import Chat.MessageProtocol;
import Chat.Users;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Thread that allows for the server to be multithreaded
 *
 * @author Samuel Tregea
 * <p>
 * Last Date Modified: January2, 2019
 */
public class ServerThread extends Thread {

    /**
     * The server
     */
    private ChatServer chatServer;

    /**
     * allows for sending out objects to clients
     **/
    private ObjectOutputStream out;

    /**
     * allows for reading in objects
     **/
    private ObjectInputStream in;

    /**
     * the socket that connects to the client
     **/
    private Socket socket;

    /**
     * the username of the client
     */
    private String username;

    /**
     * if server is running
     */
    private boolean serverIsRunning;

    /**
     * Create the thread
     *
     * @param s      - the socket
     * @param out    - the ObjectOutputStream
     * @param in     - the ObjectInputStream
     * @param server - the ChatServer itself
     */
    ServerThread(Socket s, ObjectOutputStream out, ObjectInputStream in, ChatServer server) {
        this.in = in;
        this.out = out;
        this.socket = s;
        this.serverIsRunning = true;
        chatServer = server;
    }

    /**
     * Run the thread and handle the messages from the client
     */
    @Override
    public void run() {
        if (serverIsRunning) {
            do {
                MessageRequest messageRequest = null;
                try {
                    messageRequest = (MessageRequest<?>) in.readUnshared();
                } catch (IOException e) {
                    System.out.println(username + " signed out");
                    this.chatServer.removeUser(username);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (messageRequest != null) {
                    handleMessageRequest(messageRequest);
                }
            } while (this.chatServer.userExists(this.username));
        }
    }

    /**
     * Function that will interpret the messages from the client
     *
     * @param messageRequest - the request being sent from the client
     */
    private void handleMessageRequest(MessageRequest<UserMessage> messageRequest){
        MessageRequest.RequestType request = messageRequest.getType();
        try{
            switch (request) {
                case SEND_MESSAGE:
                    this.chatServer.enterMessageChanges(messageRequest);
                    break;
                case LOGIN:
                    login(messageRequest);
                    break;
                case ERROR:
                    break;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Allowing the user to sign in
     *
     * @param messageRequest - the request the user is making to login
     * @throws IOException
     */
    private void login(MessageRequest<?> messageRequest) throws IOException {

        Users user = (Users) messageRequest.getData();

        this.username = user.getUsername();

        // if the user doesn't exist and their name is the empty string.
        if (!this.chatServer.userExists(this.username) && this.username.equals("")) {
            MessageRequest<?> unsuccessful_login = new MessageRequest<>(MessageRequest.RequestType.INVALID_USERNAME, MessageProtocol.INVALID_USERNAME);
            this.out.writeUnshared(unsuccessful_login);
            this.out.flush();
        }
        // the user enters a username that is already online
        else if (this.chatServer.userExists(this.username)) {
            MessageRequest<?> unsuccessful_login = new MessageRequest<>(MessageRequest.RequestType.USER_ALREADY_EXISTS, MessageProtocol.USER_ALREADY_EXISTS);
            this.out.writeUnshared(unsuccessful_login);
            this.out.flush();
        }
        // if user doesn't exist, thus allowing for successful login
        else if (!this.chatServer.userExists(this.username)) {

            this.chatServer.addUser(this.username, this.out);
            System.out.println(this.username + " signed in.");
            MessageRequest<?> login_successful = new MessageRequest<>(MessageRequest.RequestType.LOGIN_SUCCESS, MessageProtocol.LOGIN_SUCCESS);
            this.out.writeUnshared(login_successful);
            this.out.flush();

            MessageRequest<String> messages = new MessageRequest<>(MessageRequest.RequestType.CHAT_ROOM, this.chatServer.getChatMessages());
            this.out.writeUnshared(messages);
            this.out.flush();

        }
        // send an error message
        else {
            MessageRequest<?> error = new MessageRequest<>(MessageRequest.RequestType.ERROR, MessageProtocol.ERROR);
            out.writeUnshared(error);
            out.flush();
            this.chatServer.removeUser(this.username);// remove from the server
            this.socket.close();
        }

    }

}

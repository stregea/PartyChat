package Chat.Client.Network;

import Chat.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.NoSuchElementException;

/**
 * Class that will allow for a user to connect with the ChatServer
 *
 * @author Samuel Tregea
 * <p>
 * Last Modified: January 2, 2019
 */
public class ServerConnection {
    /**
     * the socket
     */
    private Socket sock;
    /**
     * reads PlaceRequests from the server
     */
    private ObjectInputStream clientIn;
    /**
     * writes place requests to the server
     */
    private ObjectOutputStream clientOut;
    /**
     * the model
     */
    private ChatRoom chatRoom;
    /**
     * true when you want this program to run
     */
    private boolean go;

    public ServerConnection(String host, int port, Users user) {
        try {
            this.sock = new Socket(host, port);
            this.clientIn = new ObjectInputStream(sock.getInputStream());
            this.clientOut = new ObjectOutputStream(sock.getOutputStream());
            this.go = true;

            this.login(user);

            // start the chat!
            // Run rest of client in separate thread.
            // This threads stops on its own at the end of the game and
            // does not need to rendezvous with other software components.
            Thread netThread = new Thread(() -> this.run());
            netThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Boolean that allows for the client to run their side of the program.
     */
    public boolean isRunning() {
        return this.go;
    }

    /**
     * Function that will allow a user to login, or receive error messages
     * that won't let him sign in
     *
     * @param user - the user signing in.
     */
    private void login(Users user) {
        try {

            // send LOGIN request to server
            MessageRequest<Users> request = new MessageRequest<>(MessageRequest.RequestType.LOGIN, user);
            clientOut.writeUnshared(request);
            clientOut.flush();

            // receiving login information from server
            MessageRequest<?> login_info = (MessageRequest<?>) this.clientIn.readUnshared();// getting stuck here after reprompt for new username
            handleRequest(login_info);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Update the chat room model.
     *
     * @param message - the message that was sent
     */
    private void updateChat(UserMessage message) {
        this.chatRoom.addMessage(message);
    }

    /**
     * sends the request to the server
     *
     * @param request a PlaceRequest
     */
    public void sendMessage(MessageRequest<?> request) {
        try {
            this.clientOut.writeUnshared(request);
            this.clientOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * stops the network client
     */
    private synchronized void stop() {
        this.go = false;
    }

    /**
     * Called when the server sends a message saying that
     * game play is damaged. Ends the game.
     *
     * @param arguments The error message sent from the reversi.server.
     */
    private void error(String arguments) {
        System.out.println(arguments);
        this.stop();
    }

    /**
     * This method should be called at the end of the game to
     * close the client connection.
     */
    private void close() {
        try {
            this.sock.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * @return the chat room
     */
    public ChatRoom getChatRoom() {
        return this.chatRoom;
    }

    /**
     * Handle the message from the server
     *
     * @param request - the message request from the server
     */
    private void handleRequest(MessageRequest<?> request) throws ClassNotFoundException, IOException {
        // successful login attempt
        switch (request.getType()) {
            case LOGIN_SUCCESS:
                System.out.println("Login Successful.");
                MessageRequest<String> chat_messages = (MessageRequest<String>) this.clientIn.readUnshared();// ChatRoom from server
                this.chatRoom = new ChatRoom(chat_messages.getData());
                break;
            case INVALID_USERNAME:
                this.chatRoom = new ChatRoom(MessageProtocol.INVALID_USERNAME);
                break;
            case USER_ALREADY_EXISTS:
                this.chatRoom = new ChatRoom(MessageProtocol.USER_ALREADY_EXISTS);
                System.err.println("User already exists.");
                break;
            case ERROR:
                System.err.println(request.getData());
                System.exit(0);
                break;
        }
    }

    /**
     * Run the main client loop. Intended to be started as a separate
     * thread internally. This method is made private so that no one
     * outside will call it or try to start a thread on it.
     */
    private void run() {
        while (isRunning()) {
            try {
                MessageRequest<?> request = (MessageRequest<?>) this.clientIn.readUnshared();
                switch (request.getType()) {

                    case MESSAGE_SENT:
                        updateChat((UserMessage) request.getData());
                        this.chatRoom.messageSent();
                        break;
                    case ERROR:
                        error((String) request.getData());
                        this.stop();
                        break;
                    default:
                        System.err.println("Unrecognized request: " + request);
                        break;
                }
            } catch (NoSuchElementException | IOException e) {
                // Looks like the connection shut down.
                this.error("Lost connection to server.");
                this.stop();
            } catch (ClassNotFoundException e) {
                this.error(e.getMessage() + "");
                this.stop();
            }
        }
        this.close();
    }

}

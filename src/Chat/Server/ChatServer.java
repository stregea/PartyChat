package Chat.Server;

import Chat.ChatRoom;
import Chat.MessageRequest;
import Chat.UserMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Multithreaded server that allows for multiple client usage
 *
 * @author Samuel Tregea
 * <p>
 * Last Date Modified: January 2, 2019
 */
public class ChatServer {

    /**
     * The ChatRoom users will interact with
     */
    private static ChatRoom chatRoom;

    /**
     * HashMap that will contain the names of all users and their OutputStreams.
     * <p>
     * This will allow for all the clients to receive updated messages of the board.
     */
    private static HashMap<String, ObjectOutputStream> activeUsers;
    /**
     * flag to tell the server to continue running
     **/
    private static boolean isRunning;
    /**
     * Queue that will be used for incoming messages
     **/
    private ConcurrentLinkedQueue<MessageRequest> message_queue;
    /**
     * Flag to tell the message queue that it is ready to read
     * the next message
     */
    private boolean messageReady;

    /**
     * Instantiate the data members
     */
    private ChatServer() {
        chatRoom = new ChatRoom();
        activeUsers = new HashMap<>();
        message_queue = new ConcurrentLinkedQueue<>();
        isRunning = true;
        this.messageReady = true;
    }

    /**
     * Run the server!
     *
     * @param args - not used
     */
    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            ChatServer server = new ChatServer();
            System.out.println("The server is listening...");
            while (isRunning) {
                Socket socket = null;

                try {
                    // socket object to receive incoming client requests
                    socket = serverSocket.accept();

                    System.out.println("A new client is connected : " + socket.toString());

                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                    // create a new thread to handle client
                    new ServerThread(socket,out,in,server).start();

                } catch (Exception e) {
                    socket.close();
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Determine if the user exists
     *
     * @param username - the name of the user attempting to see if they exist or not
     * @return true the user exists, false otherwise
     */
    boolean userExists(String username) {
        return activeUsers.containsKey(username);
    }

    /**
     * adds the user to both the hash map of running users and output streams
     *
     * @param username the username
     * @param out      the Object write thing
     */
    void addUser(String username, ObjectOutputStream out) {
        activeUsers.put(username, out);
        // user_information_map.put(username, new Users(username,password));
    }

    /**
     * Removing the user from the HashMap
     *
     * @param username - the user
     */
    void removeUser(String username) {
        activeUsers.remove(username);
    }

    /**
     * Method that will send clients a MESSAGE_SENT request
     *
     * @param messageRequest the SEND_MESSAGE request from the Client thread, and the message itself.
     */
    synchronized void enterMessageChanges(MessageRequest messageRequest) throws IOException {

        MessageRequest updateMessages = new MessageRequest<>(MessageRequest.RequestType.MESSAGE_SENT, messageRequest.getData());

        // adding the updated messages to a queue
        this.message_queue.add(updateMessages);

        try {
            // pausing the threads
            while (!this.messageReady) {
                this.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Updating the server-side chat
        this.updateChatRoom(updateMessages);

        // Telling the chat room on the client side to update
        this.updateClientChat(updateMessages);

        this.message_queue.poll();
        this.messageReady = true;
    }

    /**
     * Updates the chat room
     *
     * @param messageRequest the MESSAGE_SENT request
     */
    private synchronized void updateChatRoom(MessageRequest messageRequest) {
        this.messageReady = false;
        UserMessage message = (UserMessage) messageRequest.getData();
        chatRoom.addMessage(message);
    }

    /**
     * Telling the clients to update their view of the chat
     *
     * @param messageRequest the MESSAGE_SENT request
     */
    private void updateClientChat(MessageRequest messageRequest) throws IOException {
        for (String username : activeUsers.keySet()) {
            ObjectOutputStream out = activeUsers.get(username);
            out.writeUnshared(messageRequest);
            out.flush();
        }
    }

    /**
     * @return the chat room messages
     */
    String getChatMessages() {
        return chatRoom.toString();
    }

}

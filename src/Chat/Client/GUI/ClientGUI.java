package Chat.Client.GUI;

import Chat.*;
import Chat.Client.Network.ServerConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.*;

/**
 * Class that users will interact with and allow users
 * to chat with other users.
 *
 * @author Samuel Tregea
 * <p>
 * Last Modified: January 9, 2019
 */
public class ClientGUI extends Application implements Observer {

    /**
     * The ChatRoom object containing the chat room messages
     * and messages from the server.
     */
    private ChatRoom chatRoom;

    /**
     * Allows for the client to connect to the server.
     */
    private ServerConnection serverConnection;

    /**
     * The host to connect to.
     */
    private String host;

    /**
     * The port the host's connection is on
     */
    private int port;

    /**
     * Boolean that serves as a flag that allows for a
     * user to mute the sound effects.
     */
    private boolean isMuted;

    /**
     * Boolean that serves as a flag that will allow for the new message
     * sound effect only to play when the chat room is updated by other
     * users.
     */
    private boolean isMyMessage;

    /**
     * Label that is places on a ScrollPane that will display the chat room
     * messages.
     */
    private Label chat_messages;

    /**
     * ScrollPane that will display the Label "chat_messages".
     */
    private ScrollPane scrollPane;

    /**
     * The current user.
     */
    private Users user;

    /**
     * Alert message to display any error information the server sends over.
     */
    private Alert alert;

    /**
     * Mouse click sound effect.
     */
    private String mouse_click = "sfx/soundscrate-button-click2.mp3";

    /**
     * Message send sound effect
     */
    private String message_send = "sfx/knob.mp3";

    /**
     * Error message sound effect
     */
    private String error_message = "sfx/Computer Error-SoundBible.com-69768060.mp3";

    /**
     * Click on sound effect for toggling mute.
     */
    private String click_on = "sfx/Click On-SoundBible.com-1697535117.mp3";

    /**
     * Click off sound effect for toggling mute.
     */
    private String click_off = "sfx/Button Click Off-SoundBible.com-1730098776.mp3";

    /**
     * Sound effect to be used when a user receives a new message.
     */
    private String new_message = "sfx/stairs.mp3";

    /**
     * Allows for the mouse click sound effect to play on button press.
     */
    private AudioClip mouse_click_sound = new AudioClip(getClass().getResource(mouse_click).toString());

    /**
     * Allows for the message send sound effect to play on button press.
     */
    private AudioClip message_send_sound = new AudioClip(getClass().getResource(message_send).toString());

    /**
     * Allows for the error sound effect to play on error pop-up.
     */
    private AudioClip error_sound = new AudioClip(getClass().getResource(error_message).toString());

    /**
     * Allows for the mute click on sound effect to play on toggling mute button.
     */
    private AudioClip click_on_sound = new AudioClip(getClass().getResource(click_on).toString());

    /**
     * Allows for the mute click off sound effect to play on toggling mute button.
     */
    private AudioClip click_off_sound = new AudioClip(getClass().getResource(click_off).toString());

    /**
     * Allows for the new message sound effect to play.
     */
    private AudioClip new_message_sound = new AudioClip(getClass().getResource(new_message).toString());

    /**
     * Run the program.
     *
     * @param args - not used
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initialize necessary objects for program.
     */
    @Override
    public void init() {
        this.host = "localhost";
        this.port = 12345;
        this.isMuted = false;
        this.isMyMessage = true; // setting to true prevents a beep from logging in
        this.chat_messages = new Label();
        this.scrollPane = new ScrollPane();
        scrollPane.setContent(chat_messages); // places the updated chat room on to the ScrollPane
    }

    /**
     * Show and display the Stage.
     *
     * @param primaryStage - the stage being shown
     */
    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(partyChatHome(), 500, 500);

        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(e -> {
            System.exit(0);
        });

        primaryStage.show();

    }

    /**
     * Create the BorderPane that will hold the ScrollPane and GridPane.
     *
     * @return borderPane
     */
    private BorderPane chatRoomPane() {
        BorderPane borderPane = new BorderPane();

        this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        this.scrollPane.fitToHeightProperty();

        borderPane.setCenter(this.scrollPane);

        GridPane gridPane = createBottomGrid(this.scrollPane);

        borderPane.setBottom(gridPane);
        return borderPane;
    }


    /**
     * Create the home page.
     *
     * @return the home page
     */
    private GridPane partyChatHome() {
        GridPane home = new GridPane();
        HBox box = new HBox(10);

        home.setAlignment(Pos.CENTER);
        home.setHgap(10);
        home.setVgap(10);
        home.setPadding(new Insets(25, 25, 25, 25));

        //creation of buttons
        Button sign_in = new Button("Start Chatting!");

        box.getChildren().addAll(sign_in);
        home.add(box, 0, 0);

        sign_in.setOnAction(e -> {
            if (!this.isMuted) {
                mouse_click_sound.play();
            }
            sign_in.getScene().setRoot(partyChatSignIn());
        });

        return home;
    }

    /**
     * The sign in page. User will send info to server and will
     * receive the chat room.
     *
     * @return the new grid
     */
    private GridPane partyChatSignIn() {
        GridPane grid = new GridPane();
        HBox box = new HBox(10);
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text title = new Text("Create Your Username!");
        title.setTextAlignment(TextAlignment.CENTER);
        title.setFont(Font.font("Tahoma", FontWeight.BOLD, 30));
        grid.add(title, 0, 0, 2, 1);

        // Label + TextField
        Label userName = new Label("Username:");
        grid.add(userName, 0, 1);
        TextField userNameField = new TextField();
        grid.add(userNameField, 1, 1);

        // remove button
        Button sign_in = new Button("Sign In");
        grid.add(sign_in, 0, 3);

        // home button
        Button back = new Button("Back");
        box.getChildren().add(back);
        box.setAlignment(Pos.BASELINE_RIGHT);
        grid.add(back, 7, 7, 1, 1);

        // allowing user to go to the home page
        back.setOnAction(e -> {
            if (!this.isMuted) {
                mouse_click_sound.play();
            }
            back.getScene().setRoot(partyChatHome());
        });

        // determining if the user can sign in
        sign_in.setOnAction(e -> {

            if (!this.isMuted) {
                mouse_click_sound.play();
            }
            this.handleLogin(userNameField.getText(), sign_in);
        });

        // if the user hits 'enter' in the TextField
        userNameField.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER:
                    if (!this.isMuted) {
                        mouse_click_sound.play();
                    }
                    this.handleLogin(userNameField.getText(), userNameField);
                    break;
            }
        });


        return grid;
    }

    /**
     * Create and initialize the GridPane that will be
     * placed at the bottom of the BorderPane.
     *
     * @param sc - The ScrollPane that will have its contents updated
     * @return a new grid
     */
    private GridPane createBottomGrid(ScrollPane sc) {
        GridPane grid = new GridPane();
        TextArea textArea = new TextArea();
        Button send = new Button();
        Button mic = new Button();
        HBox box = new HBox(10);

        refreshMessages(); // updating the pane to current messages

        // Button Images
        ImageView send_button = new ImageView(new Image(getClass().getResource("images/send_button.png").toExternalForm(),
                60, 60, true, true));
        ImageView mic_button = new ImageView(new Image(getClass().getResource("images/mic_on.png").toExternalForm(),
                60, 60, true, true));

        // TextArea components
        textArea.setWrapText(true);
        textArea.setPrefColumnCount(30);
        textArea.setPrefRowCount(2);
        textArea.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    if (!isMuted) {
                        message_send_sound.play();
                    }
                    handleMessage(textArea, sc, textArea);
                    break;
            }
        });

        // ImageView components
        send_button.setStyle("-fx-background-color:  #ffffff");
        mic_button.setStyle("-fx-background-color:  #ffffff");

        // ---Button components---
        // Send Button
        send.setGraphic(send_button);
        send.setStyle("-fx-background-color:  #ffffff");
        send.setPrefSize(75, 75);
        send.setShape(new Circle(.1));
        // Toggle Button
        mic.setGraphic(mic_button);
        mic.setStyle("-fx-background-color:  #ffffff");
        mic.setPrefSize(75, 75);
        mic.setShape(new Circle(.8));

        //Action Events
        send.setOnMouseEntered(e -> send.setStyle("-fx-background-insets: 0"));
        send.setOnMouseExited(e -> send.setStyle("-fx-background-color:  #ffffff"));
        send.setOnAction(e -> {
            if (!this.isMuted) {
                message_send_sound.play();
            }
            handleMessage(textArea, sc, send);
        });

        mic.setOnMouseEntered(e -> mic.setStyle("-fx-background-insets: 0"));
        mic.setOnMouseExited(e ->  mic.setStyle("-fx-background-color:  #ffffff"));
        mic.setOnAction(e -> toggleMuteButton(mic));

        box.getChildren().addAll(textArea, send, mic);

        // GridPane components
        grid.add(new Label("Enter message: "), 0, 0);
        grid.add(box, 0, 1, 1, 1);
        grid.setStyle("-fx-background-color:  White");


        return grid;
    }

    /**
     * This subroutine is called upon either an action press or a enter press.
     * This method will then take the previous existing label and update it to
     * a new string by taking a new string from the text area and add it to the previously
     * existing string.
     * <p>
     * If a message is larger than 16 or 72 characters, it will take the string and divide it by 2,
     * for easier reading.
     *
     * @param textArea - the area in which the user enters a String
     */
    private void handleMessage(TextArea textArea, ScrollPane sc, Node node) {

        String divisor1, divisor2, newMessage;

        int size_of_message = textArea.getText().toCharArray().length;

        if (size_of_message > 56) {
            divisor1 = textArea.getText().substring(0, size_of_message / 2 - 1);
            divisor2 = "\n" + textArea.getText().substring(size_of_message / 2, size_of_message);
            newMessage = divisor1 + divisor2;
        } else {
            if (node instanceof Button) {
                newMessage = textArea.getText() + "\n";
            } else if (node instanceof TextArea) {
                newMessage = textArea.getText();
            } else
                newMessage = null;
        }

        // message object that is sent to server
        UserMessage message = new UserMessage(this.user, getCurrentTime(), newMessage); // creating a new message object every time a user enters a message

        this.isMyMessage = true;

        this.sendMessage(message);

        sc.setVvalue(1.0); // telling the ScrollPane to scroll down

        textArea.clear();
    }

    /**
     * Subroutine called to handle the login event used by the client.
     *
     * @param username - the username for the user signing in
     * @param node     - the node being used to change the scene
     */
    private void handleLogin(String username, Node node) {

        this.loginToServer(username);

        switch (this.chatRoom.getMessages()) {

            case MessageProtocol.ERROR:
                this.alert = new Alert(AlertType.ERROR);
                this.alert.setHeaderText("There was an error within the server");
                this.alert.setContentText("Please try again.");
                this.error_sound.play();
                this.alert.showAndWait();
                break;
            case MessageProtocol.INVALID_USERNAME:
                this.alert = new Alert(AlertType.ERROR);
                this.alert.setHeaderText("Invalid username.");
                this.alert.setContentText("Please choose another username to log in!");
                this.error_sound.play();
                this.alert.showAndWait();
                this.chatRoom = new ChatRoom(null);
                break;
            case MessageProtocol.USER_ALREADY_EXISTS:
                this.alert = new Alert(AlertType.WARNING);
                this.alert.setHeaderText("User already exists.");
                this.alert.setContentText("Please choose another username to log in!");
                this.error_sound.play();
                this.alert.showAndWait();
                break;
            // default: the login was successful, set an observer.
            default:
                this.chatRoom.addObserver(this);
                node.getScene().setRoot(chatRoomPane());
                break;
        }
    }

    /**
     * Subroutine that will allow for a user to login.
     *
     * @param username - the username
     *                 //* @param password - the password
     */
    private void loginToServer(String username) {
        this.user = new Users(username);
        this.serverConnection = new ServerConnection(this.host, this.port, this.user);
        this.chatRoom = serverConnection.getChatRoom();
    }

    /**
     * Sends a message to the server. Called within handleMessage().
     *
     * @param message - the message to be sent
     */
    private void sendMessage(UserMessage message) {

        // The message to be sent to the server
        MessageRequest<UserMessage> req = new MessageRequest<>(MessageRequest.RequestType.SEND_MESSAGE, message);

        this.serverConnection.sendMessage(req);
    }

    /**
     * Allows for the toggle of the mute button.
     *
     * @param button - the mute button
     */
    private void toggleMuteButton(Button button) {
        if (!this.isMuted) {
            ImageView mic_off = new ImageView(new Image(getClass().getResource("images/mic_off.png").toExternalForm(),
                    60, 60, true, true));
            button.setGraphic(mic_off);
            this.click_off_sound.play();
            this.isMuted = true;
        } else {
            ImageView mic_on = new ImageView(new Image(getClass().getResource("images/mic_on.png").toExternalForm(),
                    60, 60, true, true));
            button.setGraphic(mic_on);
            this.click_on_sound.play();
            this.isMuted = false;
        }

    }

    /**
     * Returns the current time
     */
    private long getCurrentTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        return calendar.getTime().getTime();
    }

    /**
     * Update the chat room. Called when the Observable object (chatroom) calls
     * notifyObservers() in ServerConnection.
     */
    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(() -> {
            refreshMessages();
        });
    }

    /**
     * Subroutine that's called to update the ScrollPane and to
     * update the Label that is containing the chat room's contents.
     */
    private void refreshMessages() {

        // Updating the Label
        this.chat_messages.setText(this.chatRoom.getMessages());

        // if the mute isn't toggled and the incoming message isn't the users message
        if (!this.isMuted && !this.isMyMessage)
            this.new_message_sound.play();

        // Re-updating the ScrollPanes contents
        this.scrollPane.setContent(chat_messages);

        // Allows for the new message sound to play only for clients who didn't send a message to update the chat
        this.isMyMessage = false;

        // Updating the ScrollPane to scroll down
        this.scrollPane.setVvalue(1.0);
    }
}
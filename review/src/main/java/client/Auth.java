package client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import serial.CloudMessage;
import serial.RegistrationMessage;
import serial.ResponseSignInMessage;
import serial.SignInRequestMessage;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class Auth implements Initializable {


    public Button btnSignIn;
    public Button btnRegistration;
    public PasswordField passwordField;
    public TextField textFieldSignIn;
    public TextField textFieldNick;
    public Label labelNick1;
    public Label labelNick2;
    public Label labelWrongLoginOrPass;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private boolean flagRegistration = false;

    private static Socket socket;

    private void readLoop() {
        boolean flag = false;
        try {
            while (true) {
                if (flag) {
                    break;
                }
                CloudMessage inputObj = (CloudMessage) is.readObject();
                log.info("received: {}", inputObj);
                switch (inputObj.getType()) {
                    case RESPONSE_SIGN_IN:
                        if (((ResponseSignInMessage) inputObj).isResponse()) {
                            Platform.runLater(() -> {
                                try {
                                    Parent parent = FXMLLoader.load(getClass().getResource("layout.fxml"));
                                    Stage window = (Stage) ((Node) btnRegistration).getScene().getWindow();
                                    window.setScene(new Scene(parent));
                                    window.show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            });
                            flag = true;
                        } else {
                            Platform.runLater(() -> {
                                passwordField.clear();
                                labelWrongLoginOrPass.setVisible(true);
                            });
                        }

                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket("localhost", 8189);
            System.out.println("Network created...");
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());

            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onClickSignIn(MouseEvent mouseEvent) throws IOException {
        os.writeObject(new SignInRequestMessage(textFieldSignIn.getText(), passwordField.getText()));
    }

    public void onClickRegistration(MouseEvent mouseEvent) throws IOException {
        if (flagRegistration) {
            os.writeObject(new RegistrationMessage(textFieldSignIn.getText(), passwordField.getText(), textFieldNick.getText()));
        } else {
            labelNick1.setVisible(true);
            labelNick2.setVisible(true);
            textFieldNick.setVisible(true);
            flagRegistration = true;
        }

    }

    public static Socket getSocket() {
        return socket;
    }
}

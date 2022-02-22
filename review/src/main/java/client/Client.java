package client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import serial.*;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
@Slf4j
public class Client implements Initializable {
    public ListView<String> listViewClient;
    public ListView<String> listViewServer;
    public Button btnSendToClient;
    public Button btnSendToServer;
    public TextField pathClientTextField;
    public TextField pathServerTextField;
    public Button btnDelete;

    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private static Path pathClient = Paths.get("src/main/java/client/root");


    private void readLoop() {
        try {
            while (true) {
                CloudMessage inputObj = (CloudMessage) is.readObject();
                log.info("received: {}", inputObj);
                switch (inputObj.getType()) {
                    case FILE: processFileMessage((FileMessage)inputObj);
                    break;
                    case LIST: processListMessage((ListMessage)inputObj);
                    break;
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processListMessage(ListMessage inputObj) {
        Platform.runLater(() -> {
            listViewServer.getItems().clear();
            listViewServer.getItems().addAll(inputObj.getListFiles());
            updateTextFieldServer(inputObj.getPath());
        });
    }

    private void processFileMessage(FileMessage inputObj) throws IOException {
        Files.write(pathClient.resolve(inputObj.getFileName()), inputObj.getBytes());
        updateListViewClient();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            Socket socket = Auth.getSocket();

            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());

            updateListViewClient();
            updateTextFieldClient(pathClient.toString());

            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickedNewDirectoryClientListener(MouseEvent mouseEvent) {
        listViewServer.getSelectionModel().clearSelection();
        if(mouseEvent.getClickCount() == 2) {
            String selectedDirectory = listViewClient.getSelectionModel().getSelectedItem();
            if (selectedDirectory != null) {
                if (Files.isDirectory(pathClient.resolve(selectedDirectory))) {
                    pathClient = pathClient.resolve(selectedDirectory);
                    updateListViewClient();
                    updateTextFieldClient(pathClient.toString());
                }
            }
        }
    }
    public void onClickedNewDirectoryServerListener(MouseEvent mouseEvent) throws IOException {
            listViewClient.getSelectionModel().clearSelection();
            if(mouseEvent.getClickCount() == 2) {
            String selectedDirectory = listViewServer.getSelectionModel().getSelectedItem();
            if (selectedDirectory != null) {
            os.writeObject(new ChangePathRequestMessage(selectedDirectory));
            }
        }
    }

    public void btnSendToClientOnClick(MouseEvent mouseEvent) throws IOException {
        // send file to client (request)
        String fileName = listViewServer.getSelectionModel().getSelectedItem();
        os.writeObject(new FileRequestMessage(fileName));
    }

    public void btnSendToServerOnClick(MouseEvent mouseEvent) throws IOException {
        //send to server
        String fileName = listViewClient.getSelectionModel().getSelectedItem();
        os.writeObject(new FileMessage(pathClient.resolve(fileName)));
    }

    public void btnUndoClientOnClicked(MouseEvent mouseEvent) {
        //change path to client (undo)
        pathClient = pathClient.resolve("..").normalize();
        updateTextFieldClient(pathClient.toString());
        updateListViewClient();
    }

    public void btnUndoServerOnClicked(MouseEvent mouseEvent) throws IOException {
        //change path to server (undo)
        os.writeObject(new ChangePathRequestMessage(".."));
    }

    public void updateListViewClient() {
        Platform.runLater(() -> {
            listViewClient.getItems().clear();
            listViewClient.getItems().addAll(pathClient.toFile().list());
        });
    }
    public void updateTextFieldClient(String path) {
        Platform.runLater(() -> {
            pathClientTextField.setText(path);
        });
    }

    public void updateTextFieldServer(String path) {
        Platform.runLater(() -> {
            pathServerTextField.setText(path);
        });
    }

    public void btnDeleteOnClick(MouseEvent mouseEvent) throws IOException {

        String selectedFile = listViewClient.getSelectionModel().getSelectedItem();
        if (selectedFile == null) {
            selectedFile = listViewServer.getSelectionModel().getSelectedItem();
            if (selectedFile != null) {
                os.writeObject(new DeleteFileRequestMessage(selectedFile));
            }
        } else {
            Files.delete(pathClient.resolve(selectedFile));
            updateListViewClient();
        }

    }

}

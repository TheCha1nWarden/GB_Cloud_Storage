package client;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import serial.AbstractCommand;
import serial.TransferObjListFilesFromServer;
import serial.TransferObjToServer;
import serial.requestFileFromServer;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class Client implements Initializable {
    public ListView<String> listViewClient;
    public ListView<String> listViewServer;
    public Button btnSendToClient;
    public Button btnSendToServer;
    private Socket socket;
    private ObjectInputStream is;
    private ObjectOutputStream os;
    private String[] listFilesFromServer;

    public Client() {
        Platform.runLater(() -> {
            File file = new File("src/main/java/client/root");
            listViewClient.getItems().addAll(file.list());
            listViewServer.getItems().addAll(listFilesFromServer);
        });

    }

    public void btnSendToClientOnClick(MouseEvent mouseEvent) {
        String selectedFileName = listViewServer.getSelectionModel().getSelectedItem();
        sendObj(new requestFileFromServer(selectedFileName));
        Platform.runLater(() -> {
            listViewServer.getItems().remove(selectedFileName);
            listViewClient.getItems().add(selectedFileName);
        });
    }

    public void btnSendToServerOnClick(MouseEvent mouseEvent) {
        File selectedFile = new File("src/main/java/client/root/" + listViewClient.getSelectionModel().getSelectedItem());
        try {
            byte[] arrayByteFile = Files.readAllBytes(selectedFile.toPath());
            sendObj(new TransferObjToServer(arrayByteFile, selectedFile.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        selectedFile.delete();
        Platform.runLater(() -> {
            listViewClient.getItems().remove(selectedFile.getName());
            listViewServer.getItems().add(selectedFile.getName());
        });
    }

    private void readLoop() {
        try {
            while (true) {
                AbstractCommand inputObj = (AbstractCommand) is.readObject();
                Platform.runLater(() -> {
                    File file = new File("src/main/java/client/root/" + inputObj.returnFile().getName());
                    inputObj.doIt(file, os);
                });

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
            os = new ObjectOutputStream(socket.getOutputStream());
            is = new ObjectInputStream(socket.getInputStream());
            TransferObjListFilesFromServer inputObj = (TransferObjListFilesFromServer) is.readObject();
            listFilesFromServer = inputObj.getList();
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendObj(AbstractCommand obj) {
        try {
            os.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

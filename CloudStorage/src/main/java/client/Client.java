package client;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import serial.*;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class Client implements Initializable {
    public ListView<String> listViewClient;
    public ListView<String> listViewServer;
    public Button btnSendToClient;
    public Button btnSendToServer;
    public TextField pathClientTextField;
    public TextField pathServerTextField;

    private ObjectInputStream is;
    private ObjectOutputStream os;
    private String[] listFilesFromServer;
    private Path pathClient = Paths.get("src/main/java/client/root");
    private Path pathServer = Paths.get("root");

    public Client() {
        Platform.runLater(() -> {
            updateListViewClient();
            updateListViewServer(listFilesFromServer);
            File file = new File(pathClient.toString());
            listViewClient.getItems().addAll(file.list());
            listViewServer.getItems().addAll(listFilesFromServer);
            pathClientTextField.setText(pathClient.toString());
            pathServerTextField.setText(pathServer.toString());
        });

    }

    public void btnSendToClientOnClick(MouseEvent mouseEvent) {
        String selectedFileName = listViewServer.getSelectionModel().getSelectedItem();
        sendObj(new requestFileFromServer(selectedFileName));
    }

    public void btnSendToServerOnClick(MouseEvent mouseEvent) {
        File selectedFile = new File(pathClient.resolve(listViewClient.getSelectionModel().getSelectedItem()).toString());
        try {
            byte[] arrayByteFile = Files.readAllBytes(selectedFile.toPath());
            sendObj(new TransferObjToServer(arrayByteFile, selectedFile.getName()));
            selectedFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readLoop() {
        try {
            while (true) {
                AbstractCommand inputObj = (AbstractCommand) is.readObject();
                    File nameFile = inputObj.returnFile();
                    if (nameFile != null) {
                        File file = new File(pathClient.resolve(nameFile.getName()).toString());
                        inputObj.doIt(file, os);
                    }
                    if (inputObj.getListFiles() != null ){
                        listFilesFromServer = inputObj.getListFiles();
                        updateListViewServer(listFilesFromServer);
                        updateListViewClient();
                    }
                    if (inputObj.isDirectorySelected() != null) {
                        updateTextFieldServer(nameFile.toString());
                    }



            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8189);
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

    public void updateListViewClient() {
        Platform.runLater(() -> {
            listViewClient.getItems().clear();
            listViewClient.getItems().addAll(pathClient.toFile().list());
        });
    }

    public void updateListViewServer(String[] listFiles) {
        Platform.runLater(() -> {
            listViewServer.getItems().clear();
            listViewServer.getItems().addAll(listFiles);
        });
    }

    public void updateTextFieldClient(String name) {
        pathClient = pathClient.resolve(name).normalize();
        pathClientTextField.setText(pathClient.toString());
    }

    public void updateTextFieldServer(String name) {
        pathServer = Paths.get(name.substring(21));
        pathServerTextField.setText(pathServer.toString());
    }

    public void btnUndoClientOnClicked(MouseEvent mouseEvent) {
        updateTextFieldClient("..");
        updateListViewClient();
    }

    public void btnUndoServerOnClicked(MouseEvent mouseEvent) {
        if(!pathServer.endsWith("root")){
            sendObj(new requestChangePathToServer(".."));
        }

    }

    public void onClickedNewDirectoryClientListener(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2) {
            String selectedDirectory = listViewClient.getSelectionModel().getSelectedItem();
            if (selectedDirectory != null) {
                if (Files.isDirectory(pathClient.resolve(selectedDirectory))) {
                    updateTextFieldClient(selectedDirectory);
                    updateListViewClient();
                }
            }
        }
    }
    public void onClickedNewDirectoryServerListener(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2) {
            String selectedDirectory = listViewServer.getSelectionModel().getSelectedItem();
            if (selectedDirectory != null) {
                sendObj(new requestChangePathToServer(selectedDirectory));
            }
        }
    }
}

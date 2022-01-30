package server;

import serial.AbstractCommand;
import serial.AskChangePathToServer;
import serial.TransferObjListFilesFromServer;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Handler implements Runnable{
    private Socket socket;
    private ObjectInputStream is;
    private ObjectOutputStream os;

    private Path path = Paths.get("src/main/java/server/root");


    public Handler(Socket socket) throws IOException {

        this.socket = socket;
        is = new ObjectInputStream(socket.getInputStream());
        os = new ObjectOutputStream(socket.getOutputStream());
        os.writeObject(new TransferObjListFilesFromServer(path.toFile().list()));
    }

    @Override
    public void run() {

        try {
            while (true) {
                AbstractCommand inputObj = (AbstractCommand) is.readObject();
                System.out.println("received: " + inputObj);
                inputObj.doIt(path.toFile(), os);
                if (inputObj.returnFile() != null) {
                    if (Files.isDirectory(path.resolve(inputObj.returnFile().toPath()).normalize())) {
                        path = path.resolve(inputObj.returnFile().toPath()).normalize();
                        os.writeObject(new AskChangePathToServer(path.toFile().list(), true, path.toFile()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

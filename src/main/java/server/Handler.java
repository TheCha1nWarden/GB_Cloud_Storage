package server;

import serial.AbstractCommand;
import serial.TransferObjListFilesFromServer;

import java.io.*;
import java.net.Socket;

public class Handler implements Runnable{
    private Socket socket;
    private ObjectInputStream is;
    private ObjectOutputStream os;
    private File rootDir = new File("src/main/java/server/root");

    public Handler(Socket socket) throws IOException {
        this.socket = socket;
        is = new ObjectInputStream(socket.getInputStream());
        os = new ObjectOutputStream(socket.getOutputStream());
        os.writeObject(new TransferObjListFilesFromServer(rootDir.list()));
    }

    @Override
    public void run() {
        try {
            while (true) {
                AbstractCommand inputObj = (AbstractCommand) is.readObject();
                System.out.println("received: " + inputObj);
                inputObj.doIt(rootDir, os);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

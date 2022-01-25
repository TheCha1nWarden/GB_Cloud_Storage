package serial;

import java.io.*;
import java.nio.file.Files;

public class TransferObjToServer extends AbstractCommand{
    private File file;
    private byte[] byteFile;
    private String nameFile;

    public TransferObjToServer(byte[] byteFile, String nameFile) {
        this.byteFile = byteFile;
        this.nameFile = nameFile;
    }

    @Override
    public void doIt(File root, ObjectOutputStream os) {
        File file = new File(root.getPath() + "\\" +  nameFile);
        try {
            file.createNewFile();
            Files.write(file.toPath(), byteFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public File returnFile() {
        return file;
    }
}

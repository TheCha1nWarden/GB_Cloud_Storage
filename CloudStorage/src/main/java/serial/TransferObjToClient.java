package serial;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;

public class TransferObjToClient extends AbstractCommand {
    private byte[] byteFile;
    private String[] listFiles;
    private String nameFile;

    public TransferObjToClient(byte[] byteFile, String nameFile, String[] listFiles) {
        this.byteFile = byteFile;
        this.nameFile = nameFile;
        this.listFiles = listFiles;
    }

    @Override
    public void doIt(File file, ObjectOutputStream os) {
        try {
            file.createNewFile();
            Files.write(file.toPath(), byteFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String[] getListFiles() {
        return listFiles;
    }

    @Override
    public File returnFile() {
        return new File(nameFile);
    }
}

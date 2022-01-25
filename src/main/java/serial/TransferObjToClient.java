package serial;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;

public class TransferObjToClient extends AbstractCommand {
    private byte[] byteFile;

    private String nameFile;

    public TransferObjToClient(byte[] byteFile, String nameFile) {
        this.byteFile = byteFile;
        this.nameFile = nameFile;
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
    public File returnFile() {
        return new File(nameFile);
    }
}

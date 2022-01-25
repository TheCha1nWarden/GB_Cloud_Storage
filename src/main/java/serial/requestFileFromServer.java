package serial;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;

public class requestFileFromServer extends AbstractCommand{
    private String nameFile;
    private ObjectOutputStream os;

    public requestFileFromServer(String nameFile) {
        this.nameFile = nameFile;
    }

    @Override
    public void doIt(File file, ObjectOutputStream os) {
        this.os = os;
        File targetFile = new File(file + "\\" + nameFile);
        try {
            byte[] arrayByteFile = Files.readAllBytes(targetFile.toPath());
            sendObj(new TransferObjToClient(arrayByteFile, nameFile));
            targetFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public File returnFile() {
        return null;
    }

    private void sendObj(AbstractCommand obj) {
        try {
            os.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

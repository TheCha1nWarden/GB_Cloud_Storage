package serial;

import java.io.File;
import java.io.ObjectOutputStream;

public class AskTransferObjToServer extends AbstractCommand{
    private String[] list;

    @Override
    public String[] getListFiles() {
        return list;
    }

    public AskTransferObjToServer(String[] list) {
        this.list = list;
    }

    @Override
    public void doIt(File file, ObjectOutputStream os) {

    }

    @Override
    public File returnFile() {
        return null;
    }
}

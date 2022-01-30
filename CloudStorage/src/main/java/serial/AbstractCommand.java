package serial;

import java.io.File;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public abstract class AbstractCommand implements Serializable {
    public abstract void doIt(File file, ObjectOutputStream os);
    public abstract File returnFile();
    public String[] getListFiles() {
        return null;
    }
    public Boolean isDirectorySelected() {
        return null;
    }

}

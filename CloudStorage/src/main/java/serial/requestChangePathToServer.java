package serial;

import java.io.File;
import java.io.ObjectOutputStream;

public class requestChangePathToServer extends AbstractCommand{
    private File nameNewDir;


    public requestChangePathToServer(String nameNewDir) {
        this.nameNewDir = new File(nameNewDir);
    }

    @Override
    public void doIt(File file, ObjectOutputStream os) {

    }

    @Override
    public File returnFile() {
        return nameNewDir;
    }
}

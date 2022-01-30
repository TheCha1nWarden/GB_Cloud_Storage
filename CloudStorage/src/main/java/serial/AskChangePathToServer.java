package serial;

import java.io.File;
import java.io.ObjectOutputStream;

public class AskChangePathToServer extends AbstractCommand{
    private String[] listFiles;
    private Boolean isDirectorySelected;
    private File nameFile;

    public AskChangePathToServer(String[] listFiles, Boolean isDirectorySelected, File nameFile) {
        this.listFiles = listFiles;
        this.isDirectorySelected = isDirectorySelected;
        this.nameFile = nameFile;
    }

    @Override
    public String[] getListFiles() {
        return listFiles;
    }

    @Override
    public void doIt(File file, ObjectOutputStream os) {

    }

    @Override
    public File returnFile() {
        return nameFile;
    }

    @Override
    public Boolean isDirectorySelected() {
        return isDirectorySelected;
    }
}

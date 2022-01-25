package serial;

import java.io.File;
import java.io.Serializable;

public class TransferObjListFilesFromServer implements Serializable {
    private String[] list;

    public TransferObjListFilesFromServer(String[] list) {
        this.list = list;
    }

    public String[] getList() {
        return list;
    }
}

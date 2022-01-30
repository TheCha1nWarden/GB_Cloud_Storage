package serial;

import lombok.Data;

@Data
public class ChangePathRequest implements CloudMessage{

    private final String dirName;

    public ChangePathRequest(String dirName) {
        this.dirName = dirName;
    }

    @Override
    public CommandType getType() {
        return CommandType.CHANGE_PATH_REQUEST;
    }
}

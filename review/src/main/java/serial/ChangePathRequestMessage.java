package serial;

import lombok.Data;

@Data
public class ChangePathRequestMessage implements CloudMessage{

    private final String dirName;

    public ChangePathRequestMessage(String dirName) {
        this.dirName = dirName;
    }

    @Override
    public CommandType getType() {
        return CommandType.CHANGE_PATH_REQUEST;
    }
}

package serial;

import lombok.Data;

@Data
public class DeleteFileRequestMessage implements CloudMessage{

    private final String fileName;

    public DeleteFileRequestMessage(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public CommandType getType() {
        return CommandType.DELETE_FIlE_REQUEST;
    }
}

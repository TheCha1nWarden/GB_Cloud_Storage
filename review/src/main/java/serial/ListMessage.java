package serial;

import lombok.Data;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Data
public class ListMessage implements CloudMessage{

    private final List<String> listFiles;
    private final String path;

    public ListMessage(Path path) {
        this.path = path.toString();
        listFiles = Arrays.asList(path.toFile().list());
    }

    @Override
    public CommandType getType() {
        return CommandType.LIST;
    }
}

package serial;

import java.io.Serializable;

public interface CloudMessage extends Serializable {
    CommandType getType();
}

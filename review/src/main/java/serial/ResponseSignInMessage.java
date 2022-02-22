package serial;

import lombok.Data;

@Data
public class ResponseSignInMessage implements CloudMessage{

    public ResponseSignInMessage(boolean response) {
        this.response = response;
    }

    private final boolean response;

    @Override
    public CommandType getType() {
        return CommandType.RESPONSE_SIGN_IN;
    }
}

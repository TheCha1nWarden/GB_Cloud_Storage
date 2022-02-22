package serial;

import lombok.Data;

@Data
public class SignInRequestMessage implements CloudMessage{

    private final String login;
    private final String password;

    public SignInRequestMessage(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public CommandType getType() {
        return CommandType.SIGN_IN_REQUEST;
    }
}

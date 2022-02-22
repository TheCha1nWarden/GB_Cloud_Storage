package serial;

import lombok.Data;

@Data
public class RegistrationMessage implements CloudMessage{

    private final String login;
    private final String password;
    private final String nick;

    public RegistrationMessage(String login, String password, String nick) {
        this.login = login;
        this.password = password;
        this.nick = nick;
    }

    @Override
    public CommandType getType() {
        return CommandType.REGISTRATION;
    }
}

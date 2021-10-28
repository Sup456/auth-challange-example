package mamontov.stepan.server.service;

import mamontov.stepan.server.model.AuthStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

import static mamontov.stepan.server.model.AuthStatus.AUTHORIZED;
import static mamontov.stepan.server.model.AuthStatus.NOT_AUTHORIZED;

@Service
public class AuthService {
    private static final Map<String, String> database = Map.of("username", "password");

    public AuthStatus checkPermission(String username, String password) {
        var passwordStored = database.get(username);
        if (!password.equals(passwordStored)) {
            return NOT_AUTHORIZED;
        } else {
            return AUTHORIZED;
        }
    }

}

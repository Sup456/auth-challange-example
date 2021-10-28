package mamontov.stepan.server.model;

import lombok.Data;

@Data
public class AuthRequest {
    private final String username;
    private final String password;
}

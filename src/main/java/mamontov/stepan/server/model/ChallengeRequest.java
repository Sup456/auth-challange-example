package mamontov.stepan.server.model;

import lombok.Data;

@Data
public class ChallengeRequest {
    private final String prefix;
    private final String result;
    private final String username;
    private final String password;
}

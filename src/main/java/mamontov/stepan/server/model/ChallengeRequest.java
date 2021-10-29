package mamontov.stepan.server.model;

import lombok.Data;

@Data
public class ChallengeRequest {
    private final String prefix;
    private final String result;
}

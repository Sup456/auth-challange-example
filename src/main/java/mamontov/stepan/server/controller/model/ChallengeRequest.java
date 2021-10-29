package mamontov.stepan.server.controller.model;

import lombok.Data;

@Data
public class ChallengeRequest {
    private final String prefix;
    private final String result;
}

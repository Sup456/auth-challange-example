package mamontov.stepan.server.controller.model;

import lombok.*;
import mamontov.stepan.server.model.HashFunction;

@Data
public class ChallengeSettingsRequest {
    private final Integer complexity;
    private final Integer limit;
    private final Integer length;
    private final Integer expiration;
    private final HashFunction hashFunction;
}

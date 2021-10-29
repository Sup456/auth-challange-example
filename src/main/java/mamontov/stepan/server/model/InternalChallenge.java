package mamontov.stepan.server.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InternalChallenge {
    private final User user;
    private final Challenge challenge;
    private final LocalDateTime expirationDate;
}

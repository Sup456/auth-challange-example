package mamontov.stepan.server.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import mamontov.stepan.server.model.AuthStatus;
import mamontov.stepan.server.model.Challenge;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private final AuthStatus status;
    private final Challenge challenge;
}

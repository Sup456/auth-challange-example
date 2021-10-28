package mamontov.stepan.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private final AuthStatus status;
    private final Challenge challenge;
}

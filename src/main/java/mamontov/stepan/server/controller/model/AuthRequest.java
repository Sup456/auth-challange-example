package mamontov.stepan.server.controller.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthRequest {
    @NotBlank(message = "Username can't be empty")
    private final String username;
    @NotBlank(message = "Password can't be empty")
    private final String password;
}

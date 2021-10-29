package mamontov.stepan.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mamontov.stepan.server.controller.model.AuthRequest;
import mamontov.stepan.server.controller.model.AuthResponse;
import mamontov.stepan.server.controller.model.ChallengeRequest;
import mamontov.stepan.server.model.User;
import mamontov.stepan.server.service.AuthService;
import mamontov.stepan.server.service.ChallengeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

import static mamontov.stepan.server.model.AuthStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final ChallengeService challengeService;

    @PostMapping("/auth")
    public AuthResponse register(@RequestBody @Valid AuthRequest request) {
        var user = new User(request.getUsername(), request.getPassword());
        return challengeService.challengeIfNeeded(user)
                .map(challenge -> new AuthResponse(CHALLENGE, challenge))
                .or(() -> {
                    final var status = authService.checkPermission(user);
                    if (status == NOT_AUTHORIZED) {
                        challengeService.addNotAuthenticatedCounter();
                    }
                    return Optional.of(new AuthResponse(status, null));
                })
                .orElseThrow();
    }

    @PostMapping("/challenge")
    public AuthResponse checkChallenge(@RequestBody ChallengeRequest request) {
        return challengeService.isChallengeCompleted(request.getPrefix(), request.getResult())
                .map(user -> new AuthResponse(authService.checkPermission(user), null))
                .orElse(new AuthResponse(NOT_AUTHORIZED, null));
    }
}

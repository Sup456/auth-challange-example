package mamontov.stepan.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mamontov.stepan.server.model.AuthRequest;
import mamontov.stepan.server.model.AuthResponse;
import mamontov.stepan.server.model.ChallengeRequest;
import mamontov.stepan.server.service.AuthService;
import mamontov.stepan.server.service.ChallengeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static mamontov.stepan.server.model.AuthStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final ChallengeService challengeService;

    @PostMapping("/auth")
    public AuthResponse register(@RequestBody AuthRequest request) {
        return challengeService.challengeIfNeeded()
                .map(challenge -> new AuthResponse(CHALLENGE, challenge))
                .or(() -> {
                    final var status = authService.checkPermission(request.getUsername(), request.getPassword());
                    if (status == NOT_AUTHORIZED) {
                        challengeService.addNotAuthenticatedCounter();
                    }
                    return Optional.of(new AuthResponse(status, null));
                })
                .orElseThrow();
    }

    @PostMapping("/challenge")
    public AuthResponse checkChallenge(@RequestBody ChallengeRequest request) {
        if (challengeService.isChallengeCompleted(request.getPrefix(), request.getResult())) {
            final var status = authService.checkPermission(request.getUsername(), request.getPassword());
            if (status == NOT_AUTHORIZED) {
                challengeService.addNotAuthenticatedCounter();
            }
            return new AuthResponse(status, null);
        } else {
            challengeService.addNotAuthenticatedCounter();
            return new AuthResponse(NOT_AUTHORIZED, null);
        }
    }
}

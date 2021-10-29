package mamontov.stepan.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mamontov.stepan.server.controller.model.ChallengeSettingsRequest;
import mamontov.stepan.server.service.ChallengeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @Scheduled(fixedRate = 10000)
    public void removeExpiredChallenges() {
        log.info("Started scheduled job: removeExpiredChallenges");
        challengeService.deleteExpiredChallenges();
    }

    @PostMapping("/internal/challenges")
    public void changeComplexity(@RequestBody ChallengeSettingsRequest request) {
        challengeService.changeSettings(request.getComplexity(), request.getLimit(), request.getLength(), request.getExpiration(), request.getHashFunction());
    }
}

package mamontov.stepan.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mamontov.stepan.server.service.ChallengeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @Scheduled(fixedRate = 10000)
    public void removeExpiredChallenges() {
        log.info("Started scheduled job: removeExpiredChallenges");
        challengeService.deleteExpiredChallenges();
    }
}

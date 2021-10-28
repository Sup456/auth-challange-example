package mamontov.stepan.server.service;

import lombok.extern.slf4j.Slf4j;
import mamontov.stepan.server.model.Challenge;
import mamontov.stepan.server.model.InternalChallenge;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ChallengeService {

    @Value("${challenge.limit}")
    int limit;

    private int failedRequests = 0;
    private static final int COMPLEXITY = 5;
    private static final int ALLOWED_SKEW = 10;
    private static final String controlSting = "0";
    private static final Map<String, InternalChallenge> challenges = new ConcurrentHashMap<>();

    public Optional<Challenge> challengeIfNeeded() {
        log.info("Check if challenge is needed");
        if (failedRequests > limit) {
            log.info("Need to produce a challenge");
            //final var prefix = UUID.randomUUID().toString().substring(0, 5);
            final var prefix = "ef1sr";
            final var challenge = new Challenge(prefix, COMPLEXITY);
            log.info("Created challenge with prefix={} and complexity={}", prefix, COMPLEXITY);
            challenges.put(prefix, new InternalChallenge(challenge, LocalDateTime.now().plusSeconds(ALLOWED_SKEW)));
            return Optional.of(challenge);
        } else {
            return Optional.empty();
        }
    }

    public boolean isChallengeCompleted(String prefix, String result) {
        if (!result.startsWith(prefix)) {
            log.info("Prefix={} does not match result={}", prefix, result);
            return false;
        }
        final var internalChallenge = challenges.get(prefix);
        if (internalChallenge == null) {
            log.info("No such challenge for prefix={}", prefix);
            return false;
        } else {
            final var challenge = internalChallenge.getChallenge();
            final var hash = DigestUtils.sha256Hex(result);
            log.info("Result for challenge: prefix={}, result={}, hash={}", prefix, result, hash);
            if (!hash.startsWith(controlSting)) {
                return false;
            } else {
                return true;
            }
        }
    }

    public void addNotAuthenticatedCounter() {
        failedRequests++;
    }

    public void deleteExpiredChallenges() {
        challenges.forEach((s, internalChallenge) -> {
            if (internalChallenge.getExpirationDate().isBefore(LocalDateTime.now())) {
                challenges.remove(s);
            }
        });
        if (failedRequests >= 10) {
            failedRequests = failedRequests - 10;
        } else {
            failedRequests = 0;
        }
    }
}

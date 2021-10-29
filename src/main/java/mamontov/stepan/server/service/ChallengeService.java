package mamontov.stepan.server.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mamontov.stepan.server.model.Challenge;
import mamontov.stepan.server.model.InternalChallenge;
import mamontov.stepan.server.model.User;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.KeySpec;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ChallengeService {

    @Value("${challenge.limit}")
    int limit;

    @Value("${challenge.prefix.length}")
    int prefixLength;

    @Value("${challenge.prefix.complexity}")
    int complexity;

    @Value("${challenge.expiration}")
    int expiration;

    private int failedRequests = 0;
    private static final byte[] SALT = {1,0,1,0, 0,0,1,1, 0,1,1,1, 1,1,1,1};
    private static final Map<String, InternalChallenge> challenges = new ConcurrentHashMap<>();

    public Optional<Challenge> challengeIfNeeded(User user) {
        log.info("Check if challenge is needed");
        if (failedRequests > limit) {
            log.info("Need to produce a challenge");
            final var prefix = UUID.randomUUID().toString().substring(0, prefixLength);
            //final var prefix = "ef1sr";
            final var challenge = new Challenge(prefix, complexity);
            log.info("Created challenge with prefix={} and complexity={}", prefix, complexity);
            challenges.put(prefix, new InternalChallenge(user, challenge, LocalDateTime.now().plusSeconds(expiration)));
            return Optional.of(challenge);
        } else {
            return Optional.empty();
        }
    }

    @SneakyThrows
    public Optional<User> isChallengeCompleted(String prefix, String result) {
        if (!result.startsWith(prefix)) {
            log.info("Prefix={} does not match result={}", prefix, result);
            return Optional.empty();
        }
        final var internalChallenge = challenges.get(prefix);
        if (internalChallenge == null) {
            log.info("No such challenge for prefix={}", prefix);
            return Optional.empty();
        } else {
            final var challenge = internalChallenge.getChallenge();
            KeySpec spec = new PBEKeySpec(result.toCharArray(), SALT, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            final var hash = Hex.encodeHexString(factory.generateSecret(spec).getEncoded());
            log.info("Result for challenge: prefix={}, result={}, hash={}", prefix, result, hash);
            final var controlString = getControlSting(challenge.getComplexity());
            if (!hash.startsWith(controlString)) {
                return Optional.empty();
            } else {
                return Optional.of(internalChallenge.getUser());
            }
        }
    }

    private String getControlSting(int complexity) {
        return "0".repeat(complexity);
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
        if (failedRequests > 10) {
            failedRequests = failedRequests - 10;
        } else {
            failedRequests = 0;
        }
    }
}

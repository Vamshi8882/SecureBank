package com.example.bank.security;

import com.example.bank.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory lockout. Use Redis/DB if you run more than one instance. */
@Service
public class LoginAttemptService {
    private static final int MAX_FAILURES = 5;
    private static final Duration LOCK_TIME = Duration.ofMinutes(15);

    private record State(int fails, Instant lockedUntil) {}

    private final ConcurrentHashMap<String, State> attempts = new ConcurrentHashMap<>();

    public void checkAllowed(String key) {
        State s = attempts.get(key);
        if (s != null && s.lockedUntil() != null && Instant.now().isBefore(s.lockedUntil())) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "Too many failed attempts. Try again later.");
        }
    }

    public void recordFailure(String key) {
        attempts.merge(key, new State(1, null), (old, ignored) -> {
            int f = old.fails() + 1;
            return f >= MAX_FAILURES ? new State(0, Instant.now().plus(LOCK_TIME)) : new State(f, null);
        });
    }

    public void reset(String key) {
        attempts.remove(key);
    }
}

package com.github.rodrigo_sp17.mscheduler.push;

import com.github.rodrigo_sp17.mscheduler.push.events.PushEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PushService {
    private final long PING_DELAY_MS = 2000;
    private final long PING_INTERVAL_MS = 15000;
    private final long SSE_TIMEOUT_MS = Duration.ofMinutes(10).toMillis();
    private final ConcurrentHashMap<String, SseEmitter> emitters;

    public PushService() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::ping,
                PING_DELAY_MS,
                PING_INTERVAL_MS,
                TimeUnit.MILLISECONDS);
        this.emitters = new ConcurrentHashMap<>();
    }

    /**
     * Subscribes a user to SSE events.
     * @param username the username of the AppUser to subscribe
     * @return the SSE emitter for the user
     */
    public SseEmitter subscribe(String username) {
        var emitter = new SseEmitter(SSE_TIMEOUT_MS);
        emitter.onTimeout(() -> emitters.remove(username));
        emitter.onCompletion(() -> emitters.remove(username));
        emitter.onError(e -> {
            log.debug("Exception creating emitter", e);
            emitters.remove(username);
        });

        emitters.put(username, emitter);
        return emitter;
    }

    /**
     * Pushes a SSE to a subscribed user.
     * @param username the username of the AppUser to send the event to
     * @param event the PushEvent object of the specific event to send
     */
    @Async
    public void pushNotification(String username, PushEvent event) {
        var emitter = emitters.get(username);
        if (emitter == null) {
            return;
        }

        try {
            emitter.send(event.toSseEvent());
        } catch (IOException e) {
            log.debug("Could not send event for user " + username);
            emitters.remove(username);
        }
    }

    // Pings all subscribers at regular intervals to prevent disconnection
    private void ping() {
        emitters.forEachValue(Long.MAX_VALUE, (emitter) -> {
            try {
                emitter.send(SseEmitter.event().name("PING"));
                log.debug("heartbeat sent for {}", emitter);
            } catch (IOException e) {
                log.debug("Failed to send heartbeat");
            }
        });
    }
}

package com.github.rodrigo_sp17.mscheduler.push.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PushEvent {
    private String type;
    private Map<String, Object> body;

    public SseEmitter.SseEventBuilder toSseEvent() {
        return SseEmitter.event().name(type).data(body);
    }
}

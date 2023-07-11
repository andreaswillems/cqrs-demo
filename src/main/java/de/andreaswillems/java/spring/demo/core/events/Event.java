package de.andreaswillems.java.spring.demo.core.events;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
public final class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_generator")
    @SequenceGenerator(name = "event_generator", sequenceName = "events_seq", allocationSize = 1)
    public long id;

    int schemaVersion = 1;
    public UUID aggregateId;
    public LocalDateTime timestamp;
    public EventType eventType;
    public String payload;

    private Event() {}

    public Event(UUID aggregateId, EventType eventType, String payload) {
        this.aggregateId = aggregateId;
        this.timestamp = LocalDateTime.now();
        this.eventType = eventType;
        this.payload = payload;
    }
}

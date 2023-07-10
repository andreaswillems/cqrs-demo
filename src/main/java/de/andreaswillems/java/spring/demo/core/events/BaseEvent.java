package de.andreaswillems.java.spring.demo.core.events;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "base_event")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type_id", discriminatorType = DiscriminatorType.INTEGER)
public abstract class BaseEvent {

    @Id
    @GeneratedValue
    public UUID eventId;
    public final UUID aggregateId;
    final LocalDateTime timestamp;
    public final EventType eventType;
    public final String payload;

    public BaseEvent(UUID aggregateId, EventType eventType, String payload) {
        this.aggregateId = aggregateId;
        this.timestamp = LocalDateTime.now();
        this.eventType = eventType;
        this.payload = payload;
    }
}

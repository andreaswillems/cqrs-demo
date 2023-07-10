package de.andreaswillems.java.spring.demo.core.events;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
@DiscriminatorValue("1")
public class TaskCreatedEvent extends BaseEvent {

    private TaskCreatedEvent() {
        super(null, null, null);
    }

    public TaskCreatedEvent(UUID taskId, String payload) {
        super(taskId, EventType.TASK_CREATED, payload);
    }
}

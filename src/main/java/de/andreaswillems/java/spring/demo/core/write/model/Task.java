package de.andreaswillems.java.spring.demo.core.write.model;

import java.util.UUID;

public class Task {
    UUID id;
    String title;
    TaskStatus status;

    public Task(String title) {
        this(UUID.randomUUID(), title, TaskStatus.TODO);
    }

    public Task(UUID id, String title, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void begin() {
        this.status = TaskStatus.IN_PROGRESS;
    }

    public void reset() {
        this.status = TaskStatus.TODO;
    }

    public void complete() {
        this.status = TaskStatus.COMPLETED;
    }
}

package de.andreaswillems.java.spring.demo.core.write.model;

import java.util.UUID;

public class Task {
    UUID id;
    String title;
    TaskStatus status;

    public Task(String title) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.status = TaskStatus.TODO;
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

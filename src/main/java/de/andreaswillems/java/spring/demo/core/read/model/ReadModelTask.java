package de.andreaswillems.java.spring.demo.core.read.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "read_model_task")
public final class ReadModelTask {
    @Id
    private String id;
    private String title;
    private String status;

    private ReadModelTask() {}

    public ReadModelTask(String id, String title, String status) {
        this.id = id;
        this.title = title;
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ReadModelTask) obj;
        return Objects.equals(this.id, that.id) &&
            Objects.equals(this.title, that.title) &&
            Objects.equals(this.status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, status);
    }

    @Override
    public String toString() {
        return "ReadModelTask[" +
            "id=" + id + ", " +
            "title=" + title + ", " +
            "status=" + status + ']';
    }
}

package de.andreaswillems.java.spring.demo.core.write;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.andreaswillems.java.spring.demo.core.events.Event;
import de.andreaswillems.java.spring.demo.core.events.EventStore;
import de.andreaswillems.java.spring.demo.core.write.commands.*;
import de.andreaswillems.java.spring.demo.core.write.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static de.andreaswillems.java.spring.demo.core.events.EventType.TASK_CREATED;

@Component
public class TaskCommandHandler {

    private final Logger logger = LoggerFactory.getLogger(TaskCommandHandler.class);

    private final EventStore eventStore;
    private final TaskEventPublisher taskEventPublisher;

    @Autowired
    public TaskCommandHandler(EventStore eventStore, TaskEventPublisher taskEventPublisher) {
        this.eventStore = eventStore;
        this.taskEventPublisher = taskEventPublisher;
    }

    public UUID createTask(CreateTaskCommand command) {
        logger.debug("Handling command of type {}", command.getClass().getSimpleName());
        Task task = new Task(command.getTaskTitle());
        Event taskCreatedEvent = new Event(task.getId(), TASK_CREATED, toJson(task));
        // persist taskCreatedEvent to database
        Event stored = eventStore.add(taskCreatedEvent);
        // publish event for updates on the query side
        taskEventPublisher.publishEvent(stored);

        return task.getId();
    }

    public void updateTaskTitle(UpdateTaskTitleCommand command) {
        logger.debug("Handling command of type {}", command.getClass().getSimpleName());
        // TODO
    }

    public void beginTask(BeginTaskCommand command) {
        // TODO
    }

    public void resetTask(ResetTaskCommand command) {
        // TODO
    }

    public void completeTask(CompleteTaskCommand command) {
        // TODO
    }

    private String toJson(Task task) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(task);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

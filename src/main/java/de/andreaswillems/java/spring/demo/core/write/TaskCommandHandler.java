package de.andreaswillems.java.spring.demo.core.write;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.andreaswillems.java.spring.demo.core.events.BaseEvent;
import de.andreaswillems.java.spring.demo.core.events.EventStore;
import de.andreaswillems.java.spring.demo.core.events.TaskCreatedEvent;
import de.andreaswillems.java.spring.demo.core.write.commands.*;
import de.andreaswillems.java.spring.demo.core.write.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public void createTask(CreateTaskCommand command) {
        logger.debug("Handling command of type {}", command.getClass().getSimpleName());
        Task task = new Task(command.getTaskTitle());
        BaseEvent taskCreatedEvent = new TaskCreatedEvent(task.getId(), toJson(task));
        // persist taskCreatedEvent to database
        BaseEvent stored = eventStore.add(taskCreatedEvent);
        // publish event for updates on the query side
        taskEventPublisher.publishEvent(stored);
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

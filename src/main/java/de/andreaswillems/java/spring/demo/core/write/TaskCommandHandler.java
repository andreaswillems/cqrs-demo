package de.andreaswillems.java.spring.demo.core.write;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.andreaswillems.java.spring.demo.core.events.Event;
import de.andreaswillems.java.spring.demo.core.events.EventStore;
import de.andreaswillems.java.spring.demo.core.exceptions.DifferentAggregateException;
import de.andreaswillems.java.spring.demo.core.exceptions.TaskNotCreatedException;
import de.andreaswillems.java.spring.demo.core.write.commands.*;
import de.andreaswillems.java.spring.demo.core.write.model.Task;
import de.andreaswillems.java.spring.demo.core.write.model.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static de.andreaswillems.java.spring.demo.core.events.EventType.TASK_COMPLETED;
import static de.andreaswillems.java.spring.demo.core.events.EventType.TASK_CREATED;

@Component
public class TaskCommandHandler {

    private final Logger logger = LoggerFactory.getLogger(TaskCommandHandler.class);

    private final EventStore eventStore;
    private final TaskEventPublisher taskEventPublisher;

    private final ObjectMapper objectMapper;

    @Autowired
    public TaskCommandHandler(EventStore eventStore, TaskEventPublisher taskEventPublisher, ObjectMapper objectMapper) {
        this.eventStore = eventStore;
        this.taskEventPublisher = taskEventPublisher;
        this.objectMapper = objectMapper;
    }

    public UUID createTask(CreateTaskCommand command) {
        logger.debug("Handling command of type {}", command.getClass().getSimpleName());
        Task task = new Task(command.taskTitle());
        Event stored = eventStore.add(new Event(task.getId(), TASK_CREATED, toJson(task)));
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
        logger.debug("Handling command of type {}", command.getClass().getSimpleName());
        List<Event> eventsForAggregate = eventStore.eventsForAggregateId(command.taskId());
        Task task = rehydrateTask(eventsForAggregate);
        task.complete();
        Event stored = eventStore.add(new Event(task.getId(), TASK_COMPLETED, toJson(task)));
        taskEventPublisher.publishEvent(stored);
    }

    private String toJson(Task task) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(task);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Task rehydrateTask(List<Event> events) throws TaskNotCreatedException, DifferentAggregateException {
        logger.info("Rehydrating task");
        Task task = null;

        try {
            for (Event ev : events) {
                switch (ev.eventType) {
                    case TASK_CREATED -> {
                        JsonNode jsonNode = objectMapper.readTree(ev.payload);
                        UUID id = UUID.fromString(jsonNode.get("id").asText());
                        String title = jsonNode.get("title").asText();
                        TaskStatus status = TaskStatus.valueOf(jsonNode.get("status").asText());
                        task = new Task(id, title, status);
                    }

                    case TASK_COMPLETED -> {
                        if (Objects.isNull(task)) {
                            throw new TaskNotCreatedException("Task not created");
                        }
                        JsonNode jsonNode = objectMapper.readTree(ev.payload);
                        UUID id = UUID.fromString(jsonNode.get("id").asText());
                        if (!Objects.equals(id, task.getId())) {
                            throw new DifferentAggregateException("Different aggregate ids");
                        }
                        task.complete();
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new TaskNotCreatedException(e.getMessage());
        }

        return task;
    }
}

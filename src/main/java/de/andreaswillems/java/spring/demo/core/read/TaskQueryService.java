package de.andreaswillems.java.spring.demo.core.read;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.andreaswillems.java.spring.demo.core.events.Event;
import de.andreaswillems.java.spring.demo.core.events.EventRepository;
import de.andreaswillems.java.spring.demo.core.exceptions.DifferentAggregateException;
import de.andreaswillems.java.spring.demo.core.exceptions.TaskNotCreatedException;
import de.andreaswillems.java.spring.demo.core.read.model.ReadModelTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Component
public class TaskQueryService {

    private final Logger logger = LoggerFactory.getLogger(TaskQueryService.class);
    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public TaskQueryService(EventRepository repository, ObjectMapper objectMapper) {
        this.eventRepository = repository;
        this.objectMapper = objectMapper;
    }

    public ReadModelTask getTaskById(UUID taskId) throws TaskNotCreatedException {
        List<Event> eventsForAggregate = eventRepository.findByAggregateIdOrderByTimestampAsc(taskId);
        ReadModelTask readModelTask = rehydrateTask(eventsForAggregate);
        if (Objects.isNull(readModelTask)) throw new TaskNotCreatedException("Task was not created");
        return readModelTask;
    }

    public List<ReadModelTask> getOpenTasks() {
        return Collections.emptyList();
    }

    public List<ReadModelTask> getAllTasks() {
        Collection<UUID> aggregateIds = eventRepository.findDistinctAggregateIds();
        return aggregateIds.stream()
            .map(this::taskById)
            .filter(Objects::nonNull)
            .toList();
    }

    private ReadModelTask taskById(UUID id) {
        try {
            return getTaskById(id);
        } catch (TaskNotCreatedException e) {
            return null;
        }
    }

    private ReadModelTask rehydrateTask(List<Event> events) throws TaskNotCreatedException, DifferentAggregateException {
        logger.info("Rehydrating task");
        ReadModelTask task = null;

        try {
            for (Event ev : events) {
                switch (ev.eventType) {
                    case TASK_CREATED -> {
                        JsonNode jsonNode = objectMapper.readTree(ev.payload);
                        String id = jsonNode.get("id").asText();
                        String title = jsonNode.get("title").asText();
                        String status = jsonNode.get("status").asText();
                        task = new ReadModelTask(id, title, status);
                    }

                    case TASK_COMPLETED -> {
                        if (Objects.isNull(task)) {
                            throw new TaskNotCreatedException("Task not created");
                        }
                        JsonNode jsonNode = objectMapper.readTree(ev.payload);
                        String status = jsonNode.get("status").asText();
                        task = new ReadModelTask(task.id(), task.title(), status);
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new TaskNotCreatedException(e.getMessage());
        }

        return task;
    }
}

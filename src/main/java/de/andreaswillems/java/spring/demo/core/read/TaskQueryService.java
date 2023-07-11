package de.andreaswillems.java.spring.demo.core.read;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.andreaswillems.java.spring.demo.core.events.Event;
import de.andreaswillems.java.spring.demo.core.events.EventRepository;
import de.andreaswillems.java.spring.demo.core.exceptions.TaskNotCreatedException;
import de.andreaswillems.java.spring.demo.core.read.model.ReadModelTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class TaskQueryService {

    // private ReadModelTaskRepository repository;
    private final EventRepository eventRepository;

    @Autowired
    public TaskQueryService(EventRepository repository) {
        this.eventRepository = repository;
    }

    public ReadModelTask getTaskById(UUID taskId) throws TaskNotCreatedException {
        List<Event> eventsForAggregate = eventRepository.findByAggregateIdOrderByTimestampAsc(taskId);
        ObjectMapper objectMapper = new ObjectMapper();
        ReadModelTask readModelTask = null;

        try {
            for (Event ev : eventsForAggregate) {
                switch (ev.eventType) {
                    case TASK_CREATED -> {
                        JsonNode jsonNode = objectMapper.readTree(ev.payload);;
                        String id = jsonNode.get("id").asText();
                        String title = jsonNode.get("title").asText();
                        String status = jsonNode.get("status").asText();
                        readModelTask = new ReadModelTask(id, title, status);
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new TaskNotCreatedException(e.getMessage());
        }
        if (Objects.isNull(readModelTask)) throw new TaskNotCreatedException("Task was not created");
        return readModelTask;
    }

    public List<ReadModelTask> getOpenTasks() {
        return Collections.emptyList();
    }

    public List<ReadModelTask> getAllTasks() {
        Collection<UUID> aggregateIds = eventRepository.findDistinctAggregateIds();
        List<ReadModelTask> tasks = aggregateIds.stream().map(id -> {
            try {
                return getTaskById(id);
            } catch (TaskNotCreatedException e) {
                return null;
            }
        }).filter(Objects::nonNull).toList();
        return tasks;
    }
}

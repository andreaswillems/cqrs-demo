package de.andreaswillems.java.spring.demo.core.read;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.andreaswillems.java.spring.demo.core.events.BaseEvent;
import de.andreaswillems.java.spring.demo.core.read.model.ReadModelTask;
import de.andreaswillems.java.spring.demo.core.read.queries.ReadModelTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskEventHandler {

    private final Logger logger = LoggerFactory.getLogger(TaskEventHandler.class);
    private final ReadModelTaskRepository repository;

    @Autowired
    public TaskEventHandler(ReadModelTaskRepository repository) {
        this.repository = repository;
    }

    public void handleEvent(BaseEvent baseEvent) {
        logger.info("Handling event of type {}", baseEvent.eventType);
        switch (baseEvent.eventType) {
            case TASK_CREATED -> {
                handleTaskCreated(baseEvent);
            }
            default -> logger.info("Event type {} unknown", baseEvent.eventType);
        }
    }

    public void handleTaskCreated(BaseEvent event) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(event.payload);
            String id = jsonNode.get("id").asText();
            String title = jsonNode.get("title").asText();
            String status = jsonNode.get("status").asText();
            ReadModelTask task = new ReadModelTask(id, title, status);
            logger.info("Persisting read model task with id {}", id);
            repository.save(task);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

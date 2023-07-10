package de.andreaswillems.java.spring.demo.core.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class EventStore {
    private final Logger logger = LoggerFactory.getLogger(EventStore.class);
    private final EventRepository repository;

    @Autowired
    private EventStore(EventRepository repository) {
        this.repository = repository;
    }

    public BaseEvent add(BaseEvent event) {
        logger.info("Persisting event of type {}", event.eventType);
        return repository.save(event);
    }

    public List<BaseEvent> getEvents() {
        logger.debug("Fetching events");
        return StreamSupport.stream(repository.findAll().spliterator(), false).collect(Collectors.toList());
    }
}

package de.andreaswillems.java.spring.demo.core.write;

import de.andreaswillems.java.spring.demo.core.events.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class TaskEventPublisher {

    private final Logger logger = LoggerFactory.getLogger(TaskEventPublisher.class);
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public TaskEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishEvent(Event event) {
        logger.info("Publishing event [type {}, id {}]", event.eventType, event.id);
        applicationEventPublisher.publishEvent(event);
    }
}

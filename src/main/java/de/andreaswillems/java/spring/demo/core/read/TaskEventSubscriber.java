package de.andreaswillems.java.spring.demo.core.read;

import de.andreaswillems.java.spring.demo.core.events.BaseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TaskEventSubscriber {

    private final Logger logger = LoggerFactory.getLogger(TaskEventSubscriber.class);

    private final TaskEventHandler taskEventHandler;

    @Autowired
    public TaskEventSubscriber(TaskEventHandler taskEventHandler) {
        this.taskEventHandler = taskEventHandler;
    }

    @Async
    @EventListener
    public void onApplicationEvent(BaseEvent event) {
        logger.info("Received event of type {}", event.eventType);
        taskEventHandler.handleEvent(event);
    }
}

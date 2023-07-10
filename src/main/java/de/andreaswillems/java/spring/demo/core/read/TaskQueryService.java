package de.andreaswillems.java.spring.demo.core.read;

import de.andreaswillems.java.spring.demo.core.read.model.ReadModelTask;
import de.andreaswillems.java.spring.demo.core.read.queries.ReadModelTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class TaskQueryService {

    private ReadModelTaskRepository repository;

    @Autowired
    public TaskQueryService(ReadModelTaskRepository repository) {
        this.repository = repository;
    }

    public ReadModelTask getTaskById(String taskId) {
        return null;
    }

    public List<ReadModelTask> getOpenTasks() {
        return Collections.emptyList();
    }

    public List<ReadModelTask> getAllTasks() {
        return StreamSupport.stream(repository.findAll().spliterator(), false).collect(Collectors.toList());
    }
}

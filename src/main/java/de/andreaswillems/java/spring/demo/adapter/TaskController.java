package de.andreaswillems.java.spring.demo.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.andreaswillems.java.spring.demo.core.read.TaskQueryService;
import de.andreaswillems.java.spring.demo.core.read.model.ReadModelTask;
import de.andreaswillems.java.spring.demo.core.write.TaskCommandHandler;
import de.andreaswillems.java.spring.demo.core.write.commands.CreateTaskCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "tasks")
public class TaskController {

    private final TaskCommandHandler taskCommandHandler;
    private final TaskQueryService taskQueryService;

    @Autowired
    public TaskController(TaskCommandHandler taskCommandHandler, TaskQueryService taskQueryService) {
        this.taskCommandHandler = taskCommandHandler;
        this.taskQueryService = taskQueryService;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskCreatedResponse> createTask(@RequestBody CreateTaskRequest createTaskRequest) {
        CreateTaskCommand command = new CreateTaskCommand(createTaskRequest.title());
        UUID taskId = taskCommandHandler.createTask(command);
        return ResponseEntity.ok().body(new TaskCreatedResponse(taskId));
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAllTasks() {
        List<ReadModelTask> tasks = taskQueryService.getAllTasks();
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> stringStream = tasks.stream().map(task -> {
            try {
                return objectMapper.writeValueAsString(task);
            } catch (JsonProcessingException e) {
                return null;
            }
        }).filter(Objects::nonNull).toList();
        return ResponseEntity.ok().body(stringStream);
    }

    @GetMapping(path = "/{taskId}")
    public ResponseEntity<String> getSingleTask(@PathVariable(name = "taskId") UUID taskId) throws JsonProcessingException {
        ReadModelTask task = taskQueryService.getTaskById(taskId);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(task);
        return ResponseEntity.ok().body(json);
    }
}

package de.andreaswillems.java.spring.demo.adapter;

import de.andreaswillems.java.spring.demo.core.read.TaskQueryService;
import de.andreaswillems.java.spring.demo.core.read.model.ReadModelTask;
import de.andreaswillems.java.spring.demo.core.write.TaskCommandHandler;
import de.andreaswillems.java.spring.demo.core.write.commands.CreateTaskCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody CreateTaskDto createTaskDto) {
        CreateTaskCommand command = new CreateTaskCommand(createTaskDto.title());
        taskCommandHandler.createTask(command);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<ReadModelTask> getAllTasks() {
        return taskQueryService.getAllTasks();
    }
}

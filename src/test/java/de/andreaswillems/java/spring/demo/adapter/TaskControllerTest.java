package de.andreaswillems.java.spring.demo.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.andreaswillems.java.spring.demo.core.events.Event;
import de.andreaswillems.java.spring.demo.core.events.EventRepository;
import de.andreaswillems.java.spring.demo.core.write.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static de.andreaswillems.java.spring.demo.core.events.EventType.TASK_CREATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestDatabase
public class TaskControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        String body = """
        {
          "title": "Todo Zero"
        }
        """;
        restTemplate.postForEntity(
            "/tasks",
            new HttpEntity<>(body, headers),
            String.class
        );
    }

    @AfterEach
    void tearDown() {
        eventRepository.deleteAll();
    }

    @Test
    @DisplayName("A task with the given title is created")
    void createTask() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        String body = """
        {
          "title": "Todo One"
        }
        """;
        ResponseEntity<TaskCreatedResponse> response = restTemplate.postForEntity(
            "/tasks",
            new HttpEntity<>(body, headers),
            TaskCreatedResponse.class
        );

        assertThat(response.getStatusCode().isSameCodeAs(OK)).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().taskId()).isNotNull();
    }

    @Test
    @DisplayName("A single task is fetched.")
    void fetchSingleTask() {
        Task task = new Task("Title");
        Event taskCreatedEvent = new Event(task.getId(), TASK_CREATED, toJson(task));
        eventRepository.save(taskCreatedEvent);

        ResponseEntity<SingleTaskResponse> response =
            restTemplate.getForEntity("/tasks/" + task.getId().toString(), SingleTaskResponse.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @DisplayName("All tasks are fetched")
    void fetchTasks() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        String body = """
        {
          "title": "Todo One"
        }
        """;
        restTemplate.postForEntity("/tasks", new HttpEntity<>(body, headers), String.class);

        // Act
        ResponseEntity<ListTasksResponse> response = restTemplate.getForEntity("/tasks", ListTasksResponse.class);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().tasks().size()).isEqualTo(2);
    }

    private String toJson(Task task) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(task);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

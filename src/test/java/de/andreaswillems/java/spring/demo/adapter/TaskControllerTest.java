package de.andreaswillems.java.spring.demo.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.andreaswillems.java.spring.demo.core.events.Event;
import de.andreaswillems.java.spring.demo.core.events.EventRepository;
import de.andreaswillems.java.spring.demo.core.write.model.Task;
import de.andreaswillems.java.spring.demo.core.write.model.TaskStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.Optional;

import static de.andreaswillems.java.spring.demo.core.events.EventType.TASK_CREATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestDatabase
public class TaskControllerTest {

    private final EventRepository eventRepository;

    private final WebClient webClient;

    @Autowired
    public TaskControllerTest(ServletWebServerApplicationContext webServerAppCtx, EventRepository eventRepository) {
        this.eventRepository = eventRepository;

        int port = webServerAppCtx.getWebServer().getPort();

        URI uri = URI.create(
            "http://localhost:" + port + "/tasks"
        );

        this.webClient = WebClient
            .builder()
            .baseUrl(uri.toString())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    @BeforeEach
    void setUp() {
        String body = """
        {
          "title": "Todo Zero"
        }
        """;
        webClient.post().bodyValue(body).retrieve();
    }

    @AfterEach
    void tearDown() {
        eventRepository.deleteAll();
    }

    @Test
    @DisplayName("A task with the given title is created")
    void createTask() {
        String body = """
        {
          "title": "Todo One"
        }
        """;

        WebClient.RequestHeadersSpec<?> headersSpec = webClient.post().bodyValue(body);
        Optional<TaskCreatedResponse> response = headersSpec.exchangeToMono(clientResponse -> {
           assertThat(clientResponse.statusCode().isSameCodeAs(OK)).isTrue();
           return clientResponse.bodyToMono(TaskCreatedResponse.class);
        }).blockOptional();

        assertThat(response.isPresent()).isTrue();
        assertThat(response.get().taskId()).isNotNull();
    }

    @Test
    @DisplayName("A single task is fetched.")
    void fetchSingleTask() {
        Task task = new Task("Title");
        Event taskCreatedEvent = new Event(task.getId(), TASK_CREATED, toJson(task));
        eventRepository.save(taskCreatedEvent);

        WebClient.RequestHeadersSpec<?> headersSpec = webClient.get().uri("/{taskId}", task.getId());
        Optional<JsonNode> response = headersSpec.exchangeToMono(clientResponse -> {
            assertThat(clientResponse.statusCode().isSameCodeAs(OK)).isTrue();
            return clientResponse.bodyToMono(JsonNode.class);
        }).blockOptional();

        assertThat(response.isPresent()).isTrue();
        assertThat(response.get().get("id").asText()).isEqualTo(task.getId().toString());
    }

    @Test
    @DisplayName("All tasks are fetched")
    void fetchTasks() {
        // Arrange
        String body = """
        {
          "title": "Todo One"
        }
        """;
        webClient.post().bodyValue(body).retrieve();

        // Act
        WebClient.RequestHeadersSpec<?> headersSpec = webClient.get();
        Optional<JsonNode> response = headersSpec.exchangeToMono(clientResponse -> {
            assertThat(clientResponse.statusCode().isSameCodeAs(OK)).isTrue();
            return clientResponse.bodyToMono(JsonNode.class);
        }).blockOptional();

        // Assert
        assertThat(response.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Task is completed")
    void completeTask() {
        // arrange
        Task task = new Task("Title");
        Event taskCreatedEvent = new Event(task.getId(), TASK_CREATED, toJson(task));
        eventRepository.save(taskCreatedEvent);

        // act
        WebClient.RequestHeadersSpec<?> headersSpec = webClient.put().uri("/{taskId}/complete", task.getId());
        headersSpec.exchangeToMono(clientResponse -> {
            // assert
            assertThat(clientResponse.statusCode().isSameCodeAs(NO_CONTENT)).isTrue();
            return clientResponse.bodyToMono(String.class);
        }).block();

        // assert
        WebClient.RequestHeadersSpec<?> getHeadersSpec = webClient.get().uri("/{taskId}", task.getId());
        Optional<JsonNode> getResponse = getHeadersSpec.exchangeToMono(clientResponse -> {
            assertThat(clientResponse.statusCode().isSameCodeAs(OK)).isTrue();
            return clientResponse.bodyToMono(JsonNode.class);
        }).blockOptional();

        assertThat(getResponse.isPresent()).isTrue();
        assertThat(getResponse.get().get("status").asText()).isEqualTo(TaskStatus.COMPLETED.toString());
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

package de.andreaswillems.java.spring.demo.adapter;

import de.andreaswillems.java.spring.demo.core.events.EventRepository;
import de.andreaswillems.java.spring.demo.core.read.model.ReadModelTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
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
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/tasks",
            new HttpEntity<>(body, headers),
            String.class
        );

        assertThat(response.getStatusCode().isSameCodeAs(HttpStatus.NO_CONTENT)).isTrue();
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
        ResponseEntity<ReadModelTask[]> response = restTemplate.getForEntity("/tasks", ReadModelTask[].class);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(2);
    }
}

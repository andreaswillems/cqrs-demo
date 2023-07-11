package de.andreaswillems.java.spring.demo.core.exceptions;

public class TaskNotCreatedException extends RuntimeException {
    public TaskNotCreatedException(String message) {
        super(message);
    }
}

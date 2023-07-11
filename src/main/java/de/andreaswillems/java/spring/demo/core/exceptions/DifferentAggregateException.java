package de.andreaswillems.java.spring.demo.core.exceptions;

public class DifferentAggregateException extends RuntimeException {

    public DifferentAggregateException(String message) {
        super(message);
    }
}

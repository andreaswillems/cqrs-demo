package de.andreaswillems.java.spring.demo.core.write.commands;

import java.util.UUID;

public record CompleteTaskCommand(UUID taskId) {}

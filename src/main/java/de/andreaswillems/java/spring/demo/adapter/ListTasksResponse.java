package de.andreaswillems.java.spring.demo.adapter;

import de.andreaswillems.java.spring.demo.core.read.model.ReadModelTask;

import java.util.List;

public record ListTasksResponse(List<ReadModelTask> tasks) {}

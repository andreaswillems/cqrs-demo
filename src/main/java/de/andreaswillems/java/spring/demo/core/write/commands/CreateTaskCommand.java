package de.andreaswillems.java.spring.demo.core.write.commands;

public class CreateTaskCommand {
    String taskTitle;

    public CreateTaskCommand(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskTitle() {
        return taskTitle;
    }
}

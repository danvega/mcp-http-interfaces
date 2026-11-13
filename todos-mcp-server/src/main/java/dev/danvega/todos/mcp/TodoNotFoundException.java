package dev.danvega.todos.mcp;

public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(String path) {
        super("No to-do found at " + path);
    }

}

package dev.danvega.todos.mcp;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpTool.McpAnnotations;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MCP tools for the to-dos API. Each tool is a thin wrapper around the HTTP interface.
 */
@Component
public class TodoTools {

    private final TodoClient todoClient;

    public TodoTools(TodoClient todoClient) {
        this.todoClient = todoClient;
    }

    @McpTool(name = "list_todos",
            description = "List every to-do with its id, title, and whether it is completed",
            annotations = @McpAnnotations(readOnlyHint = true))
    public List<Todo> listTodos() {
        return todoClient.findAll();
    }

    @McpTool(name = "get_todo",
            description = "Get a single to-do by its id",
            annotations = @McpAnnotations(readOnlyHint = true))
    public Todo getTodo(@McpToolParam(description = "The id of the to-do") Integer id) {
        return todoClient.findById(id);
    }

    @McpTool(name = "create_todo",
            description = "Create a new to-do that is not completed yet",
            annotations = @McpAnnotations(destructiveHint = false))
    public Todo createTodo(@McpToolParam(description = "What needs to be done") String title) {
        return todoClient.create(new Todo(null, title, false));
    }

    @McpTool(name = "complete_todo",
            description = "Mark a to-do as completed",
            annotations = @McpAnnotations(destructiveHint = false, idempotentHint = true))
    public Todo completeTodo(@McpToolParam(description = "The id of the to-do to complete") Integer id) {
        Todo todo = todoClient.findById(id);
        return todoClient.update(id, new Todo(id, todo.title(), true));
    }

    @McpTool(name = "delete_todo",
            description = "Delete a to-do by its id",
            annotations = @McpAnnotations(destructiveHint = true))
    public String deleteTodo(@McpToolParam(description = "The id of the to-do to delete") Integer id) {
        todoClient.delete(id);
        return "Deleted to-do " + id;
    }

}

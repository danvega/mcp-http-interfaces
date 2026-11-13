package dev.danvega.todos.mcp;

/**
 * The MCP server's own copy of the to-do shape. It does not share code with the API.
 * It only has to match the JSON.
 */
public record Todo(Integer id, String title, boolean completed) {
}

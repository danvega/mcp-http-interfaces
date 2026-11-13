# HTTP Interfaces in an MCP Server

Can you use Spring HTTP interfaces inside an MCP server? Yes. This repo has two Spring Boot apps. One is a small todos REST API. The other is a Spring AI MCP server that calls that API through a declarative HTTP interface (`@HttpExchange`), so the MCP tools never touch `RestClient` directly.

This is the `start` branch. The todos API is done. The MCP server runs, but it has no tools yet and no way to call the API. The finished code is on `main`.

## Tested with

Java 25, Spring Boot 4.1.2, Spring AI 2.0.2. Built and run on JDK 27.

## What you need

- JDK 25 or newer
- No API keys. The MCP server does not call a model. Your MCP client brings its own.

## Run it

Start the API first, then the MCP server, each in its own terminal.

```bash
./mvnw -pl todos-api spring-boot:run
```

```bash
./mvnw -pl todos-mcp-server spring-boot:run
```

| App | Port | What it is |
|-----|------|------------|
| `todos-api` | 8080 | REST API at `/api/todos`, seeded with 5 todos (in memory, reset on restart) |
| `todos-mcp-server` | 8081 | MCP server over Streamable HTTP at `/mcp`, with no tools yet |

Check the API:

```bash
curl localhost:8080/api/todos
```

## What's already here

- The [todos API](todos-api/src/main/java/dev/danvega/todos/TodoController.java) with `GET`, `POST`, `PUT` and `DELETE` on `/api/todos`.
- The MCP server app with the `spring-ai-starter-mcp-server-webmvc` starter and its [application.yaml](todos-mcp-server/src/main/resources/application.yaml).
- A [Todo](todos-mcp-server/src/main/java/dev/danvega/todos/mcp/Todo.java) record in the MCP server that matches the API's JSON.

## What you'll build

1. Add `spring-boot-starter-restclient` to the MCP server's `pom.xml`. The MCP starter does not include it, and Spring Boot only sets up HTTP interfaces when a client starter is present.
2. Write `TodoClient`, an `@HttpExchange("/api/todos")` interface with one method per endpoint.
3. Register it with `@ImportHttpServices(group = "todos", types = TodoClient.class)` and set the base URL in `application.yaml`:

   ```yaml
   spring:
     http:
       serviceclient:
         todos:
           base-url: http://localhost:8080
   ```

4. Write `TodoTools`, a bean with `@McpTool` methods (`list_todos`, `get_todo`, `create_todo`, `complete_todo`, `delete_todo`). Each one calls `TodoClient`.
5. Connect an MCP client to `http://localhost:8081/mcp` and ask it to manage your todos. For Claude Code:

   ```bash
   claude mcp add --transport http todos-demo http://localhost:8081/mcp
   ```

The finished code on `main` also has a group configurer that turns a 404 into a clear error, and a test that checks the tools with a fake `TodoClient`.

## Good to know

- In Spring AI 2.0 the MCP annotations live in `org.springframework.ai.mcp.annotation`. Older examples use `org.springaicommunity.mcp.annotation`.
- `destructiveHint` is `true` by default in `@McpTool` annotations. Set it to `false` on tools that don't delete anything.

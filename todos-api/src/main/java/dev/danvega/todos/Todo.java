package dev.danvega.todos;

import jakarta.validation.constraints.NotBlank;

public record Todo(Integer id, @NotBlank String title, boolean completed) {
}

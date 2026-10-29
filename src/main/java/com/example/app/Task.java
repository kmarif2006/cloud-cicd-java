package com.example.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Immutable Task domain object.
 * Priority: LOW, MEDIUM, HIGH
 * dueDate: optional ISO date (yyyy-MM-dd), null = no due date
 */
public record Task(
        long id,
        String title,
        boolean done,
        Priority priority,
        String dueDate  // ISO-8601 date string, nullable
) {

    /** Allowed priority levels. */
    public enum Priority { LOW, MEDIUM, HIGH }

    /** Compact canonical constructor with basic validation. */
    public Task {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title must not be empty");
        }
        if (priority == null) priority = Priority.MEDIUM;
    }

    /** Convenience factory for backward-compatible creation. */
    @JsonCreator
    public static Task of(
            @JsonProperty("id")       long   id,
            @JsonProperty("title")    String title,
            @JsonProperty("done")     boolean done,
            @JsonProperty("priority") Priority priority,
            @JsonProperty("dueDate")  String dueDate) {
        return new Task(id, title, done,
                priority == null ? Priority.MEDIUM : priority,
                dueDate);
    }
}

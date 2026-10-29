package com.example.app;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory task store.
 * Thread-safe via ConcurrentHashMap + AtomicLong.
 */
@Service
public class TaskService {

    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    // ------------------------------------------------------------------ READ

    public List<Task> findAll() {
        return tasks.values().stream()
                .sorted(Comparator.comparingLong(Task::id))
                .toList();
    }

    public Optional<Task> findById(long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    /** Returns a summary map used by the /api/stats endpoint. */
    public Map<String, Object> stats() {
        Collection<Task> all = tasks.values();
        long total  = all.size();
        long done   = all.stream().filter(Task::done).count();
        long pending = total - done;

        Map<String, Long> byPriority = all.stream()
                .collect(Collectors.groupingBy(
                        t -> t.priority().name(), Collectors.counting()));

        return Map.of(
                "total",     total,
                "done",      done,
                "pending",   pending,
                "byPriority", byPriority
        );
    }

    // ----------------------------------------------------------------- WRITE

    public Task add(String title, Task.Priority priority, String dueDate) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title must not be empty");
        }
        long id   = sequence.incrementAndGet();
        Task task = new Task(id, title.trim(), false,
                priority == null ? Task.Priority.MEDIUM : priority,
                dueDate);
        tasks.put(id, task);
        return task;
    }

    /** Backward-compatible overload used by tests. */
    public Task add(String title) {
        return add(title, Task.Priority.MEDIUM, null);
    }

    public Optional<Task> toggle(long id) {
        return Optional.ofNullable(
                tasks.computeIfPresent(id,
                        (key, t) -> new Task(t.id(), t.title(), !t.done(),
                                t.priority(), t.dueDate())));
    }

    public boolean delete(long id) {
        return tasks.remove(id) != null;
    }
}

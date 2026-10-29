package com.example.app;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {

    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public List<Task> findAll() {
        return tasks.values().stream()
                .sorted(Comparator.comparingLong(Task::id))
                .toList();
    }

    public Task add(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title must not be empty");
        }
        long id = sequence.incrementAndGet();
        Task task = new Task(id, title.trim(), false);
        tasks.put(id, task);
        return task;
    }

    public Optional<Task> toggle(long id) {
        return Optional.ofNullable(
                tasks.computeIfPresent(id, (key, t) -> new Task(t.id(), t.title(), !t.done())));
    }

    public boolean delete(long id) {
        return tasks.remove(id) != null;
    }
}

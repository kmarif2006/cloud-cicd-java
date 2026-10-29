package com.example.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    private TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService();
    }

    @Test
    void addTaskStoresTrimmedTitleAsNotDone() {
        Task t = service.add("  Learn Docker  ");
        assertEquals("Learn Docker", t.title());
        assertFalse(t.done());
        assertEquals(Task.Priority.MEDIUM, t.priority());
        assertEquals(1, service.findAll().size());
    }

    @Test
    void addTaskWithPriorityAndDueDate() {
        Task t = service.add("Deploy to AKS", Task.Priority.HIGH, "2025-12-31");
        assertEquals(Task.Priority.HIGH, t.priority());
        assertEquals("2025-12-31", t.dueDate());
    }

    @Test
    void addTaskWithBlankTitleThrows() {
        assertThrows(IllegalArgumentException.class, () -> service.add("   "));
        assertThrows(IllegalArgumentException.class, () -> service.add(null));
    }

    @Test
    void toggleFlipsDoneStatus() {
        Task t = service.add("Write report");
        assertTrue(service.toggle(t.id()).orElseThrow().done());
        assertFalse(service.toggle(t.id()).orElseThrow().done());
    }

    @Test
    void toggleUnknownIdReturnsEmpty() {
        assertTrue(service.toggle(999).isEmpty());
    }

    @Test
    void deleteRemovesTask() {
        Task t = service.add("Temp");
        assertTrue(service.delete(t.id()));
        assertFalse(service.delete(t.id()));
        assertTrue(service.findAll().isEmpty());
    }

    @Test
    void statsReturnsCorrectCounts() {
        service.add("Task A", Task.Priority.HIGH, null);
        service.add("Task B", Task.Priority.LOW, null);
        Task t = service.add("Task C", Task.Priority.HIGH, null);
        service.toggle(t.id());

        var stats = service.stats();
        assertEquals(3L, stats.get("total"));
        assertEquals(1L, stats.get("done"));
        assertEquals(2L, stats.get("pending"));
    }

    @Test
    void findByIdReturnsTask() {
        Task t = service.add("Find me");
        assertTrue(service.findById(t.id()).isPresent());
        assertTrue(service.findById(9999L).isEmpty());
    }
}

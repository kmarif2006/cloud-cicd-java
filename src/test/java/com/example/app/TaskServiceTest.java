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
        assertEquals(1, service.findAll().size());
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
}

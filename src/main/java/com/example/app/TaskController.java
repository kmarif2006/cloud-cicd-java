package com.example.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService service;
    private final String message;
    private final String version;

    public TaskController(TaskService service,
                          @Value("${app.message}") String message,
                          @Value("${app.version}") String version) {
        this.service = service;
        this.message = message;
        this.version = version;
    }

    @GetMapping("/info")
    public Map<String, String> info() {
        String host = System.getenv().getOrDefault("HOSTNAME", "local");
        return Map.of("message", message, "version", version, "host", host);
    }

    @GetMapping("/tasks")
    public List<Task> list() {
        return service.findAll();
    }

    @PostMapping("/tasks")
    public ResponseEntity<Task> add(@RequestBody Map<String, String> body) {
        Task created = service.add(body.get("title"));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/tasks/{id}/toggle")
    public ResponseEntity<Task> toggle(@PathVariable long id) {
        return service.toggle(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        return service.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}

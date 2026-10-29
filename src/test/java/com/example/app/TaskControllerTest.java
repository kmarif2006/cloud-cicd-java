package com.example.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    // ------------------------------------------------------------------- INFO

    @Test
    void infoEndpointReturnsMessageVersionAndHost() throws Exception {
        mvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.version").exists())
                .andExpect(jsonPath("$.host").exists());
    }

    // ------------------------------------------------------------------ TASKS

    @Test
    void createTaskReturns201WithPriorityAndDueDate() throws Exception {
        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Deploy to AKS\",\"priority\":\"HIGH\",\"dueDate\":\"2025-12-31\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Deploy to AKS"))
                .andExpect(jsonPath("$.done").value(false))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.dueDate").value("2025-12-31"));
    }

    @Test
    void createTaskDefaultsPriorityToMedium() throws Exception {
        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Default Priority Task\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.priority").value("MEDIUM"));
    }

    @Test
    void createTaskWithBlankTitleReturns400() throws Exception {
        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void deleteUnknownTaskReturns404() throws Exception {
        mvc.perform(delete("/api/tasks/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void toggleTaskFlipsDoneStatus() throws Exception {
        // Create first
        String resp = mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Toggle me\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extract id using simple parse
        long id = Long.parseLong(resp.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mvc.perform(put("/api/tasks/" + id + "/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(true));
    }

    // ------------------------------------------------------------------ STATS

    @Test
    void statsEndpointReturnsExpectedFields() throws Exception {
        mvc.perform(get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.done").isNumber())
                .andExpect(jsonPath("$.pending").isNumber())
                .andExpect(jsonPath("$.byPriority").isMap());
    }

    // ----------------------------------------------------------------- HEALTH

    @Test
    void healthEndpointIsUp() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}

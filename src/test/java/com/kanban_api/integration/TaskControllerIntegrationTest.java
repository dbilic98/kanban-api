package com.kanban_api.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kanban_api.controller.request.CreateTaskRequest;
import com.kanban_api.controller.request.UpdateTaskRequest;
import com.kanban_api.domain.enumeration.Priority;
import com.kanban_api.domain.enumeration.Status;
import com.kanban_api.domain.model.Task;
import com.kanban_api.domain.repository.TaskRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest extends AbstractIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void cleanDatabase() {
    taskRepository.deleteAll();
  }

  @Test
  void shouldCreateTask_whenRequestIsValid() throws Exception {

    CreateTaskRequest createTaskRequest = new CreateTaskRequest("Task 1", "Task task", Status.TO_DO,
        Priority.HIGH);

    mockMvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createTaskRequest)))
        .andExpect(status().isCreated()).andExpect(jsonPath("$.title").value("Task 1"))
        .andExpect(jsonPath("$.description").value("Task task"))
        .andExpect(jsonPath("$.status").value("TO_DO"))
        .andExpect(jsonPath("$.priority").value("HIGH"));
  }

  @Test
  void shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {

    CreateTaskRequest createTaskRequest = new CreateTaskRequest("", "Task task", Status.TO_DO,
        Priority.HIGH);

    mockMvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createTaskRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturnAllTasks() throws Exception {

    Task task1 = new Task("Task 1", "Task task", Status.TO_DO, Priority.HIGH);
    Task task2 = new Task("Task 2", "Task task", Status.TO_DO, Priority.HIGH);
    taskRepository.saveAll(List.of(task1, task2));

    mockMvc.perform(get("/api/tasks")).andExpect(status().isOk())
        .andExpect(jsonPath("$.items.length()").value(2))
        .andExpect(jsonPath("$.items[0].title").value("Task 1"))
        .andExpect(jsonPath("$.items[1].title").value("Task 2"));
  }

  @Test
  void shouldReturnTasksFilteredByStatus() throws Exception {

    Task task1 = new Task("Task 1", "Task task", Status.TO_DO, Priority.HIGH);
    Task task2 = new Task("Task 2", "Task task", Status.DONE, Priority.LOW);
    Task task3 = new Task("Task 3", "Task task", Status.DONE, Priority.MED);
    taskRepository.saveAll(List.of(task1, task2, task3));

    mockMvc.perform(get("/api/tasks").param("status", "DONE")).andExpect(status().isOk())
        .andExpect(jsonPath("$.items.length()").value(2))
        .andExpect(jsonPath("$.items[0].status").value("DONE"))
        .andExpect(jsonPath("$.items[1].status").value("DONE"));
  }

  @Test
  void shouldReturnTasksWithPagination() throws Exception {

    Task task1 = new Task("Task 1", "Task task", Status.TO_DO, Priority.HIGH);
    Task task2 = new Task("Task 2", "Task task", Status.DONE, Priority.LOW);
    Task task3 = new Task("Task 3", "Task task", Status.IN_PROGRESS, Priority.MED);
    taskRepository.saveAll(List.of(task1, task2, task3));

    mockMvc.perform(get("/api/tasks").param("pageNumber", "0").param("pageSize", "2"))
        .andExpect(status().isOk()).andExpect(jsonPath("$.items.length()").value(2))
        .andExpect(jsonPath("$.items[0].title").value("Task 1"))
        .andExpect(jsonPath("$.items[1].title").value("Task 2"));
  }

  @Test
  void shouldReturnTask_whenTaskExists() throws Exception {
    Task task = new Task("Task 1", "Task task", Status.DONE, Priority.MED);
    taskRepository.save(task);

    mockMvc.perform(get("/api/tasks/" + task.getId())).andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(task.getId()))
        .andExpect(jsonPath("$.title").value("Task 1"))
        .andExpect(jsonPath("$.description").value("Task task"))
        .andExpect(jsonPath("$.status").value("DONE"))
        .andExpect(jsonPath("$.priority").value("MED"));
  }

  @Test
  void shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
    Task task = new Task("Task 1", "Task task", Status.DONE, Priority.MED);
    taskRepository.save(task);

    mockMvc.perform(get("/api/tasks/99")).andExpect(status().isNotFound());
  }

  @Test
  void shouldUpdateTask_whenTaskExists() throws Exception {
    Task task1 = new Task("Task 1", "Task task", Status.IN_PROGRESS, Priority.MED);
    taskRepository.save(task1);

    UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest("Task 2", "Task task", Status.DONE,
        Priority.MED);

    mockMvc.perform(put("/api/tasks/" + task1.getId()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateTaskRequest))).andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Task 2"))
        .andExpect(jsonPath("$.description").value("Task task"))
        .andExpect(jsonPath("$.status").value("DONE"))
        .andExpect(jsonPath("$.priority").value("MED"));
  }

  @Test
  void shouldReturnNotFound_whenUpdatingTaskThatDoesNotExist() throws Exception {
    Task task = new Task("Task 1", "Task task", Status.DONE, Priority.MED);
    taskRepository.save(task);

    UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest("Task 2", "Updated description",
        Status.DONE, Priority.HIGH);

    mockMvc.perform(put("/api/tasks/99")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateTaskRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldPatchTask_whenTaskExists() throws Exception {
    Task task1 = new Task("Task 1", "Task task", Status.DONE, Priority.MED);
    taskRepository.save(task1);

    String patchJson = """
        {
        "title": "Task 2"
        }
        """;

    mockMvc.perform(patch("/api/tasks/" + task1.getId())
        .contentType("application/merge-patch+json")
        .content(patchJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Task 2"))
        .andExpect(jsonPath("$.description").value("Task task"))
        .andExpect(jsonPath("$.status").value("DONE"))
        .andExpect(jsonPath("$.priority").value("MED"));
  }

  @Test
  void shouldReturnNotFound_whenPatchingTaskDoesNotExist() throws Exception {
  Task task = new Task("Task 1", "Task task", Status.DONE, Priority.MED);
    taskRepository.save(task);

    String patchJson = """
    {
        "title": "Task 2"
    }
    """;

    mockMvc.perform(patch("/api/tasks/99")
            .contentType("application/merge-patch+json")
            .content(patchJson))
      .andExpect(status().isNotFound());
}

@Test
void shouldReturnBadRequest_whenPatchContainsInvalidField() throws Exception {
  Task task = new Task("Task 1", "Task task", Status.DONE, Priority.MED);
  taskRepository.save(task);

  String patchJson = """
      {
          "id": 88
      }
      """;

  mockMvc.perform(patch("/api/tasks/" + task.getId())
          .contentType("application/merge-patch+json")
          .content(patchJson))
      .andExpect(status().isBadRequest());
}

  @Test
  void shouldDeleteTask_whenTaskExists() throws Exception {
    Task task = new Task("Task 1", "Task task", Status.DONE, Priority.MED);
    taskRepository.save(task);

    mockMvc.perform(delete("/api/tasks/" + task.getId()))
        .andExpect(status().isNoContent());
}

  @Test
  void shouldReturnNotFound_whenDeleteTaskDoesNotExist() throws Exception {
    Task task = new Task("Task 1", "Task task", Status.DONE, Priority.MED);
    taskRepository.save(task);

    mockMvc.perform(delete("/api/tasks/99"))
        .andExpect(status().isNotFound());
  }
}

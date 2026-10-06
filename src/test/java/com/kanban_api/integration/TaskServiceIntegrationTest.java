package com.kanban_api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.kanban_api.domain.dto.CreateTaskDto;
import com.kanban_api.domain.dto.TaskDto;
import com.kanban_api.domain.dto.UpdateTaskDto;
import com.kanban_api.domain.enumeration.Priority;
import com.kanban_api.domain.enumeration.Status;
import com.kanban_api.domain.model.Task;
import com.kanban_api.domain.repository.TaskRepository;
import com.kanban_api.domain.service.TaskService;
import com.kanban_api.exception.InvalidPatchFieldException;
import com.kanban_api.exception.TaskNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
public class TaskServiceIntegrationTest extends AbstractIntegrationTest {

  @Autowired
  private TaskService taskService;

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void cleanDatabase() {
    taskRepository.deleteAll();
  }

  @Test
  void createTask_savesTaskToDatabase() {

    CreateTaskDto createTaskDto = new CreateTaskDto("Login", "Fix login", Status.DONE,
        Priority.HIGH);

    TaskDto result = taskService.createTask(createTaskDto);

    assertNotNull(result.id());

    Task savedTask = taskRepository.findById(result.id()).orElseThrow();

    assertEquals("Login", savedTask.getTitle());
    assertEquals("Fix login", savedTask.getDescription());
    assertEquals(Status.DONE, savedTask.getStatus());
    assertEquals(Priority.HIGH, savedTask.getPriority());
  }

  @Test
  void createTask_whenStatusIsNull_savesToDoStatus() {

    CreateTaskDto createTaskDto = new CreateTaskDto("Login", "Fix login", null, Priority.HIGH);

    TaskDto result = taskService.createTask(createTaskDto);

    Task savedTask = taskRepository.findById(result.id()).orElseThrow();

    assertEquals(Status.TO_DO, savedTask.getStatus());
  }

  @Test
  void createTask_whenPriorityIsNull_savesMediumPriority() {

    CreateTaskDto createTaskDto = new CreateTaskDto("Login", "Fix login", Status.TO_DO, null);

    TaskDto result = taskService.createTask(createTaskDto);

    Task savedTask = taskRepository.findById(result.id()).orElseThrow();

    assertEquals(Priority.MED, savedTask.getPriority());
  }

  @Test
  void findTaskById_whenTaskExists_returnsTask() {

    CreateTaskDto createTaskDto = new CreateTaskDto("Login", "Fix login", Status.TO_DO,
        Priority.HIGH);

    TaskDto createdTask = taskService.createTask(createTaskDto);

    TaskDto result = taskService.findTaskById(createdTask.id());

    assertEquals(createdTask.id(), result.id());
    assertEquals("Login", result.title());
  }

  @Test
  void findTaskById_whenTaskDoesNotExist_throwsException() {

    TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
        () -> taskService.findTaskById(999L));

    assertEquals("Task with ID 999 not found", exception.getMessage());
  }

  @Test
  void findAllTasks_returnsAllTasks() {

    CreateTaskDto createTaskDto1 = new CreateTaskDto("Login", "Fix login", Status.TO_DO,
        Priority.MED);

    CreateTaskDto createTaskDto2 = new CreateTaskDto("Register", "Add register", Status.IN_PROGRESS,
        Priority.HIGH);

    taskService.createTask(createTaskDto1);
    taskService.createTask(createTaskDto2);

    int pageNumber = 0;
    int pageSize = 2;

    Page<TaskDto> result = taskService.findAllTasks(null, pageSize, pageNumber, Sort.unsorted());

    assertEquals(2, result.getContent().size());
  }

  @Test
  void findAllTasks_filtersByStatus() {

    CreateTaskDto createTaskDto1 = new CreateTaskDto("Login", "Fix login", Status.TO_DO,
        Priority.MED);

    CreateTaskDto createTaskDto2 = new CreateTaskDto("Register", "Add register", Status.IN_PROGRESS,
        Priority.HIGH);

    CreateTaskDto createTaskDto3 = new CreateTaskDto("Profile", "Update profile", Status.TO_DO,
        Priority.LOW);

    taskService.createTask(createTaskDto1);
    taskService.createTask(createTaskDto2);
    taskService.createTask(createTaskDto3);

    int pageNumber = 0;
    int pageSize = 2;

    Page<TaskDto> result = taskService.findAllTasks(Status.TO_DO, pageSize, pageNumber,
        Sort.unsorted());

    assertEquals(2, result.getContent().size());
  }

  @Test
  void findAllTasks_appliesPagination() {

    CreateTaskDto createTaskDto1 = new CreateTaskDto("Login", "Fix login", Status.TO_DO,
        Priority.MED);

    CreateTaskDto createTaskDto2 = new CreateTaskDto("Register", "Add register", Status.IN_PROGRESS,
        Priority.HIGH);

    CreateTaskDto createTaskDto3 = new CreateTaskDto("Profile", "Update profile", Status.TO_DO,
        Priority.LOW);

    taskService.createTask(createTaskDto1);
    taskService.createTask(createTaskDto2);
    taskService.createTask(createTaskDto3);

    int pageNumber = 0;
    int pageSize = 2;

    Page<TaskDto> result = taskService.findAllTasks(null, pageSize, pageNumber, Sort.unsorted());

    assertEquals(2, result.getContent().size());
  }

  @Test
  void findAllTasks_appliesSorting() {

    CreateTaskDto createTaskDto1 = new CreateTaskDto("Profile", "Update profile", Status.TO_DO,
        Priority.MED);

    CreateTaskDto createTaskDto2 = new CreateTaskDto("Login", "Fix login", Status.IN_PROGRESS,
        Priority.HIGH);

    CreateTaskDto createTaskDto3 = new CreateTaskDto("Register", "Add register", Status.TO_DO,
        Priority.LOW);

    taskService.createTask(createTaskDto1);
    taskService.createTask(createTaskDto2);
    taskService.createTask(createTaskDto3);

    int pageNumber = 0;
    int pageSize = 3;

    Page<TaskDto> result = taskService.findAllTasks(null, pageSize, pageNumber,
        Sort.by("title").ascending());

    assertEquals("Login", result.getContent().get(0).title());
    assertEquals("Profile", result.getContent().get(1).title());
    assertEquals("Register", result.getContent().get(2).title());
  }

  @Test
  void updateTask_updatesTaskInDatabase() {
    Task task = new Task("Login", "Fix login", Status.DONE, Priority.LOW);

    Task savedTask = taskRepository.save(task);

    UpdateTaskDto updatedTask = new UpdateTaskDto("Register", "Fix login", Status.DONE,
        Priority.LOW);

    taskService.updateTask(savedTask.getId(), updatedTask);

    Task updateTask = taskRepository.findById(savedTask.getId()).orElseThrow();

    assertEquals("Register", updateTask.getTitle());
    assertEquals("Fix login", updateTask.getDescription());
    assertEquals(Status.DONE, updateTask.getStatus());
    assertEquals(Priority.LOW, updateTask.getPriority());
  }

  @Test
  void updateTask_whenTaskDoesNotExist_throwsException() {

    UpdateTaskDto updateTaskDto = new UpdateTaskDto("Register", "Add register", Status.TO_DO,
        Priority.HIGH);

    TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
        () -> taskService.updateTask(999L, updateTaskDto));

    assertEquals("Task with ID 999 not found", exception.getMessage());
  }

  @Test
  void patchTask_updatesSpecifiedFields() {

    Task task = new Task("Login", "Fix login", Status.TO_DO, Priority.LOW);

    Task savedTask = taskRepository.save(task);

    JsonNode patch = objectMapper.readTree("""
        {
            "title": "Register",
            "status": "DONE"
        }
        """);

    taskService.patchTask(savedTask.getId(), patch);

    Task updatedTask = taskRepository.findById(savedTask.getId()).orElseThrow();

    assertEquals("Register", updatedTask.getTitle());
    assertEquals("Fix login", updatedTask.getDescription());
    assertEquals(Status.DONE, updatedTask.getStatus());
    assertEquals(Priority.LOW, updatedTask.getPriority());
  }

  @Test
  void patchTask_allowsNullValues() {

    Task task = new Task("Profile", "Update profile", Status.TO_DO, Priority.MED);

    Task savedTask = taskRepository.save(task);

    JsonNode patch = objectMapper.readTree("""
        {
            "description": null
        }
        """);

    taskService.patchTask(savedTask.getId(), patch);

    Task updatedTask = taskRepository.findById(savedTask.getId()).orElseThrow();

    assertNull(updatedTask.getDescription());
  }

  @Test
  void patchTask_rejectsInvalidField() {

    Task task = new Task("Login", "Fix login", Status.TO_DO, Priority.MED);

    Task savedTask = taskRepository.save(task);

    JsonNode patch = objectMapper.readTree("""
        {
            "something": "test"
        }
        """);

    assertThrows(InvalidPatchFieldException.class,
        () -> taskService.patchTask(savedTask.getId(), patch));
  }

  @Test
  void patchTask_whenTaskDoesNotExist_throwsException() {

    JsonNode patch = objectMapper.readTree("""
        {
            "title": "Login"
        }
        """);

    assertThrows(TaskNotFoundException.class, () -> taskService.patchTask(999L, patch));
  }

  @Test
  void deleteTask_deletesTaskFromDatabase() {

    CreateTaskDto createTaskDto = new CreateTaskDto("Login", "Fix login", Status.TO_DO,
        Priority.HIGH);

    TaskDto createdTask = taskService.createTask(createTaskDto);

    taskService.deleteTask(createdTask.id());

    assertFalse(taskRepository.existsById(createdTask.id()));
  }

  @Test
  void deleteTask_whenTaskDoesNotExist_throwsException() {

    TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
        () -> taskService.deleteTask(999L));

    assertEquals("Task with ID 999 not found", exception.getMessage());
  }
}
package com.kanban_api.unit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kanban_api.controller.TaskController;
import com.kanban_api.controller.mapper.TaskApiMapper;
import com.kanban_api.controller.request.CreateTaskRequest;
import com.kanban_api.controller.request.UpdateTaskRequest;
import com.kanban_api.controller.response.PaginatedResponse;
import com.kanban_api.controller.response.TaskResponse;
import com.kanban_api.domain.dto.CreateTaskDto;
import com.kanban_api.domain.dto.TaskDto;
import com.kanban_api.domain.dto.UpdateTaskDto;
import com.kanban_api.domain.enumeration.Priority;
import com.kanban_api.domain.enumeration.Status;
import com.kanban_api.domain.service.TaskService;
import com.kanban_api.exception.InvalidPatchFieldException;
import com.kanban_api.exception.TaskNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class TaskControllerUnitTest {

  @Mock
  private TaskService taskService;

  @Mock
  private TaskApiMapper taskApiMapper;

  @InjectMocks
  private TaskController taskController;

  @Test
  void shouldCreateTaskSuccessfully() {

    //ARRANGE
    CreateTaskRequest createTaskRequest = new CreateTaskRequest("Task 1", "Create task",
        Status.TO_DO, Priority.HIGH);
    CreateTaskDto createTaskDto = new CreateTaskDto("Task 1", "Create task", Status.TO_DO,
        Priority.HIGH);
    TaskDto taskDto = new TaskDto(1L, "Task 1", "Create task", Status.TO_DO, Priority.HIGH);
    TaskResponse taskResponse = new TaskResponse(1L, "Task 1", "Create task", Status.TO_DO,
        Priority.HIGH);

    when(taskApiMapper.toDto(createTaskRequest)).thenReturn(createTaskDto);
    when(taskService.createTask(createTaskDto)).thenReturn(taskDto);
    when(taskApiMapper.toResponse(taskDto)).thenReturn(taskResponse);

    //ACT
    TaskResponse result = taskController.createTask(createTaskRequest);

    //ASSERT
    assertEquals(taskResponse, result);

    //VERIFY
    verify(taskApiMapper).toDto(createTaskRequest);
    verify(taskService).createTask(createTaskDto);
    verify(taskApiMapper).toResponse(taskDto);
  }

  @Test
  void shouldFindTaskById_whenTaskExists() {

    //ARRANGE
    TaskDto taskDto = new TaskDto(1L, "Task 1", "Task task", Status.TO_DO, Priority.HIGH);
    TaskResponse taskResponse = new TaskResponse(1L, "Task 1", "Task task", Status.TO_DO,
        Priority.HIGH);

    when(taskService.findTaskById(1L)).thenReturn(taskDto);
    when(taskApiMapper.toResponse(taskDto)).thenReturn(taskResponse);

    //ACT
    TaskResponse result = taskController.findTaskById(1L);

    //ASSERT
    assertEquals(taskResponse, result);

    //VERIFY
    verify(taskService).findTaskById(1L);
    verify(taskApiMapper).toResponse(taskDto);

  }

  @Test
  void shouldThrowTaskNotFoundException_whenFindingNonExistingTask() {

    //ARRANGE
    when(taskService.findTaskById(99L)).thenThrow(
        new TaskNotFoundException("Task with ID 99 is not found"));

    //ACT + ASSERT
    assertThrows(TaskNotFoundException.class, () -> taskController.findTaskById(99L));

    //VERIFY
    verify(taskService).findTaskById(99L);
  }

  @Test
  void shouldFindAllTasks_whenStatusIsProvided() {

    //ARRANGE
    Status status = Status.TO_DO;
    int pageNumber = 0;
    int pageSize = 10;
    Sort sort = Sort.unsorted();

    TaskDto taskDto = new TaskDto(1L, "Task 1", "Task task", Status.TO_DO, Priority.HIGH);
    TaskResponse taskResponse = new TaskResponse(1L, "Task 1", "Task task", Status.TO_DO,
        Priority.HIGH);

    Page<TaskDto> taskDtoPage = new PageImpl<>(List.of(taskDto));
    Page<TaskResponse> expectedPage = new PageImpl<>(List.of(taskResponse));
    PaginatedResponse<TaskResponse> expected = new PaginatedResponse<>(expectedPage);

    when(taskService.findAllTasks(status, pageNumber, pageSize, sort)).thenReturn(taskDtoPage);
    when(taskApiMapper.toResponse(taskDto)).thenReturn(taskResponse);

    //ACT
    PaginatedResponse<TaskResponse> result = taskController.findAllTasks(status, pageNumber,
        pageSize, sort);

    //ASSERT
    assertEquals(expected, result);

    //VERIFY
    verify(taskService).findAllTasks(status, pageNumber, pageSize, sort);
    verify(taskApiMapper).toResponse(taskDto);
  }

  @Test
  void shouldFindAllTasks_whenStatusIsNull() {

    //ARRANGE
    Status status = null;
    int pageNumber = 0;
    int pageSize = 10;
    Sort sort = Sort.unsorted();

    TaskDto taskDto = new TaskDto(1L, "Task 1", "Task task", null, Priority.HIGH);
    TaskResponse taskResponse = new TaskResponse(1L, "Task 1", "Task task", null, Priority.HIGH);

    Page<TaskDto> taskDtoPage = new PageImpl<>(List.of(taskDto));
    Page<TaskResponse> expectedPage = new PageImpl<>(List.of(taskResponse));
    PaginatedResponse<TaskResponse> expected = new PaginatedResponse<>(expectedPage);

    when(taskService.findAllTasks(status, pageNumber, pageSize, sort)).thenReturn(taskDtoPage);
    when(taskApiMapper.toResponse(taskDto)).thenReturn(taskResponse);

    PaginatedResponse<TaskResponse> result = taskController.findAllTasks(status, pageNumber,
        pageSize, sort);

    assertEquals(expected, result);

    verify(taskService).findAllTasks(status, pageNumber, pageSize, sort);
    verify(taskApiMapper).toResponse(taskDto);
  }

  @Test
  void shouldUpdateTask_whenTaskExists() {

    UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest("Task 1", "Task task", Status.TO_DO,
        Priority.HIGH);
    UpdateTaskDto updateTaskDto = new UpdateTaskDto("Task 1", "Task task", Status.TO_DO,
        Priority.HIGH);

    TaskDto taskDto = new TaskDto(1L, "Task 1", "Task task", Status.TO_DO, Priority.HIGH);
    TaskResponse taskResponse = new TaskResponse(1L, "Task 1", "Task task", Status.TO_DO,
        Priority.HIGH);

    when(taskApiMapper.toDto(updateTaskRequest)).thenReturn(updateTaskDto);
    when(taskService.updateTask(1L, updateTaskDto)).thenReturn(taskDto);
    when(taskApiMapper.toResponse(taskDto)).thenReturn(taskResponse);

    TaskResponse result = taskController.updateTask(1L, updateTaskRequest);

    assertEquals(taskResponse, result);

    verify(taskApiMapper).toDto(updateTaskRequest);
    verify(taskService).updateTask(1L, updateTaskDto);
    verify(taskApiMapper).toResponse(taskDto);
  }

  @Test
  void shouldThrowTaskNotFoundException_whenUpdatingNonExistingTask() {

    UpdateTaskDto updateTaskDto = new UpdateTaskDto("Task 1", "Task task", Status.TO_DO,
        Priority.HIGH);
    UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest("Task 1", "Task task", Status.TO_DO,
        Priority.HIGH);

    when(taskApiMapper.toDto(updateTaskRequest)).thenReturn(updateTaskDto);
    when(taskService.updateTask(99L, updateTaskDto)).thenThrow(
        new TaskNotFoundException("Task with ID 99 is not found"));

    assertThrows(TaskNotFoundException.class,
        () -> taskController.updateTask(99L, updateTaskRequest));

    verify(taskService).updateTask(99L, updateTaskDto);
    verify(taskApiMapper).toDto(updateTaskRequest);
  }

  @Test
  void shouldPatchTask_whenTaskExists() {

    JsonNode patch = new ObjectMapper().readTree("""
        {
        "title": "Task 1"
        }
        """);

    TaskDto taskDto = new TaskDto(1L, "Task 1", "Task task", Status.TO_DO, Priority.HIGH);
    TaskResponse taskResponse = new TaskResponse(1L, "Task 1", "Task task", Status.TO_DO,
        Priority.HIGH);

    when(taskService.patchTask(1L, patch)).thenReturn(taskDto);
    when(taskApiMapper.toResponse(taskDto)).thenReturn(taskResponse);

    TaskResponse result = taskController.patchTask(1L, patch);

    assertEquals(taskResponse, result);

    verify(taskService).patchTask(1L, patch);
    verify(taskApiMapper).toResponse(taskDto);
  }

  @Test
  void shouldThrowTaskNotFoundException_whenPatchingNonExistingTask() {

    JsonNode patch = new ObjectMapper().readTree("""
        {
        "title": "Task 1"
        }
        """);

    when(taskService.patchTask(99L, patch)).thenThrow(
        new TaskNotFoundException("Task with ID 99 is not found"));

    TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
        () -> taskController.patchTask(99L, patch));

    assertEquals("Task with ID 99 is not found", exception.getMessage());

    verify(taskService).patchTask(99L, patch);
    verify(taskApiMapper, never()).toResponse(any());
  }

  @Test
  void shouldThrowInvalidPatchFieldException_whenFieldIsNotAllowed() {
    JsonNode patch = new ObjectMapper().readTree("""
        {
        "id": 100
        }
        """);

    when(taskService.patchTask(99L, patch)).thenThrow(
        new InvalidPatchFieldException("Field 'id' cannot be patched"));

    InvalidPatchFieldException exception = assertThrows(InvalidPatchFieldException.class,
        () -> taskController.patchTask(99L, patch));

    assertEquals("Field 'id' cannot be patched", exception.getMessage());

    verify(taskService).patchTask(99L, patch);
    verify(taskApiMapper, never()).toResponse(any());

  }

  @Test
  void shouldDeleteTask_whenTaskExists() {

    doNothing().when(taskService).deleteTask(99L);

    assertDoesNotThrow(() -> taskController.deleteTask(99L));

    verify(taskService).deleteTask(99L);
  }

  @Test
  void shouldThrowTaskNotFoundException_whenDeleteNonExistingTask() {

    doThrow(new TaskNotFoundException("Task with ID 99 is not found")).when(taskService)
        .deleteTask(99L);

    TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
        () -> taskController.deleteTask(99L));

    assertEquals("Task with ID 99 is not found", exception.getMessage());

    verify(taskService).deleteTask(99L);

  }
}

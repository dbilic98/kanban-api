package com.kanban_api.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kanban_api.domain.dto.CreateTaskDto;
import com.kanban_api.domain.dto.TaskDto;
import com.kanban_api.domain.dto.UpdateTaskDto;
import com.kanban_api.domain.enumeration.Priority;
import com.kanban_api.domain.enumeration.Status;
import com.kanban_api.domain.model.Task;
import com.kanban_api.domain.repository.TaskRepository;
import com.kanban_api.domain.service.mapper.TaskMapper;
import com.kanban_api.exception.TaskNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

  @Mock
  private TaskRepository taskRepository;

  @Mock
  private TaskMapper taskMapper;

  @InjectMocks
  private TaskService taskService;

  @Test
  void shouldCreateTaskSuccessfully() {

    CreateTaskDto createTaskDto = new CreateTaskDto("Task 1", "Create task", Status.TO_DO,
        Priority.HIGH);

    Task task = new Task("Task 1", "Create task", Status.TO_DO, Priority.HIGH);

    Task savedTask = new Task("Task 1", "Create task", Status.TO_DO, Priority.HIGH);
    savedTask.setId(1L);

    TaskDto expected = new TaskDto(1L, "Task 1", "Create task", Status.TO_DO, Priority.HIGH);

    when(taskMapper.toEntity(createTaskDto)).thenReturn(task);

    when(taskRepository.save(task)).thenReturn(savedTask);

    when(taskMapper.toDto(savedTask)).thenReturn(expected);

    TaskDto result = taskService.createTask(createTaskDto);

    assertEquals(expected, result);

    verify(taskMapper).toEntity(createTaskDto);
    verify(taskRepository).save(task);
    verify(taskMapper).toDto(savedTask);
  }

  @Test
  void shouldFindTaskById_whenTaskExists() {

    Task task = new Task("Task 1", "Find task", Status.TO_DO, Priority.HIGH);
    task.setId(1L);

    TaskDto expected = new TaskDto(1L, "Task 1", "Find task", Status.TO_DO, Priority.HIGH);

    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

    when(taskMapper.toDto(task)).thenReturn(expected);

    TaskDto foundTask = taskService.findTaskById(1L);

    assertEquals(expected, foundTask);

    verify(taskRepository).findById(1L);
    verify(taskMapper).toDto(task);
  }

  @Test
  void shouldThrowTaskNotFoundException_whenTaskDoesNotExist() {

    when(taskRepository.findById(99L)).thenReturn(Optional.empty());

    TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
        () -> taskService.findTaskById(99L));

    assertEquals("Task with ID 99 not found", exception.getMessage());

    verify(taskRepository).findById(99L);
  }

  @Test
  void shouldFindAllTasksByStatus() {

    int pageNumber = 0;
    int pageSize = 10;

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id"));

    Task task = new Task("Task 1", "Description", Status.TO_DO, Priority.HIGH);
    TaskDto taskDto = new TaskDto(1L, "Task 1", "Description", Status.TO_DO, Priority.HIGH);

    Page<Task> taskPage = new PageImpl<>(List.of(task));

    when(taskRepository.findByStatus(Status.TO_DO, pageable)).thenReturn(taskPage);

    when(taskMapper.toDto(task)).thenReturn(taskDto);

    Page<TaskDto> result = taskService.findAllTasks(Status.TO_DO, pageSize, pageNumber,
        Sort.by("id"));

    assertEquals(1, result.getTotalElements());
    assertEquals(taskDto, result.getContent().getFirst());

    verify(taskRepository).findByStatus(Status.TO_DO, pageable);
    verify(taskMapper).toDto(task);
  }

  @Test
  void shouldFindAllTasksWhenStatusIsNull() {

    int pageNumber = 0;
    int pageSize = 10;

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.unsorted());

    Task task = new Task("Task 1", "Description", Status.TO_DO, Priority.HIGH);
    TaskDto taskDto = new TaskDto(1L, "Task 1", "Description", Status.TO_DO, Priority.HIGH);

    Page<Task> taskPage = new PageImpl<>(List.of(task));

    when(taskRepository.findAll(pageable)).thenReturn(taskPage);

    when(taskMapper.toDto(task)).thenReturn(taskDto);

    Page<TaskDto> result = taskService.findAllTasks(null, pageSize, pageNumber, Sort.unsorted());

    assertEquals(1, result.getTotalElements());
    assertEquals(taskDto, result.getContent().getFirst());

    verify(taskRepository).findAll(pageable);
    verify(taskMapper).toDto(task);
  }

  @Test
  void shouldUpdateTask_whenTaskExists() {

    Task existingTask = new Task("Old title", "Old description", Status.TO_DO, Priority.LOW);
    existingTask.setId(1L);

    UpdateTaskDto updateTaskDto = new UpdateTaskDto("Task 1", "Description", Status.TO_DO,
        Priority.HIGH);

    TaskDto taskDto = new TaskDto(1L, "Task 1", "Description", Status.TO_DO, Priority.HIGH);

    when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

    when(taskMapper.toDto(existingTask)).thenReturn(taskDto);

    TaskDto result = taskService.updateTask(1L, updateTaskDto);

    assertEquals("Task 1", existingTask.getTitle());
    assertEquals("Description", existingTask.getDescription());
    assertEquals(Status.TO_DO, existingTask.getStatus());
    assertEquals(Priority.HIGH, existingTask.getPriority());

    assertEquals(taskDto, result);

    verify(taskRepository).findById(1L);
    verify(taskMapper).toDto(existingTask);
  }

  @Test
  void shouldThrowTaskNotFoundException_whenUpdatingNonExistingTask() {

    when(taskRepository.findById(99L)).thenReturn(Optional.empty());

    TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
        () -> taskService.updateTask(99L,
            new UpdateTaskDto("Task 1", "Description", Status.TO_DO, Priority.HIGH)));

    assertEquals("Task with ID 99 not found", exception.getMessage());

    verify(taskRepository).findById(99L);
  }

  @Test
  void shouldDeleteTask_whenTaskExists() {

    when(taskRepository.existsById(1L)).thenReturn(true);

    taskService.deleteTask(1L);

    verify(taskRepository).deleteById(1L);
  }

  @Test
  void shouldThrowTaskNotFoundException_whenDeleteNonExistingTask() {

    when(taskRepository.existsById(99L)).thenReturn(false);

    TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () ->
        taskService.deleteTask(99L));

    assertEquals("Task with ID 99 not found", exception.getMessage());

    verify(taskRepository, never()).deleteById(99L);
  }
}

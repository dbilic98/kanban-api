package com.kanban_api.controller;

import com.kanban_api.controller.mapper.TaskApiMapper;
import com.kanban_api.controller.request.CreateTaskRequest;
import com.kanban_api.controller.request.UpdateTaskRequest;
import com.kanban_api.controller.response.PaginatedResponse;
import com.kanban_api.controller.response.TaskResponse;
import com.kanban_api.domain.dto.CreateTaskDto;
import com.kanban_api.domain.dto.TaskDto;
import com.kanban_api.domain.dto.UpdateTaskDto;
import com.kanban_api.domain.enumeration.Status;
import com.kanban_api.domain.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService taskService;
  private final TaskApiMapper taskApiMapper;

  public TaskController(TaskService taskService, TaskApiMapper taskApiMapper) {
    this.taskService = taskService;
    this.taskApiMapper = taskApiMapper;
  }

  @GetMapping
  public PaginatedResponse<TaskResponse> findAllTasks(
      @RequestParam(required = false) Status status,
      @RequestParam(defaultValue = "0") int pageNumber,
      @RequestParam(defaultValue = "10") int pageSize, Sort sort) {

    Page<TaskDto> tasks = taskService.findAllTasks(status, pageSize, pageNumber, sort);
    Page<TaskResponse> taskResponse = tasks.map(taskApiMapper::toResponse);
    return new PaginatedResponse<>(taskResponse);
  }

  @GetMapping("/{taskId}")
  public TaskResponse findTaskById(@PathVariable("taskId") Long id) {
    TaskDto taskDto = taskService.findTaskById(id);
    return taskApiMapper.toResponse(taskDto);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TaskResponse createTask(@Valid @RequestBody CreateTaskRequest createTaskRequest) {
    CreateTaskDto createTaskDto = taskApiMapper.toDto(createTaskRequest);
    TaskDto createdTask = taskService.createTask(createTaskDto);
    return taskApiMapper.toResponse(createdTask);
  }

  @PutMapping("/{taskId}")
  public TaskResponse updateTask(@PathVariable("taskId") Long id,
      @Valid @RequestBody UpdateTaskRequest updateTaskRequest) {
    UpdateTaskDto updateTaskDto = taskApiMapper.toDto(updateTaskRequest);
    TaskDto updatedTask = taskService.updateTask(id, updateTaskDto);
    return taskApiMapper.toResponse(updatedTask);
  }

  @PatchMapping(
      value =  "/{taskId}",
      consumes = "application/merge-patch+json"
  )
  public TaskResponse patchTask(@PathVariable("taskId") Long id,
      @RequestBody JsonNode patch) {
    TaskDto patchedTask = taskService.patchTask(id, patch);
    return taskApiMapper.toResponse(patchedTask);
  }

  @DeleteMapping("/{taskId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTask(@PathVariable("taskId") Long id) {
    taskService.deleteTask(id);
  }
}


package com.kanban_api.domain.service;

import com.kanban_api.domain.dto.CreateTaskDto;
import com.kanban_api.domain.dto.TaskDto;
import com.kanban_api.domain.dto.UpdateTaskDto;
import com.kanban_api.domain.enumeration.Status;
import com.kanban_api.domain.model.Task;
import com.kanban_api.domain.repository.TaskRepository;
import com.kanban_api.domain.service.mapper.TaskMapper;
import com.kanban_api.exception.TaskNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final TaskMapper taskMapper;
  private final ObjectMapper objectMapper;

  public TaskService(TaskRepository taskRepository, TaskMapper taskMapper,
      ObjectMapper objectMapper) {
    this.taskRepository = taskRepository;
    this.taskMapper = taskMapper;
    this.objectMapper = objectMapper;
  }

  public TaskDto createTask(CreateTaskDto createTaskDto) {
    Task createdTask = taskMapper.toEntity(createTaskDto);
    Task saved = taskRepository.save(createdTask);
    return taskMapper.toDto(saved);
  }

  public TaskDto findTaskById(Long id) {
    Task task = taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
    return taskMapper.toDto(task);
  }

  public Page<TaskDto> findAllTasks(Status status, int pageSize, int pageNumber,
      Sort sort) {
    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

    if (status != null) {
      return taskRepository.findByStatus(status, pageable)
          .map(taskMapper::toDto);
    }

    return taskRepository.findAll(pageable)
        .map(taskMapper::toDto);
  }

  @Transactional
  public TaskDto updateTask(Long id, UpdateTaskDto updateTaskDto) {
    Task task = taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));

    task.setTitle(updateTaskDto.title());
    task.setDescription(updateTaskDto.description());
    task.setStatus(updateTaskDto.status());
    task.setPriority(updateTaskDto.priority());

    return taskMapper.toDto(task);
  }

  //JSON Merge Patch?
  public TaskDto patchTask(Long id, JsonNode patch) {
    Task task = taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));

    objectMapper.readerForUpdating(task).readValue(patch);
    return taskMapper.toDto(task);
  }


  public void deleteTask(Long id) {
    if (taskRepository.existsById(id)) {
      taskRepository.deleteById(id);
      return;
    }
    throw new TaskNotFoundException("Task with ID " + id + " not found");
  }
}

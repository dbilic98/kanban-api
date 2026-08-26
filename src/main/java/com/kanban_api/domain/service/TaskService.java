package com.kanban_api.domain.service;

import com.kanban_api.domain.dto.CreateTaskDto;
import com.kanban_api.domain.dto.TaskDto;
import com.kanban_api.domain.dto.UpdateTaskDto;
import com.kanban_api.domain.enumeration.Status;
import com.kanban_api.domain.model.Task;
import com.kanban_api.domain.repository.TaskRepository;
import com.kanban_api.domain.service.mapper.TaskServiceMapper;
import com.kanban_api.exception.TaskNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

public class TaskService {

  private final TaskRepository taskRepository;
  private final TaskServiceMapper taskServiceMapper;

  public TaskService(TaskRepository taskRepository, TaskServiceMapper taskServiceMapper) {
    this.taskRepository = taskRepository;
    this.taskServiceMapper = taskServiceMapper;
  }

  public TaskDto createTask(CreateTaskDto createTaskDto) {
    Task createdTask = taskServiceMapper.toEntity(createTaskDto);
    Task saved = taskRepository.save(createdTask);
    return taskServiceMapper.toDto(saved);
  }

  public TaskDto findTaskById(Long id) {
    Task task = taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
    return taskServiceMapper.toDto(task);
  }

  public Page<TaskDto> findAllTasks(Status status, int pageSize, int pageNumber,
      Sort sort) {
    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

    if (status != null) {
      return taskRepository.findByStatus(status, pageable)
          .map(taskServiceMapper::toDto);
    }

    return taskRepository.findAll(pageable)
        .map(taskServiceMapper::toDto);
  }

  @Transactional
  public TaskDto updateTask(Long id, UpdateTaskDto updateTaskDto) {
    Task task = taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));

    taskServiceMapper.updateEntity(task, updateTaskDto);
    return taskServiceMapper.toDto(task);
  }

  //JSON Merge Patch?


  public void deleteTask(Long id) {
    if (taskRepository.existsById(id)) {
      taskRepository.deleteById(id);
      return;
    }
    throw new TaskNotFoundException("Task with ID " + id + " not found");
  }
}

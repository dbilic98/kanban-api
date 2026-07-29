package com.kanban_api.domain.service;

import com.kanban_api.controller.request.CreateTaskDto;
import com.kanban_api.controller.request.UpdateTaskDto;
import com.kanban_api.domain.enumeration.Status;
import com.kanban_api.domain.model.Task;
import com.kanban_api.domain.repository.TaskRepository;
import com.kanban_api.exception.TaskNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

public class TaskService {

  private TaskRepository taskRepository;

  public Task createTask(CreateTaskDto createTaskDto) {
    Task createdTask = new Task(createTaskDto.title(), createTaskDto.description(),
        createTaskDto.status(), createTaskDto.priority());
    return taskRepository.save(createdTask);
  }

  public Task findTaskById(Long id) {
    return taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
  }

  public Page<Task> findAllTasks(Status status, int pageSize, int pageNumber,
      Sort sort) {
    Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

    if (status != null) {
      return taskRepository.findByStatus(status, pageable);
    }

    return taskRepository.findAll(pageable);
  }

  @Transactional
  public Task updateTask(Long id, UpdateTaskDto updateTaskDto) {
    Task taskToUpdate = taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));

    taskToUpdate.setTitle(updateTaskDto.title());
    taskToUpdate.setDescription(updateTaskDto.description());
    taskToUpdate.setStatus(updateTaskDto.status());
    taskToUpdate.setPriority(updateTaskDto.priority());
    return taskRepository.save(taskToUpdate);
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

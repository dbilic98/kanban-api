package com.kanban_api.domain.service.mapper;

import com.kanban_api.domain.dto.CreateTaskDto;
import com.kanban_api.domain.dto.TaskDto;
import com.kanban_api.domain.dto.UpdateTaskDto;
import com.kanban_api.domain.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskServiceMapper {

  public Task toEntity(CreateTaskDto createTaskDto) {
    return new Task(
        createTaskDto.title(),
        createTaskDto.description(),
        createTaskDto.status(),
        createTaskDto.priority()
    );
  }

  public void updateEntity(Task task, UpdateTaskDto updateTaskDto) {
    task.setTitle(updateTaskDto.title());
    task.setDescription(updateTaskDto.description());
    task.setStatus(updateTaskDto.status());
    task.setPriority(updateTaskDto.priority());
  }

  public TaskDto toDto(Task task) {
    return new TaskDto(
        task.getId(),
        task.getTitle(),
        task.getDescription(),
        task.getStatus(),
        task.getPriority()
    );
  }
}

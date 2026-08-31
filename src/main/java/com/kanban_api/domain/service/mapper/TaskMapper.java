package com.kanban_api.domain.service.mapper;

import com.kanban_api.domain.dto.CreateTaskDto;
import com.kanban_api.domain.dto.TaskDto;
import com.kanban_api.domain.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

  public Task toEntity(CreateTaskDto createTaskDto) {
    return new Task(
        createTaskDto.title(),
        createTaskDto.description(),
        createTaskDto.status(),
        createTaskDto.priority()
    );
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

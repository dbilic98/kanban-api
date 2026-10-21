package com.kanban_api.controller.mapper;

import com.kanban_api.controller.request.CreateTaskRequest;
import com.kanban_api.controller.request.UpdateTaskRequest;
import com.kanban_api.controller.response.TaskResponse;
import com.kanban_api.domain.dto.CreateTaskDto;
import com.kanban_api.domain.dto.TaskDto;
import com.kanban_api.domain.dto.UpdateTaskDto;
import org.springframework.stereotype.Component;

@Component
public class TaskApiMapper {

  public CreateTaskDto toDto(CreateTaskRequest createTaskRequest) {
    return new CreateTaskDto(
        createTaskRequest.title(),
        createTaskRequest.description(),
        createTaskRequest.status(),
        createTaskRequest.priority()
    );
  }

  public UpdateTaskDto toDto(UpdateTaskRequest updateTaskRequest) {
    return new UpdateTaskDto(
        updateTaskRequest.title(),
        updateTaskRequest.description(),
        updateTaskRequest.status(),
        updateTaskRequest.priority()
    );
  }

  public TaskResponse toResponse(TaskDto dto) {
    return new TaskResponse(
        dto.id(),
        dto.title(),
        dto.description(),
        dto.status(),
        dto.priority()
    );
  }
}

package com.kanban_api.domain.repository;

import com.kanban_api.domain.enumeration.Priority;
import com.kanban_api.domain.enumeration.Status;
import com.kanban_api.domain.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

  Page<Task> findByStatus(Status status, Pageable pageable);
}

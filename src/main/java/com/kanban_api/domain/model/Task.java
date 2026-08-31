package com.kanban_api.domain.model;

import com.kanban_api.domain.enumeration.Priority;
import com.kanban_api.domain.enumeration.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  private String description;

  @Enumerated(EnumType.STRING)
  private Status status;

  @Enumerated(EnumType.STRING)
  private Priority priority;

  @Version
  private Long version;

  public Task(String title, String description, Status status, Priority priority) {
    this.title = title;
    this.description = description;
    this.status = (status != null) ? status : Status.TO_DO;
    this.priority = (priority != null) ? priority : Priority.MED;
  }
}


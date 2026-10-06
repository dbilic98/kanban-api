package com.kanban_api.integration;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractIntegrationTest {

  @Container
  static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0");
}

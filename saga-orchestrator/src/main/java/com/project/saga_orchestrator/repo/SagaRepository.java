package com.project.saga_orchestrator.repo;

import com.project.saga_orchestrator.model.SagaState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaRepository extends JpaRepository<SagaState, String> {
}
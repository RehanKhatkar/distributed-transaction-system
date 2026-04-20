package com.project.saga_orchestrator.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class SagaState {
    @Id
    private String orderId;
    private String status;
    public SagaState() {}
    public SagaState(String orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }

}
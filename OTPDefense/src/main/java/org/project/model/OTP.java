package org.project.model;

import java.time.LocalDateTime;

public class OTP {
    private int id;
    private String operationId;
    private String code;
    private OTPStatus status;
    private LocalDateTime creationTime;
    private LocalDateTime expirationTime;

    // Геттеры и сеттеры

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getOperationId() {
        return operationId;
    }
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public OTPStatus getStatus() {
        return status;
    }
    public void setStatus(OTPStatus status) {
        this.status = status;
    }
    public LocalDateTime getCreationTime() {
        return creationTime;
    }
    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }
    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }
    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }
}


package org.project.model;

public class OTPConfig {
    private int id;
    private int codeLength;
    private int validDurationSeconds; // Время жизни кода в секундах

    // Геттеры и сеттеры

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getCodeLength() {
        return codeLength;
    }
    public void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }
    public int getValidDurationSeconds() {
        return validDurationSeconds;
    }
    public void setValidDurationSeconds(int validDurationSeconds) {
        this.validDurationSeconds = validDurationSeconds;
    }
}


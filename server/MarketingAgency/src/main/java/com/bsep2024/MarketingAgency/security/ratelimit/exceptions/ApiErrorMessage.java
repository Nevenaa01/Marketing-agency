package com.bsep2024.MarketingAgency.security.ratelimit.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiErrorMessage {

    private final UUID id = UUID.randomUUID();
    private int status;
    private String error;
    private String message;
    private final LocalDateTime timestamp = LocalDateTime.now(Clock.systemUTC());
    private String path;

    public UUID getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    public ApiErrorMessage(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}

package com.takehometask.readingassignment.assignment.dto;

import com.takehometask.readingassignment.assignment.AssignmentStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateProgressRequest(
        @NotNull AssignmentStatus status,
        @Min(0) int minutesRead
) {
}
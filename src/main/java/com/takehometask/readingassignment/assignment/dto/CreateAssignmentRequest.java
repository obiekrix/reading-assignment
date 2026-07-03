package com.takehometask.readingassignment.assignment.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CreateAssignmentRequest(
        @NotNull Long bookId,
        @NotEmpty List<String> studentIds,
        @NotNull @FutureOrPresent LocalDate dueDate
) {
}
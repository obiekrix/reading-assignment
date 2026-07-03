package com.takehometask.readingassignment.assignment;

import com.takehometask.readingassignment.assignment.dto.CreateAssignmentRequest;
import com.takehometask.readingassignment.assignment.dto.UpdateProgressRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    public List<ReadingAssignment> getTeacherAssignments(@RequestHeader("X-User-Id") Long teacherId) {
        return assignmentService.getTeacherAssignments(teacherId);
    }

    @PostMapping
    public List<ReadingAssignment> createAssignments(
            @RequestHeader("X-User-Id") Long teacherId,
            @Valid @RequestBody CreateAssignmentRequest request
    ) {
        return assignmentService.createAssignments(teacherId, request);
    }

    @GetMapping("/student")
    public List<ReadingAssignment> getStudentAssignments(@RequestHeader("X-User-Id") Long studentId) {
        return assignmentService.getStudentAssignments(studentId);
    }

    @PatchMapping("/{assignmentId}/progress")
    public ReadingAssignment updateProgress(
            @RequestHeader("X-User-Id") Long studentId,
            @PathVariable Long assignmentId,
            @Valid @RequestBody UpdateProgressRequest request
    ) {
        return assignmentService.updateProgress(studentId, assignmentId, request);
    }
}
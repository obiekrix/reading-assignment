package com.takehometask.readingassignment.assignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReadingAssignmentRepository extends JpaRepository<ReadingAssignment, Long> {
    List<ReadingAssignment> findByTeacherId(Long teacherId);

    List<ReadingAssignment> findByStudentId(Long studentId);
}
package com.takehometask.readingassignment.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReadingAssignmentRepository extends JpaRepository<ReadingAssignment, Long> {

    @Query("SELECT r FROM ReadingAssignment r INNER JOIN AppUser a ON r.student = a AND a.userId = :studentUserId")
    List<ReadingAssignment> findByStudentUserId(String studentUserId);

    @Query("SELECT r FROM ReadingAssignment r INNER JOIN AppUser a ON r.teacher = a AND a.userId = :teacherUserId")
    List<ReadingAssignment> findByTeacherUserId(String teacherUserId);
}
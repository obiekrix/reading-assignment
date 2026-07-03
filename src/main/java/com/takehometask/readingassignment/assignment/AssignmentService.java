package com.takehometask.readingassignment.assignment;

import com.takehometask.readingassignment.assignment.dto.CreateAssignmentRequest;
import com.takehometask.readingassignment.assignment.dto.UpdateProgressRequest;
import com.takehometask.readingassignment.book.Book;
import com.takehometask.readingassignment.book.BookRepository;
import com.takehometask.readingassignment.user.AppUser;
import com.takehometask.readingassignment.user.UserRepository;
import com.takehometask.readingassignment.user.UserRole;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {

    private final ReadingAssignmentRepository assignmentRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public AssignmentService(
            ReadingAssignmentRepository assignmentRepository,
            BookRepository bookRepository,
            UserRepository userRepository
    ) {
        this.assignmentRepository = assignmentRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<ReadingAssignment> createAssignments(Long teacherId, CreateAssignmentRequest request) {
        AppUser teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        if (teacher.getRole() != UserRole.TEACHER) {
            throw new IllegalArgumentException("Only teachers can create assignments");
        }

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        List<ReadingAssignment> assignments = request.studentIds().stream()
                .map(studentId -> {
                    AppUser student = userRepository.findById(studentId)
                            .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

                    if (student.getRole() != UserRole.STUDENT) {
                        throw new IllegalArgumentException("Assignments can only be created for students");
                    }

                    return new ReadingAssignment(book, student, teacher, request.dueDate());
                })
                .toList();

        return assignmentRepository.saveAll(assignments);
    }

    public List<ReadingAssignment> getTeacherAssignments(Long teacherId) {
        return assignmentRepository.findByTeacherId(teacherId);
    }

    public List<ReadingAssignment> getStudentAssignments(Long studentId) {
        return assignmentRepository.findByStudentId(studentId);
    }

    public ReadingAssignment updateProgress(Long studentId, Long assignmentId, UpdateProgressRequest request) {
        ReadingAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (!assignment.getStudent().getId().equals(studentId)) {
            throw new IllegalArgumentException("Students can only update their own assignments");
        }

        assignment.updateProgress(request.status(), request.minutesRead());
        return assignmentRepository.save(assignment);
    }
}
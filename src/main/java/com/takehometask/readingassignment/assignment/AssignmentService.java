package com.takehometask.readingassignment.assignment;

import com.takehometask.readingassignment.assignment.dto.CreateAssignmentRequest;
import com.takehometask.readingassignment.assignment.dto.UpdateProgressRequest;
import com.takehometask.readingassignment.book.Book;
import com.takehometask.readingassignment.book.BookRepository;
import com.takehometask.readingassignment.user.AppUser;
import com.takehometask.readingassignment.user.UserRepository;
import com.takehometask.readingassignment.user.UserRole;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

    public List<ReadingAssignment> createAssignments(String teacherId, CreateAssignmentRequest request) {
        AppUser teacher = userRepository.findByUserId(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        if (teacher.getRole() != UserRole.TEACHER) {
            throw new IllegalArgumentException("Only teachers can create assignments");
        }

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        List<ReadingAssignment> assignments = request.studentIds().stream()
                .map(studentId -> {
                    AppUser student = userRepository.findByUserId(studentId)
                            .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

                    if (student.getRole() != UserRole.STUDENT) {
                        throw new IllegalArgumentException("Assignments can only be created for students");
                    }

                    LocalDateTime localDateTime = LocalDateTime.of(request.dueDate(), LocalTime.MAX);
                    OffsetDateTime dueDate = localDateTime.atOffset(ZoneOffset.UTC);

                    return new ReadingAssignment(book, student, teacher, dueDate);
                })
                .toList();

        return assignmentRepository.saveAll(assignments);
    }

    public List<ReadingAssignment> getTeacherAssignments(String teacherId) {
        return assignmentRepository.findByTeacherUserId(teacherId);
    }

    public List<ReadingAssignment> getStudentAssignments(String studentId) {
        return assignmentRepository.findByStudentUserId(studentId);
    }

    public ReadingAssignment updateProgress(String studentId, Long assignmentId, UpdateProgressRequest request) {
        ReadingAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (!assignment.getStudent().getUserId().equals(studentId)) {
            throw new IllegalArgumentException("Students can only update their own assignments");
        }

        if (OffsetDateTime.now().isAfter(assignment.getDueDate())) {
            throw new IllegalArgumentException("Assignments can only be updated before the due date");
        }

        assignment.updateProgress(request.status());
        return assignmentRepository.save(assignment);
    }
}
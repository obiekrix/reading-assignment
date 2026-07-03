package com.takehometask.readingassignment.assignment;

import com.takehometask.readingassignment.assignment.dto.CreateAssignmentRequest;
import com.takehometask.readingassignment.assignment.dto.UpdateProgressRequest;
import com.takehometask.readingassignment.book.Book;
import com.takehometask.readingassignment.book.BookRepository;
import com.takehometask.readingassignment.user.AppUser;
import com.takehometask.readingassignment.user.UserRepository;
import com.takehometask.readingassignment.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    @Mock
    private ReadingAssignmentRepository assignmentRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    private AssignmentService assignmentService;

    @BeforeEach
    void setUp() {
        assignmentService = new AssignmentService(
                assignmentRepository,
                bookRepository,
                userRepository
        );
    }

    @Test
    void createAssignmentsCreatesAssignmentForEachStudent() {
        AppUser teacher = new AppUser(
                "Teacher",
                "teacher@example.com",
                "teacher-1",
                UserRole.TEACHER
        );
        AppUser studentOne = new AppUser(
                "Student One",
                "student.one@example.com",
                "student-1",
                UserRole.STUDENT
        );
        AppUser studentTwo = new AppUser(
                "Student Two",
                "student.two@example.com",
                "student-2",
                UserRole.STUDENT
        );
        Book book = new Book(
                "Test Book",
                "Test Author",
                "Test Description",
                "https://example.com/book"
        );

        LocalDate dueDate = LocalDate.now().plusDays(7);
        CreateAssignmentRequest request = new CreateAssignmentRequest(
                1L,
                List.of("student-1", "student-2"),
                dueDate
        );

        when(userRepository.findByUserId("teacher-1")).thenReturn(Optional.of(teacher));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findByUserId("student-1")).thenReturn(Optional.of(studentOne));
        when(userRepository.findByUserId("student-2")).thenReturn(Optional.of(studentTwo));
        when(assignmentRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<ReadingAssignment> result = assignmentService.createAssignments("teacher-1", request);

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(ReadingAssignment::getBook)
                .containsExactly(book, book);
        assertThat(result)
                .extracting(ReadingAssignment::getTeacher)
                .containsExactly(teacher, teacher);
        assertThat(result)
                .extracting(ReadingAssignment::getStudent)
                .containsExactly(studentOne, studentTwo);
        assertThat(result)
                .extracting(ReadingAssignment::getStatus)
                .containsExactly(AssignmentStatus.NOT_STARTED, AssignmentStatus.NOT_STARTED);

        OffsetDateTime expectedDueDate = dueDate
                .atTime(LocalTime.MAX)
                .atOffset(ZoneOffset.UTC);

        assertThat(result)
                .extracting(ReadingAssignment::getDueDate)
                .containsExactly(expectedDueDate, expectedDueDate);

        ArgumentCaptor<List<ReadingAssignment>> assignmentsCaptor = ArgumentCaptor.forClass(List.class);
        verify(assignmentRepository).saveAll(assignmentsCaptor.capture());
        assertThat(assignmentsCaptor.getValue()).hasSize(2);
    }

    @Test
    void createAssignmentsThrowsWhenTeacherDoesNotExist() {
        CreateAssignmentRequest request = new CreateAssignmentRequest(
                1L,
                List.of("student-1"),
                LocalDate.now().plusDays(7)
        );

        when(userRepository.findByUserId("missing-teacher")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignmentService.createAssignments("missing-teacher", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Teacher not found");

        verify(bookRepository, never()).findById(any());
        verify(assignmentRepository, never()).saveAll(any());
    }

    @Test
    void createAssignmentsThrowsWhenUserIsNotTeacher() {
        AppUser nonTeacher = new AppUser(
                "Student",
                "student@example.com",
                "student-1",
                UserRole.STUDENT
        );
        CreateAssignmentRequest request = new CreateAssignmentRequest(
                1L,
                List.of("student-2"),
                LocalDate.now().plusDays(7)
        );

        when(userRepository.findByUserId("student-1")).thenReturn(Optional.of(nonTeacher));

        assertThatThrownBy(() -> assignmentService.createAssignments("student-1", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only teachers can create assignments");

        verify(bookRepository, never()).findById(any());
        verify(assignmentRepository, never()).saveAll(any());
    }

    @Test
    void createAssignmentsThrowsWhenBookDoesNotExist() {
        AppUser teacher = new AppUser(
                "Teacher",
                "teacher@example.com",
                "teacher-1",
                UserRole.TEACHER
        );
        CreateAssignmentRequest request = new CreateAssignmentRequest(
                99L,
                List.of("student-1"),
                LocalDate.now().plusDays(7)
        );

        when(userRepository.findByUserId("teacher-1")).thenReturn(Optional.of(teacher));
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignmentService.createAssignments("teacher-1", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book not found");

        verify(assignmentRepository, never()).saveAll(any());
    }

    @Test
    void createAssignmentsThrowsWhenStudentDoesNotExist() {
        AppUser teacher = new AppUser(
                "Teacher",
                "teacher@example.com",
                "teacher-1",
                UserRole.TEACHER
        );
        Book book = new Book(
                "Test Book",
                "Test Author",
                "Test Description",
                "https://example.com/book"
        );
        CreateAssignmentRequest request = new CreateAssignmentRequest(
                1L,
                List.of("missing-student"),
                LocalDate.now().plusDays(7)
        );

        when(userRepository.findByUserId("teacher-1")).thenReturn(Optional.of(teacher));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findByUserId("missing-student")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignmentService.createAssignments("teacher-1", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Student not found: missing-student");

        verify(assignmentRepository, never()).saveAll(any());
    }

    @Test
    void createAssignmentsThrowsWhenTargetUserIsNotStudent() {
        AppUser teacher = new AppUser(
                "Teacher",
                "teacher@example.com",
                "teacher-1",
                UserRole.TEACHER
        );
        AppUser anotherTeacher = new AppUser(
                "Another Teacher",
                "another.teacher@example.com",
                "teacher-2",
                UserRole.TEACHER
        );
        Book book = new Book(
                "Test Book",
                "Test Author",
                "Test Description",
                "https://example.com/book"
        );
        CreateAssignmentRequest request = new CreateAssignmentRequest(
                1L,
                List.of("teacher-2"),
                LocalDate.now().plusDays(7)
        );

        when(userRepository.findByUserId("teacher-1")).thenReturn(Optional.of(teacher));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findByUserId("teacher-2")).thenReturn(Optional.of(anotherTeacher));

        assertThatThrownBy(() -> assignmentService.createAssignments("teacher-1", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Assignments can only be created for students");

        verify(assignmentRepository, never()).saveAll(any());
    }

    @Test
    void getTeacherAssignmentsDelegatesToRepository() {
        ReadingAssignment assignment = new ReadingAssignment(
                new Book("Book", "Author", "Description", "https://example.com/book"),
                new AppUser("Student", "student@example.com", "student-1", UserRole.STUDENT),
                new AppUser("Teacher", "teacher@example.com", "teacher-1", UserRole.TEACHER),
                OffsetDateTime.now().plusDays(1)
        );

        when(assignmentRepository.findByTeacherUserId("teacher-1"))
                .thenReturn(List.of(assignment));

        List<ReadingAssignment> result = assignmentService.getTeacherAssignments("teacher-1");

        assertThat(result).containsExactly(assignment);
        verify(assignmentRepository).findByTeacherUserId("teacher-1");
    }

    @Test
    void getStudentAssignmentsDelegatesToRepository() {
        ReadingAssignment assignment = new ReadingAssignment(
                new Book("Book", "Author", "Description", "https://example.com/book"),
                new AppUser("Student", "student@example.com", "student-1", UserRole.STUDENT),
                new AppUser("Teacher", "teacher@example.com", "teacher-1", UserRole.TEACHER),
                OffsetDateTime.now().plusDays(1)
        );

        when(assignmentRepository.findByStudentUserId("student-1"))
                .thenReturn(List.of(assignment));

        List<ReadingAssignment> result = assignmentService.getStudentAssignments("student-1");

        assertThat(result).containsExactly(assignment);
        verify(assignmentRepository).findByStudentUserId("student-1");
    }

    @Test
    void updateProgressUpdatesOwnAssignmentBeforeDueDate() {
        AppUser student = new AppUser(
                "Student",
                "student@example.com",
                "student-1",
                UserRole.STUDENT
        );
        AppUser teacher = new AppUser(
                "Teacher",
                "teacher@example.com",
                "teacher-1",
                UserRole.TEACHER
        );
        Book book = new Book(
                "Test Book",
                "Test Author",
                "Test Description",
                "https://example.com/book"
        );
        ReadingAssignment assignment = new ReadingAssignment(
                book,
                student,
                teacher,
                OffsetDateTime.now().plusDays(1)
        );
        UpdateProgressRequest request = new UpdateProgressRequest(
                AssignmentStatus.IN_PROGRESS,
                15
        );

        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(assignmentRepository.save(assignment)).thenReturn(assignment);

        ReadingAssignment result = assignmentService.updateProgress("student-1", 1L, request);

        assertThat(result).isSameAs(assignment);
        assertThat(result.getStatus()).isEqualTo(AssignmentStatus.IN_PROGRESS);
        assertThat(result.getStartedReadingAt()).isNotNull();

        verify(assignmentRepository).save(assignment);
    }

    @Test
    void updateProgressThrowsWhenAssignmentDoesNotExist() {
        UpdateProgressRequest request = new UpdateProgressRequest(
                AssignmentStatus.IN_PROGRESS,
                15
        );

        when(assignmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignmentService.updateProgress("student-1", 99L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Assignment not found");

        verify(assignmentRepository, never()).save(any());
    }

    @Test
    void updateProgressThrowsWhenStudentDoesNotOwnAssignment() {
        ReadingAssignment assignment = new ReadingAssignment(
                new Book("Book", "Author", "Description", "https://example.com/book"),
                new AppUser("Student", "student@example.com", "student-1", UserRole.STUDENT),
                new AppUser("Teacher", "teacher@example.com", "teacher-1", UserRole.TEACHER),
                OffsetDateTime.now().plusDays(1)
        );
        UpdateProgressRequest request = new UpdateProgressRequest(
                AssignmentStatus.IN_PROGRESS,
                15
        );

        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));

        assertThatThrownBy(() -> assignmentService.updateProgress("different-student", 1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Students can only update their own assignments");

        verify(assignmentRepository, never()).save(any());
    }

    @Test
    void updateProgressThrowsWhenAssignmentIsPastDueDate() {
        ReadingAssignment assignment = new ReadingAssignment(
                new Book("Book", "Author", "Description", "https://example.com/book"),
                new AppUser("Student", "student@example.com", "student-1", UserRole.STUDENT),
                new AppUser("Teacher", "teacher@example.com", "teacher-1", UserRole.TEACHER),
                OffsetDateTime.now().minusDays(1)
        );
        UpdateProgressRequest request = new UpdateProgressRequest(
                AssignmentStatus.COMPLETED,
                30
        );

        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));

        assertThatThrownBy(() -> assignmentService.updateProgress("student-1", 1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Assignments can only be updated before the due date");

        verify(assignmentRepository, never()).save(any());
    }
}
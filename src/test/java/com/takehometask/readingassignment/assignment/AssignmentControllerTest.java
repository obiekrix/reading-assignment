package com.takehometask.readingassignment.assignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takehometask.readingassignment.assignment.dto.CreateAssignmentRequest;
import com.takehometask.readingassignment.assignment.dto.UpdateProgressRequest;
import com.takehometask.readingassignment.book.Book;
import com.takehometask.readingassignment.user.AppUser;
import com.takehometask.readingassignment.user.UserRole;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssignmentController.class)
class AssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AssignmentService assignmentService;

    @Test
    void getTeacherAssignmentsReturnsAssignmentsForTeacherHeader() throws Exception {
        ReadingAssignment assignment = assignment();

        when(assignmentService.getTeacherAssignments("teacher-1"))
                .thenReturn(List.of(assignment));

        mockMvc.perform(get("/api/assignments")
                        .header("X-User-Id", "teacher-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].book.title").value("Test Book"))
                .andExpect(jsonPath("$[0].student.userId").value("student-1"))
                .andExpect(jsonPath("$[0].teacher.userId").value("teacher-1"))
                .andExpect(jsonPath("$[0].status").value("NOT_STARTED"));

        verify(assignmentService).getTeacherAssignments("teacher-1");
    }

    @Test
    void createAssignmentsPassesTeacherHeaderAndRequestBodyToService() throws Exception {
        CreateAssignmentRequest request = new CreateAssignmentRequest(
                1L,
                List.of("student-1", "student-2"),
                LocalDate.now().plusDays(7)
        );

        when(assignmentService.createAssignments(eq("teacher-1"), org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(assignment()));

        mockMvc.perform(post("/api/assignments")
                        .header("X-User-Id", "teacher-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].book.title").value("Test Book"))
                .andExpect(jsonPath("$[0].student.userId").value("student-1"))
                .andExpect(jsonPath("$[0].teacher.userId").value("teacher-1"));

        ArgumentCaptor<CreateAssignmentRequest> requestCaptor =
                ArgumentCaptor.forClass(CreateAssignmentRequest.class);

        verify(assignmentService).createAssignments(eq("teacher-1"), requestCaptor.capture());

        assertThat(requestCaptor.getValue().bookId()).isEqualTo(1L);
        assertThat(requestCaptor.getValue().studentIds()).containsExactly("student-1", "student-2");
        assertThat(requestCaptor.getValue().dueDate()).isEqualTo(request.dueDate());
    }

    @Test
    void createAssignmentsReturnsBadRequestWhenRequestBodyIsInvalid() throws Exception {
        String invalidRequest = """
                {
                  "bookId": null,
                  "studentIds": [],
                  "dueDate": null
                }
                """;

        mockMvc.perform(post("/api/assignments")
                        .header("X-User-Id", "teacher-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStudentAssignmentsReturnsAssignmentsForStudentHeader() throws Exception {
        ReadingAssignment assignment = assignment();

        when(assignmentService.getStudentAssignments("student-1"))
                .thenReturn(List.of(assignment));

        mockMvc.perform(get("/api/assignments/student")
                        .header("X-User-Id", "student-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].book.title").value("Test Book"))
                .andExpect(jsonPath("$[0].student.userId").value("student-1"))
                .andExpect(jsonPath("$[0].status").value("NOT_STARTED"));

        verify(assignmentService).getStudentAssignments("student-1");
    }

    @Test
    void updateProgressPassesStudentHeaderAssignmentIdAndRequestBodyToService() throws Exception {
        UpdateProgressRequest request = new UpdateProgressRequest(
                AssignmentStatus.IN_PROGRESS,
                20
        );

        ReadingAssignment updatedAssignment = assignment();
        updatedAssignment.updateProgress(AssignmentStatus.IN_PROGRESS);

        when(assignmentService.updateProgress(eq("student-1"), eq(10L), org.mockito.ArgumentMatchers.any()))
                .thenReturn(updatedAssignment);

        mockMvc.perform(patch("/api/assignments/10/progress")
                        .header("X-User-Id", "student-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.book.title").value("Test Book"))
                .andExpect(jsonPath("$.student.userId").value("student-1"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        ArgumentCaptor<UpdateProgressRequest> requestCaptor =
                ArgumentCaptor.forClass(UpdateProgressRequest.class);

        verify(assignmentService).updateProgress(eq("student-1"), eq(10L), requestCaptor.capture());

        assertThat(requestCaptor.getValue().status()).isEqualTo(AssignmentStatus.IN_PROGRESS);
        assertThat(requestCaptor.getValue().minutesRead()).isEqualTo(20);
    }

    @Test
    void updateProgressReturnsBadRequestWhenRequestBodyIsInvalid() throws Exception {
        String invalidRequest = """
                {
                  "status": null,
                  "minutesRead": -1
                }
                """;

        mockMvc.perform(patch("/api/assignments/10/progress")
                        .header("X-User-Id", "student-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void controllerReturnsBadRequestWhenServiceThrowsIllegalArgumentException() throws Exception {
        UpdateProgressRequest request = new UpdateProgressRequest(
                AssignmentStatus.COMPLETED,
                30
        );

        when(assignmentService.updateProgress(eq("student-1"), eq(10L), org.mockito.ArgumentMatchers.any()))
                .thenThrow(new IllegalArgumentException("Assignments can only be updated before the due date"));

        mockMvc.perform(patch("/api/assignments/10/progress")
                        .header("X-User-Id", "student-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Assignments can only be updated before the due date"));
    }

    private ReadingAssignment assignment() {
        Book book = new Book(
                "Test Book",
                "Test Author",
                "Test Description",
                "https://example.com/book"
        );
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

        return new ReadingAssignment(
                book,
                student,
                teacher,
                OffsetDateTime.now().plusDays(7)
        );
    }
}
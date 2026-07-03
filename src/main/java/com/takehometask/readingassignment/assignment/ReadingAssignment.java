package com.takehometask.readingassignment.assignment;

import com.takehometask.readingassignment.book.Book;
import com.takehometask.readingassignment.user.AppUser;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
public class ReadingAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Book book;

    @ManyToOne(optional = false)
    private AppUser student;

    @ManyToOne(optional = false)
    private AppUser teacher;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private AssignmentStatus status = AssignmentStatus.NOT_STARTED;

    private OffsetDateTime createdAt = OffsetDateTime.now();

    private OffsetDateTime startedReadingAt;

    private OffsetDateTime finishedReadingAt;

    protected ReadingAssignment() {
    }

    public ReadingAssignment(Book book, AppUser student, AppUser teacher, LocalDate dueDate) {
        this.book = book;
        this.student = student;
        this.teacher = teacher;
        this.dueDate = dueDate;
    }

    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public AppUser getStudent() {
        return student;
    }

    public AppUser getTeacher() {
        return teacher;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getStartedReadingAt() {
        return startedReadingAt;
    }

    public OffsetDateTime getFinishedReadingAt() {
        return finishedReadingAt;
    }

    public void updateProgress(AssignmentStatus status) {
        this.status = status;

        if (status.equals(AssignmentStatus.IN_PROGRESS)) {
            this.startedReadingAt = OffsetDateTime.now();
        } else if (status.equals(AssignmentStatus.COMPLETED)) {
            this.finishedReadingAt = OffsetDateTime.now();
        }
    }
}
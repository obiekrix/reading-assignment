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

    private int minutesRead = 0;

    private OffsetDateTime createdAt = OffsetDateTime.now();

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

    public int getMinutesRead() {
        return minutesRead;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void updateProgress(AssignmentStatus status, int minutesRead) {
        this.status = status;
        this.minutesRead = Math.max(minutesRead, 0);
    }
}
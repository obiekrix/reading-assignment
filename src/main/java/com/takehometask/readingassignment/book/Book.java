package com.takehometask.readingassignment.book;

import jakarta.persistence.*;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String author;

    @Column(length = 1000)
    private String description;

    private String contentUrl;

    protected Book() {
    }

    public Book(String title, String author, String description, String contentUrl) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.contentUrl = contentUrl;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getContentUrl() {
        return contentUrl;
    }
}
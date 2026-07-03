package com.takehometask.readingassignment.config;

import com.takehometask.readingassignment.book.Book;
import com.takehometask.readingassignment.book.BookRepository;
import com.takehometask.readingassignment.user.AppUser;
import com.takehometask.readingassignment.user.UserRepository;
import com.takehometask.readingassignment.user.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(BookRepository bookRepository, UserRepository userRepository) {
        return args -> {
            userRepository.save(new AppUser("Demo Teacher", "teacher@example.com", UserRole.TEACHER));
            userRepository.save(new AppUser("Demo Student", "student@example.com", UserRole.STUDENT));
            userRepository.save(new AppUser("Alex Reader", "alex@example.com", UserRole.STUDENT));
            userRepository.save(new AppUser("Sam Learner", "sam@example.com", UserRole.STUDENT));

            bookRepository.save(new Book(
                    "The Secret Garden",
                    "Frances Hodgson Burnett",
                    "A classic story about friendship, healing, and discovery.",
                    "https://www.gutenberg.org/ebooks/17396"
            ));

            bookRepository.save(new Book(
                    "Alice's Adventures in Wonderland",
                    "Lewis Carroll",
                    "A whimsical journey through Wonderland.",
                    "https://www.gutenberg.org/ebooks/11"
            ));

            bookRepository.save(new Book(
                    "The Wonderful Wizard of Oz",
                    "L. Frank Baum",
                    "Dorothy's adventure through the magical land of Oz.",
                    "https://www.gutenberg.org/ebooks/55"
            ));
        };
    }
}
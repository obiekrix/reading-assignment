package com.takehometask.readingassignment.config;

import com.takehometask.readingassignment.book.Book;
import com.takehometask.readingassignment.book.BookRepository;
import com.takehometask.readingassignment.user.AppUser;
import com.takehometask.readingassignment.user.UserRepository;
import com.takehometask.readingassignment.user.UserRole;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.CommandLineRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class DataSeederTest {

    @Test
    void seedDataSavesExpectedDemoUsers() throws Exception {
        BookRepository bookRepository = mock(BookRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        DataSeeder dataSeeder = new DataSeeder();
        CommandLineRunner runner = dataSeeder.seedData(bookRepository, userRepository);

        runner.run();

        ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository, times(4)).save(userCaptor.capture());

        List<AppUser> savedUsers = userCaptor.getAllValues();

        assertThat(savedUsers)
                .extracting(AppUser::getName)
                .containsExactly(
                        "Demo Teacher",
                        "Demo Student",
                        "Alex Reader",
                        "Sam Learner"
                );

        assertThat(savedUsers)
                .extracting(AppUser::getEmail)
                .containsExactly(
                        "teacher@example.com",
                        "student@example.com",
                        "alex@example.com",
                        "sam@example.com"
                );

        assertThat(savedUsers)
                .extracting(AppUser::getUserId)
                .containsExactly("T1", "S1", "S2", "S3");

        assertThat(savedUsers)
                .extracting(AppUser::getRole)
                .containsExactly(
                        UserRole.TEACHER,
                        UserRole.STUDENT,
                        UserRole.STUDENT,
                        UserRole.STUDENT
                );
    }

    @Test
    void seedDataSavesExpectedDemoBooks() throws Exception {
        BookRepository bookRepository = mock(BookRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        DataSeeder dataSeeder = new DataSeeder();
        CommandLineRunner runner = dataSeeder.seedData(bookRepository, userRepository);

        runner.run();

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository, times(3)).save(bookCaptor.capture());

        List<Book> savedBooks = bookCaptor.getAllValues();

        assertThat(savedBooks)
                .extracting(Book::getTitle)
                .containsExactly(
                        "The Secret Garden",
                        "Alice's Adventures in Wonderland",
                        "The Wonderful Wizard of Oz"
                );

        assertThat(savedBooks)
                .extracting(Book::getAuthor)
                .containsExactly(
                        "Frances Hodgson Burnett",
                        "Lewis Carroll",
                        "L. Frank Baum"
                );

        assertThat(savedBooks)
                .extracting(Book::getDescription)
                .containsExactly(
                        "A classic story about friendship, healing, and discovery.",
                        "A whimsical journey through Wonderland.",
                        "Dorothy's adventure through the magical land of Oz."
                );

        assertThat(savedBooks)
                .extracting(Book::getContentUrl)
                .containsExactly(
                        "https://www.gutenberg.org/ebooks/17396",
                        "https://www.gutenberg.org/ebooks/11",
                        "https://www.gutenberg.org/ebooks/55"
                );
    }

    @Test
    void seedDataSavesUsersBeforeBooks() throws Exception {
        BookRepository bookRepository = mock(BookRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        DataSeeder dataSeeder = new DataSeeder();
        CommandLineRunner runner = dataSeeder.seedData(bookRepository, userRepository);

        runner.run();

        verify(userRepository, times(4)).save(org.mockito.ArgumentMatchers.any(AppUser.class));
        verify(bookRepository, times(3)).save(org.mockito.ArgumentMatchers.any(Book.class));
    }
}
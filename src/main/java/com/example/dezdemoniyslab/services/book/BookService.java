package com.example.dezdemoniyslab.services.book;


import com.example.dezdemoniyslab.jms.Producer;
import com.example.dezdemoniyslab.models.Book;
import com.example.dezdemoniyslab.models.LogType;
import com.example.dezdemoniyslab.models.User;
import com.example.dezdemoniyslab.requests.book.BookCreationRequest;
import com.example.dezdemoniyslab.requests.book.BookUpdateRequest;
import com.example.dezdemoniyslab.services.EmailService;
import com.example.dezdemoniyslab.services.user.BaseUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BaseBookService baseBookService;
    private final BaseUserService baseUserService;
    private final Producer producer;
    private final EmailService emailService;


    public List<Book> getAllBooks() {
        return  baseBookService.getAllBooksFromDatabase();
    }

    public  Book getBookById(Long bookId){
        return baseBookService.getBookFromDatabaseById(bookId);
    }

    public List<Book> getAllAuthorBooksByAuthorId(Long userId){
        if (baseUserService.getUserFromDatabaseById(userId) == null) {
            throw new IllegalArgumentException("User with id " + userId + " not exists");
        }
        return baseBookService.getAllBooksFromDatabaseByAuthorId(userId);
    }

    public void updateBook(BookUpdateRequest bookUpdateRequest, Long bookId) {
        Book book = baseBookService.getBookFromDatabaseById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book with id " + bookId + " not exists");
        }
        Long userId = bookUpdateRequest.getAuthorId();
        User user = baseUserService.getUserFromDatabaseById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User with id " + userId + " not exists");
        }
        book.setContent(bookUpdateRequest.getContent());
        book.setUser(user);

        String message = "New values for book with id " + book.getId() + "\n" + bookUpdateRequest;
        producer.sendMessage(book, LogType.UPDATE, message);
        emailService.sendTextEmail(EmailService.adminEmail, LogType.UPDATE.name(), message );

        baseBookService.saveBookToDatabase(book);

    }

    public void deleteBookById(Long bookId){
        Book book = baseBookService.getBookFromDatabaseById(bookId);
        if (book == null){
            throw new IllegalArgumentException("Book with id " + bookId + " not exists");
        }
        String message = "New values for book with id " + book.getId() + "\n" + "isDeleted: " + true;
        producer.sendMessage(book, LogType.UPDATE, message);
        emailService.sendTextEmail(EmailService.adminEmail, LogType.UPDATE.name(), message );

        baseBookService.softDeleteBookFromDatabaseById(book);
    }

    public void createBook(BookCreationRequest bookCreationRequest) {
        Long userId = bookCreationRequest.getAuthorId();
        User user = baseUserService.getUserFromDatabaseById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User with id " + userId + " not exists");
        }

        Book book = Book.builder()
                .content(bookCreationRequest.getContent())
                .user(user)
                .build();

        String message = "Creation a new book  with values" + "\n" + bookCreationRequest;
        producer.sendMessage(book, LogType.CREATE, message);
        emailService.sendTextEmail(EmailService.adminEmail, LogType.CREATE.name(), message );
        baseBookService.saveBookToDatabase(book);
    }

    public String  getBookByIdWithAuthor(Long bookId) {
        Book book = getBookById(bookId);
        if (book == null){
            throw new IllegalArgumentException("Book with id " + bookId + " not exists");
        }
        return book + "\n"+baseUserService.getUserFromDatabaseById(book.getId()).toString();
    }
}

package library.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Author {

    private String name;
    private List<Book> books = new ArrayList<>();

    public Author(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }

    void addBook(Book book) {
        if (!books.contains(book)) {
            books.add(book);
        }
    }
}
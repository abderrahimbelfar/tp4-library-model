package library.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Book {

    private String isbn;
    private String title;
    private int year;

    private List<Author> authors = new ArrayList<>();
    private List<Copy> copies = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();

    public Book(String isbn, String title, int year, Author author) {

        if (author == null) {
            throw new IllegalArgumentException("A book must have at least one author");
        }

        this.isbn = isbn;
        this.title = title;
        this.year = year;

        addAuthor(author);
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public List<Author> getAuthors() {
        return Collections.unmodifiableList(authors);
    }

    public List<Copy> getCopies() {
        return Collections.unmodifiableList(copies);
    }

    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(reservations);
    }

    public void addAuthor(Author author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }

        if (!authors.contains(author)) {
            authors.add(author);
            author.addBook(this);
        }
    }

    void addCopy(Copy copy) {
        if (!copies.contains(copy)) {
            copies.add(copy);
        }
    }

    void addReservation(Reservation reservation) {
        if (!reservations.contains(reservation)) {
            reservations.add(reservation);
            reservations.sort(
                    Comparator.comparing(Reservation::getDate)
            );
        }
    }

    public int availableCopies() {
        int count = 0;

        for (Copy copy : copies) {
            if (copy.isAvailable()) {
                count++;
            }
        }

        return count;
    }

    public Reservation firstReservation() {
        if (reservations.isEmpty()) {
            return null;
        }

        return reservations.get(0);
    }
}
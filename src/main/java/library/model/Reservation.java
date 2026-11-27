package library.model;

import java.time.LocalDate;

public class Reservation {

    private Member member;
    private Book book;
    private LocalDate date;

    Reservation(Member member, Book book, LocalDate date) {

        if (member == null||  book == null || date == null) {
            throw new IllegalArgumentException("Values cannot be null");
        }

        this.member = member;
        this.book = book;
        this.date = date;

        member.addReservation(this);
        book.addReservation(this);
    }

    public Member getMember() {
        return member;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getDate() {
        return date;
    }
}
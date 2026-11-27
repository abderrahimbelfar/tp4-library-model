package library.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Member {

    private String memberNumber;
    private String name;
    private String email;

    private List<Loan> loans = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();

    public Member(String memberNumber, String name, String email) {
        this.memberNumber = memberNumber;
        this.name = name;
        this.email = email;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<Loan> getLoans() {
        return Collections.unmodifiableList(loans);
    }

    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(reservations);
    }

    public abstract int maxLoans();

    public boolean canBorrow() {
        long activeLoans = loans.stream()
                .filter(loan -> loan.getReturnDate() == null)
                .count();

        return activeLoans < maxLoans();
    }

    public Loan borrow(Copy copy, LocalDate today) {
        if (!canBorrow()) {
            throw new IllegalStateException("Maximum loans reached");
        }

        if (copy == null || !copy.isAvailable()) {
            throw new IllegalStateException("Copy is not available");
        }

        return new Loan(this, copy, today);
    }

    public Reservation reserve(Book book, LocalDate today) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        return new Reservation(this, book, today);
    }

    void addLoan(Loan loan) {
        loans.add(loan);
    }

    void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

}
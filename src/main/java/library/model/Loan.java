package library.model;

import java.time.LocalDate;

public class Loan {

    private Member member;
    private Copy copy;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    Loan(Member member, Copy copy, LocalDate loanDate) {

        if (member == null || copy == null || loanDate == null) {
            throw new IllegalArgumentException("Values cannot be null");
        }

        if (!copy.isAvailable()) {
            throw new IllegalStateException("Copy is not available");
        }

        this.member = member;
        this.copy = copy;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(14);

        member.addLoan(this);
        copy.addLoan(this);
        copy.markBorrowed();
    }

    public Member getMember() {
        return member;
    }

    public Copy getCopy() {
        return copy;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void close(LocalDate returnDate) {

        if (returnDate == null) {
            throw new IllegalArgumentException("Return date cannot be null");
        }

        if (this.returnDate != null) {
            throw new IllegalStateException("Loan already closed");
        }

        if (returnDate.isBefore(loanDate)) {
            throw new IllegalArgumentException("Invalid return date");
        }

        this.returnDate = returnDate;
        copy.markAvailable();
    }

    public boolean isLate(LocalDate today) {
        return returnDate == null && today.isAfter(dueDate);
    }
}
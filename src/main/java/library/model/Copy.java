package library.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Copy {

    private Book book;
    private String inventoryNumber;
    private CopyStatus status;
    private List<Loan> loans = new ArrayList<>();

    public Copy(Book book, String inventoryNumber) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        this.book = book;
        this.inventoryNumber = inventoryNumber;
        this.status = CopyStatus.AVAILABLE;

        book.addCopy(this);
    }

    public Book getBook() {
        return book;
    }

    public String getInventoryNumber() {
        return inventoryNumber;
    }

    public CopyStatus getStatus() {
        return status;
    }

    public boolean isAvailable() {
        return status == CopyStatus.AVAILABLE;
    }

    public List<Loan> getLoans() {
        return Collections.unmodifiableList(loans);
    }

    void addLoan(Loan loan) {
        loans.add(loan);
    }

    public Loan getCurrentLoan() {
        for (Loan loan : loans) {
            if (loan.getReturnDate() == null) {
                return loan;
            }
        }
        return null;
    }

    public void markLost() {
        if (!isAvailable()) {
            throw new IllegalStateException("Only available copies can be marked lost");
        }
        status = CopyStatus.LOST;
    }

    void markBorrowed() {
        status = CopyStatus.BORROWED;
    }

    void markAvailable() {
        status = CopyStatus.AVAILABLE;
    }
}
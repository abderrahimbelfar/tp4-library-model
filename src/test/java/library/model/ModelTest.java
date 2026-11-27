package library.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    private final LocalDate today = LocalDate.of(2026, 10, 1);

    private Author createAuthor() {
        return new Author("Author One");
    }

    private Book createBook() {
        Author author = createAuthor();
        return new Book("12345", "Java Book", 2020, author);
    }

    // 1. Test bidirectional relationship between Book and Author
    @Test
    void testBookAuthorRelationship() {
        Author author1 = new Author("Author One");
        Author author2 = new Author("Author Two");

        Book book = new Book("123", "Java", 2020, author1);
        book.addAuthor(author2);

        assertTrue(book.getAuthors().contains(author1));
        assertTrue(book.getAuthors().contains(author2));

        assertTrue(author1.getBooks().contains(book));
        assertTrue(author2.getBooks().contains(book));
    }

    // 2. Test that a new copy is available and registered in its book
    @Test
    void testNewCopy() {
        Book book = createBook();

        Copy copy = new Copy(book, "C001");

        assertTrue(copy.isAvailable());
        assertEquals(CopyStatus.AVAILABLE, copy.getStatus());
        assertTrue(book.getCopies().contains(copy));
    }

    // 3. Test borrowing a copy
    @Test
    void testBorrowCopy() {
        Book book = createBook();
        Copy copy = new Copy(book, "C001");

        Member member = new StudentMember(
                "S001", "Ali", "ali@email.com", "Master"
        );

        Loan loan = member.borrow(copy, today);

        assertTrue(member.getLoans().contains(loan));
        assertTrue(copy.getLoans().contains(loan));
        assertEquals(CopyStatus.BORROWED, copy.getStatus());
        assertFalse(copy.isAvailable());
        assertEquals(today.plusDays(14), loan.getDueDate());
    }

    // 4. Test maximum number of loans
    @Test
    void testMaximumLoans() {
        Book book = createBook();

        Member student = new StudentMember(
                "S001", "Ali", "ali@email.com", "Master"
        );

        for (int i = 0; i < 5; i++) {
            Copy copy = new Copy(book, "S00" + i);
            student.borrow(copy, today);
        }

        assertThrows(IllegalStateException.class, () -> {
            Copy sixthCopy = new Copy(book, "S006");
            student.borrow(sixthCopy, today);
        });

        Member teacher = new TeacherMember(
                "T001", "Ahmed", "ahmed@email.com", "Computer Science"
        );

        for (int i = 0; i < 10; i++) {
            Copy copy = new Copy(book, "T00" + i);
            teacher.borrow(copy, today);
        }

        assertEquals(10, teacher.getLoans().size());
    }

    // 5. Test that a borrowed copy cannot be borrowed again
    @Test
    void testCannotBorrowTwice() {
        Book book = createBook();
        Copy copy = new Copy(book, "C001");

        Member member1 = new StudentMember(
                "S001", "Ali", "ali@email.com", "Master"
        );

        Member member2 = new TeacherMember(
                "T001", "Ahmed", "ahmed@email.com", "IT"
        );

        member1.borrow(copy, today);

        assertThrows(IllegalStateException.class, () -> {
            member2.borrow(copy, today);
        });
    }

    // 6. Test closing a loan and keeping loan history
    @Test
    void testCloseLoan() {
        Book book = createBook();
        Copy copy = new Copy(book, "C001");

        Member member = new StudentMember(
                "S001", "Ali", "ali@email.com", "Master"
        );

        Loan loan = member.borrow(copy, today);

        loan.close(today.plusDays(5));

        assertTrue(copy.isAvailable());
        assertEquals(CopyStatus.AVAILABLE, copy.getStatus());
        assertEquals(1, member.getLoans().size());
        assertEquals(1, copy.getLoans().size());
        assertNotNull(loan.getReturnDate());
    }
    // 7. Test late loan
    @Test
    void testLateLoan() {
        Book book = createBook();
        Copy copy = new Copy(book, "C001");

        Member member = new StudentMember(
                "S001", "Ali", "ali@email.com", "Master"
        );

        Loan loan = member.borrow(copy, today);

        assertFalse(loan.isLate(today.plusDays(14)));
        assertTrue(loan.isLate(today.plusDays(15)));
    }

    // 8. Test unmodifiable collections
    @Test
    void testUnmodifiableCollections() {
        Author author = createAuthor();
        Book book = new Book("123", "Java", 2020, author);
        Copy copy = new Copy(book, "C001");

        Member member = new StudentMember(
                "S001", "Ali", "ali@email.com", "Master"
        );

        member.borrow(copy, today);

        assertThrows(UnsupportedOperationException.class, () -> {
            book.getAuthors().add(new Author("Another Author"));
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            book.getCopies().clear();
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            member.getLoans().clear();
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            copy.getLoans().clear();
        });
    }

    // 9. Test reservations ordered by date
    @Test
    void testReservationsOrder() {
        Book book = createBook();

        Member member1 = new StudentMember(
                "S001", "Ali", "ali@email.com", "Master"
        );

        Member member2 = new TeacherMember(
                "T001", "Ahmed", "ahmed@email.com", "IT"
        );

        Reservation second = member1.reserve(book, today.plusDays(2));
        Reservation first = member2.reserve(book, today.plusDays(1));

        assertEquals(first, book.firstReservation());
        assertEquals(List.of(first, second), book.getReservations());
    }

    // 10. Test that only available copies can be marked lost
    @Test
    void testMarkLost() {
        Book book = createBook();
        Copy copy = new Copy(book, "C001");

        copy.markLost();

        assertEquals(CopyStatus.LOST, copy.getStatus());
        assertFalse(copy.isAvailable());

        Copy borrowedCopy = new Copy(book, "C002");

        Member member = new StudentMember(
                "S001", "Ali", "ali@email.com", "Master"
        );

        member.borrow(borrowedCopy, today);

        assertThrows(IllegalStateException.class, () -> {
            borrowedCopy.markLost();
        });
    }
    @Test
    void testPartEScenario() {

        // Create a book with two authors
        Author author1 = new Author("First Author");
        Author author2 = new Author("Second Author");

        Book book = new Book("978-0-00-000001-1",
                "Software Engineering", 2016, author1);

        book.addAuthor(author2);

        // Create two copies
        Copy c1 = new Copy(book, "C-001");
        Copy c2 = new Copy(book, "C-002");

        // Create a student and a teacher
        Member student = new StudentMember(
                "M-01", "Student 1", "s1@example.org", "L3");

        Member teacher = new TeacherMember(
                "M-02", "Teacher 1", "t1@example.org", "Computer Science");

        // Student borrows both copies on 1 October 2026
        Loan loan1 = student.borrow(c1, LocalDate.of(2026, 10, 1));
        Loan loan2 = student.borrow(c2, LocalDate.of(2026, 10, 1));

        // Teacher reserves the book on the next day
        Reservation reservation =
                teacher.reserve(book, LocalDate.of(2026, 10, 2));

        // Check authors
        assertEquals(2, book.getAuthors().size());
        assertTrue(book.getAuthors().contains(author1));
        assertTrue(book.getAuthors().contains(author2));

        // Check copies
        assertEquals(2, book.getCopies().size());
        assertTrue(book.getCopies().contains(c1));
        assertTrue(book.getCopies().contains(c2));

        // Check loans
        assertTrue(student.getLoans().contains(loan1));
        assertTrue(student.getLoans().contains(loan2));
        assertTrue(c1.getLoans().contains(loan1));
        assertTrue(c2.getLoans().contains(loan2));

        // Check copy status
        assertEquals(CopyStatus.BORROWED, c1.getStatus());
        assertEquals(CopyStatus.BORROWED, c2.getStatus());

        // Check reservation
        assertEquals(reservation, book.firstReservation());
    }
}
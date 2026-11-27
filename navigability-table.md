# Part B – Design Decisions

## 1. Navigability Table

| Association | Direction(s)           | Needed by |
|---|------------------------|---|
| Member – Loan | Member <-> Loan        | Borrowing a copy |
| Loan – Copy | Copy <-> Loan          | Returning a copy |
| Copy – Book | Book <-> Copy          | Counting available copies and accessing the book of a copy|
| Member – Reservation | Member <-> Reservation | Reserving a book |
| Reservation – Book | Book <-> Reservation   | Finding the first reservation |
| Book – Author | Book <-> Author        | Accessing authors and their books | 
## 2. Required Operations

### Member
- borrow(copy: Copy, today: LocalDate): Loan
- reserve(book: Book, today: LocalDate): Reservation
- canBorrow(): boolean
- maxLoans(): int

### Copy
- getCurrentLoan(): Loan
- isAvailable(): boolean

### Loan
- close(returnDate: LocalDate): void
- isLate(today: LocalDate): boolean

### Book
- firstReservation(): Reservation
- availableCopies(): int
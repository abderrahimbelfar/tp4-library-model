TP 4 – From Class Diagrams to Java

Answers to the Questions (a) to (f)

Question (a)

Why is the constructor of Loan not public? What could happen if any class could create a loan?

A loan is valid only if the member is allowed to borrow and the copy is available. These checks are done in "Member.borrow(Copy, LocalDate)". The constructor of Loan registers the loan in the member and in the copy, so a public constructor would let any class create a loan directly and bypass the rules:

- A student could exceed the limit of 5 loans.
- A copy could be lent twice.
- The status of the copy might not change to BORROWED.
- A loan could exist with a missing link, so the model becomes inconsistent.

With a package-visible constructor, code outside "library.model" cannot build a loan. The only entry point is the method that checks the rules.

Question (b)

How does your code guarantee the multiplicity 1. .*of the authors of a book? Which multiplicity of the association cannot be guaranteed by the constructors, and how could it be checked?*

The constructor of Book requires at least one author (a non-null author), so a book is never created without an author. The class has "addAuthor" but no public method to remove the last author, and the getter returns an unmodifiable view. So the multiplicity 1. .* on the authors side is guaranteed.

The multiplicity that cannot be guaranteed by the constructors is the other end of the association: an author must have at least one book (1. .*). An Author is created with only a name, before any book exists, so for a moment it has no book. It can be checked by a verification method (invariant: every author has at least one book) run after the objects are created, by a unit test, or by the OCL constraint "self.books->notEmpty()" on Author.

Question (c)

Why do the getters return unmodifiable views of the collections instead of the collections themselves?

If a getter returned the real list, any client could add or remove elements without using the methods of the model. This would break encapsulation, the consistency of bidirectional associations (a loan added to a member but not to its copy) and the business rules (limit of loans, availability). An unmodifiable view ("Collections.unmodifiableList") lets the client read the collection, always up to date, while any attempt to modify it throws "UnsupportedOperationException". It is also cheaper than copying the list.

Question (d)

Figure 1 has "isLate() : Boolean", your code "isLate(LocalDate today)". Why? Is the diagram wrong?

The diagram is not wrong. Figure 1 is a domain model at a high level of abstraction: it says that a loan can tell whether it is late, and the current date is implicit. In the design and the code, this information must become explicit. Taking "today" as a parameter makes the method independent of the system clock (no "LocalDate.now()"), so it can be tested with fixed dates: a loan due on 15 October is not late on 15 October and is late on 16 October. The types are also refined: Boolean becomes "boolean".

Question (e)

Member is specialized into students and teachers. What happens in your program when a student becomes a teacher? Propose another design (Chapter 4, Table 4.4).

In Java, the class of an object is fixed at creation. A "StudentMember" cannot become a "TeacherMember". The program would have to create a new "TeacherMember", copy the data and move all loans and reservations to it. Other references to the old object stay wrong, the identity of the object changes, and the history can be damaged. Generalization is a poor choice when an object can change category during its life.

Another design: use composition instead of inheritance. Member becomes a concrete class with a link to a category object, for example an abstract class "MemberCategory" with "maxLoans()", specialized in "StudentCategory" and "TeacherCategory" (or an enumeration with the limit as a field). Member delegates "maxLoans()" to its category.
When a student becomes a teacher, only the category link changes; the member, its loans and its reservations stay the same.

Remark: Table 4.4 is not reproduced in the TP document. This is the standard composition/delegation alternative; compare it with the exact wording of Table 4.4 in the course.

Question (f)

Which associations did you make bidirectional? What does a bidirectional association cost?

All six associations of the model are bidirectional: Book – Author, Copy – Book, Member – Loan, Loan – Copy, Member – Reservation and Book – Reservation. For the last one, Book keeps the list of its reservations (needed by "firstReservation()") and Reservation keeps a reference to its Book. The generalization of Member is not an association. A bidirectional association has a cost:

- Consistency: every change must update both ends (for example "Member.reserve(Book, LocalDate)" registers the reservation in the member and in the book); forgetting one end makes the model inconsistent.
- More code and tests: package-visible methods and constructors that register objects in each other.
- Stronger coupling: the two classes depend on each other and change together.
- Technical risks: infinite recursion in "toString()", "equals()", "hashCode()" and cycles in serialization.
- Memory: each link is stored twice.

This cost is accepted because both directions are useful: from a book to its reservations, and from a reservation back to its book and its member.
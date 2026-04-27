package org.example;

public class AuthorBook {
    private final String firstName;
    private final String lastName;
    private final String isbn;
    private final String title;

    public AuthorBook(String firstName, String lastName, String isbn, String title) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.isbn = isbn;
        this.title = title;
    }

    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }
    public String getIsbn()      { return isbn; }
    public String getTitle()     { return title; }
}

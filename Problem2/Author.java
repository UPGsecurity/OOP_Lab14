package org.example;

public class Author {
    private final int authorID;
    private final String firstName;
    private final String lastName;

    public Author(int authorID, String firstName, String lastName) {
        this.authorID = authorID;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public int getAuthorID() { return authorID; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}

package com.example.urbookproject;

public class BookDataMatch {
    private String userID;
    private String username;
    private String price;
    private String transactionType;
    private BookData incomingBook;
    private BookData outgoingBook;

    public BookDataMatch() {
        this.userID = "";
        this.username = "";
        this.transactionType = "";
        this.price = "";
        this.incomingBook = new BookData();
        this.outgoingBook = new BookData();
    }

    public BookDataMatch(String userID, String username, String price, String transactionType,
                         BookData incomingBook, BookData outgoingBook) {
        this.userID = userID;
        this.username = username;
        this.price = price;
        this.transactionType = transactionType;
        this.incomingBook = incomingBook;
        this.outgoingBook = outgoingBook;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BookData getIncomingBook() {
        return incomingBook;
    }

    public void setIncomingBook(BookData incomingBook) {
        this.incomingBook = incomingBook;
    }

    public BookData getOutgoingBook() {
        return outgoingBook;
    }

    public void setOutgoingBook(BookData outgoingBook) {
        this.outgoingBook = outgoingBook;
    }

    public String getFilterableString() {
        String filterString = "";

        filterString += " " + this.username;
        filterString += " " + this.price;
        filterString += " " + this.transactionType;
        filterString += " " + this.incomingBook.getAuthor();
        filterString += " " + this.incomingBook.getIsbn10();
        filterString += " " + this.incomingBook.getIsbn13();
        filterString += " " + this.incomingBook.getTitle();
        filterString += " " + this.incomingBook.getYear();
        filterString += " " + this.outgoingBook.getAuthor();
        filterString += " " + this.outgoingBook.getIsbn10();
        filterString += " " + this.outgoingBook.getIsbn13();
        filterString += " " + this.outgoingBook.getTitle();
        filterString += " " + this.outgoingBook.getYear();

        return filterString;
    }
}

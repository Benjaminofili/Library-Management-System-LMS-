/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

/**
 *
 * @author benja
 */
public class Book {
     private int id;
    private String name;
    private String author;
    private double price;
    private String isbn;
     private int quantity; 
    private String category;
    private boolean availability;

    // Constructor to initialize all fields

    public Book(int id, String name, String author, double price, String isbn, String category, boolean availability, int quantity) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.price = price;
        this.isbn = isbn;
        this.category = category;
        this.availability = availability;
        this.quantity = quantity;
    }

    public Book(int id, String name, String author, double price, String isbn, String category, boolean availability) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.price = price;
        this.isbn = isbn;
        this.category = category;
        this.availability = availability;
    }



    public Book(String name, String author, double price, String isbn, String category, boolean availability) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.isbn = isbn;
        this.category = category;
        this.availability = availability;
    }

    public Book(int id, String name, String author, double price, String isbn, boolean availability) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.price = price;
        this.isbn = isbn;
        this.availability = availability;
    }

    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

      public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }
 



}

package com.muproject.campusskill.model;

// Service data model (Hinglish: Student ki service jaise Assignments, Logo design)
public class Service {
    private int id;
    private String sellerName;
    private String title;
    private String description;
    private double price;
    private float rating;
    private int imageResId; // Placeholder image

    public Service(int id, String sellerName, String title, String description, double price, float rating, int imageResId) {
        this.id = id;
        this.sellerName = sellerName;
        this.title = title;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.imageResId = imageResId;
    }

    public int getId() { return id; }
    public String getSellerName() { return sellerName; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public float getRating() { return rating; }
    public int getImageResId() { return imageResId; }
}

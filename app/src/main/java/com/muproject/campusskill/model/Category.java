package com.muproject.campusskill.model;

// Category data model (Hinglish: Services ki categories jaise Programming, Design)
public class Category {
    private int id;
    private String name;
    private int iconResId; // Using local resource for now
    
    @com.google.gson.annotations.SerializedName("service_count")
    private int serviceCount;

    public Category(int id, String name, int iconResId) {
        this.id = id;
        this.name = name;
        this.iconResId = iconResId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getIconResId() { return iconResId; }
    public int getServiceCount() { return serviceCount; }
}

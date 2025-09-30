package com.oceanbreezeresorts.hotel.model;

public class RoomModel {

    private int imageUrl;
    private String title;
    private int id;
    private String price;
    private String rating;

    public String getTitle() {
        return title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getImageUrl() {
        return imageUrl;
    }



    public String getPrice() {
        return price;
    }

    public String getRating() {
        return rating;
    }

    public RoomModel(int imageUrl, String title, String price, String rating) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.price = price;
        this.rating = rating;
    }
}

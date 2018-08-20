package com.oneflaretech.kiakia.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kolawoleadewale on 10/7/17.
 */

public class ReviewModel {
    private int id;
    private ServiceModel service;
    private String name;
    private String image;
    private String message;
    private int rating;

    @SerializedName("created_at")
    private String created;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ServiceModel getService() {
        return service;
    }

    public void setService(ServiceModel service) {
        this.service = service;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "ReviewModel{" +
                "id=" + id +
                ", service=" + service +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", message='" + message + '\'' +
                ", rating=" + rating +
                ", created='" + created + '\'' +
                '}';
    }
}
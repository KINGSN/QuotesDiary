package com.royal.attitude.status.item;

import java.io.Serializable;

public class ItemCat implements Serializable {

    private String id, name, imageBig, imageSmall;

    public ItemCat(String id, String name, String imageBig, String imageSmall) {
        this.id = id;
        this.name = name;
        this.imageBig = imageBig;
        this.imageSmall = imageSmall;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageBig() {
        return imageBig;
    }

    public String getImageSmall() {
        return imageSmall;
    }
}

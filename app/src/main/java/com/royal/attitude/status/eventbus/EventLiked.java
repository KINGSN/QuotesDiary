package com.royal.attitude.status.eventbus;

public class EventLiked {
    private Boolean isLiked;
    private int position;
    private String likes, id, type;

    public EventLiked(Boolean isLiked, int position, String likes, String id, String type) {
        this.isLiked = isLiked;
        this.position = position;
        this.likes = likes;
        this.id = id;
        this.type = type;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public int getPosition() {
        return position;
    }

    public String getLikes() {
        return likes;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
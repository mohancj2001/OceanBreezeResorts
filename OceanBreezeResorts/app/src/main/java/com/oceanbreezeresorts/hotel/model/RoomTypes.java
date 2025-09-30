package com.oceanbreezeresorts.hotel.model;

public class RoomTypes {

    private String id;
    private String room_type;

    public RoomTypes(String id, String room_type) {
        this.id = id;
        this.room_type = room_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }
}

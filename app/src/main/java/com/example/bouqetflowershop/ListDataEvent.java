package com.example.bouqetflowershop;

public class ListDataEvent {
    String date, event, id;

    public ListDataEvent() {}

    public ListDataEvent(String date, String event, String id) {
        this.date = date;
        this.event = event;
        this.id = id;
    }

    public ListDataEvent(String date, String event) {
        this.date = date;
        this.event = event;
    }

    public String getDate() {
        return date;
    }

    public String getEvent() {
        return event;
    }

    public String getId() {
        return id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setId(String id) {
        this.id = id;
    }
}

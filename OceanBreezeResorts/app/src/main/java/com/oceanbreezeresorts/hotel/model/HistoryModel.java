package com.oceanbreezeresorts.hotel.model;

public class HistoryModel {

    private String title;
    private String price;
    private String paymentStatus;
    private String date;
    private String qty;
    private String fromDate;
    private String toDate;

    public HistoryModel(String title, String price, String paymentStatus, String date, String qty, String fromDate, String toDate) {
        this.title = title;
        this.price = price;
        this.paymentStatus = paymentStatus;
        this.date = date;
        this.qty = qty;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getDate() {
        return date;
    }

    public String getQty() {
        return qty;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }
}

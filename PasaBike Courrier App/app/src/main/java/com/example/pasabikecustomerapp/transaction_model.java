package com.example.pasabikecustomerapp;

import com.google.firebase.Timestamp;

public class transaction_model {

    private String courrier_id;
    private String customer_id;
    private String order_id;
    private long status;
    private long fee;
    private Timestamp order_date;
    private String type;


    public transaction_model() {

    }

    public transaction_model(String courrier_id, String customer_id, String order_id, String type, long fee, long status, Timestamp order_date) {
        this.courrier_id = courrier_id;
        this.customer_id = customer_id;
        this.status = status;
        this.order_id = order_id;
        this.order_date = order_date;
        this.fee = fee;
        this.type = type;

    }


    public String getCourrier_id() {
        return courrier_id;
    }

    public void setCourrier_id(String courrier_id) {
        this.courrier_id = courrier_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }


    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public Timestamp getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Timestamp order_date) {
        this.order_date = order_date;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

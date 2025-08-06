package com.example.xpensetracker;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ExpenseModel implements Serializable {
    private String expenseId;
    private String note;
    private String category;
    private String type;
    private long amount;
    private long time;
    private String date;
    private String uid;


    public ExpenseModel() {
    }

    public ExpenseModel(String expenseId, String note, String category, String type, long amount, /*long time,*/ String date, String uid) {
        this.expenseId = expenseId;
        this.note = note;
        this.category = category;
        this.type = type;
        this.amount = amount;
        //this.time = time;
        this.date = date;
        this.uid = uid;


    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(calendar.getTime());

    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

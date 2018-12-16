package com.ht.commonactivity.vo;

public class Leave {

    public int day;
    public int total;

    public Leave(int day,int total ){
        this.day=day;
        this.total=total;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}

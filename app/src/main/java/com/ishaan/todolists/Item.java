package com.ishaan.todolists;

import io.realm.RealmObject;

/**
 * Created by ishaan on 04/05/16.
 */
public class Item extends RealmObject {


    private String email;
    private String color;
    private String listItem;
    private String Place;
    private String dateTime;

    public Item(){
        listItem=Place= dateTime =null;
        email=color=null;

    }

    public Item(String listItem,String place,String dateTime,String email,String color){
        this.listItem=listItem;
        this.email=email;
        this.Place=place;
        this.color=color;
        this.dateTime =dateTime;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getListItem() {
        return listItem;
    }

    public void setListItem(String listItem) {
        this.listItem = listItem;
    }

    public String getPlace() {
        return Place;
    }

    public void setPlace(String place) {
        Place = place;
    }
}

package com.ishaan.todolists;

import io.realm.RealmObject;

/**
 * Created by ishaan on 06/05/16.
 */
public class User extends RealmObject {
    private String email;

    public User()
    {
        email=null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

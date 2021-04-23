package com.example.cookbook;

import java.util.UUID;

public class User {
    private UUID mId;
    private String mFamily;
    private String mName;

    public User(String family, String name){
        mFamily=family;
        mName=name;
        mId =UUID.randomUUID();
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        this.mId = id;
    }

    public String getFamily() {
        return mFamily;
    }

    public void setFamily(String family) {
        mFamily = family;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean IsEqualUser(User u){
        return (this==u);
    }

}

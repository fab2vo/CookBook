package com.example.cookbook;

import java.util.Date;

public class Comment {
    private String mTxt;
    private User mUser;
    private Date mDate;


    public Comment(String s, User u){
        mTxt=s;
        mUser=u;
        mDate=new Date();
    }

    public String getTxt() {
        return mTxt;
    }

    public User getUser() {
        return mUser;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    @Override
    public String toString(){
        return mTxt+" ("+mUser.getNameComplete()+")";
    }
}

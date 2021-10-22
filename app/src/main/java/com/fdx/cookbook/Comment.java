package com.fdx.cookbook;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment {
    private String mTxt;
    private User mUser;
    private Date mDate;

    public Comment(){
        mTxt="";
        mUser=new User("","");
        mDate=new Date();
    }

    public Comment(String s, User u){
        mTxt=s;
        mUser=u;
        mDate=new Date();
    }
    public Comment(String s, User u, Date d){
        mTxt=s;
        mUser=u;
        mDate=d;
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

    public String toTxt(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String s= dateFormat.format(mDate);
        return mTxt+" ("+mUser.getNameComplete()+") - " + s;
    }

    @Override
    public boolean equals(Object o){
        if (o == null) return false;
        if (o == this) return true;
        if (getClass() != o.getClass()) return false;
        Comment c=(Comment) o;
        boolean b=(this.mTxt.equals(c.getTxt()));
        b=b && (mDate.toString().equals(c.getDate().toString()));
        b=b && (mUser.equals(c.getUser()));
        return b;
    }
}

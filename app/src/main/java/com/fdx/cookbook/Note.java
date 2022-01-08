package com.fdx.cookbook;

import java.util.Date;

public class Note {
    private Integer mNote;
    private User mUser;
    private Date mDate;


    public Note(Integer note, User u){
        mNote=note;
        mUser=u;
        mDate=new Date();
    }

    public Note(Integer note, User u, Date d){
        mNote=note;
        mUser=u;
        mDate=d;
    }

    public Integer getNote() {
        return mNote;
    }

    public User getUser() {
        return mUser;
    }

    public Date getDate() {
        return mDate;
    }

    public void setNote(Integer note) {
        mNote = note;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    @Override
    public boolean equals(Object o){
        if (o == null) return false;
        if (o == this) return true;
        if (getClass() != o.getClass()) return false;
        Note n=(Note) o;
        if (mNote==null) return false;
        if (n.getNote()==null) return false;
        boolean b=(this.mNote.equals(n.getNote()));
        b=b && (mDate.toString().equals(n.getDate().toString()));
        b=b && (mUser.equals(n.getUser()));
        return b;
    }
}

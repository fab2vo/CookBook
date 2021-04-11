package com.example.cookbook;

import java.util.Date;
import java.util.UUID;

public class Recipe {
    private UUID mId;
    private Date mDate;
    private String mTitle;
    private String mSource;
    private String[] mStep;
    private int mNote;

    public Recipe( int nbstep) {
        mId=UUID.randomUUID();
        mDate= new Date();
        mStep= new String[nbstep];
    }

    public UUID getId() {
        return mId;
    }

    public Date getDate() {
        return mDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSource() {
        return mSource;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setSource(String source) {
        mSource = source;
    }

    public void setStep(String[] step) {
        mStep = step;
    }

    public String[] getStep() {
        return mStep;
    }

    public int getNote() {
        return mNote;
    }

    public void setNote(int note) {
        mNote = note;
    }

    public void setS1(String step) {
        mStep[0] = step;
    }
    public void setS2(String step) {
        mStep[1] = step;
    }
    public void setS3(String step) {
        mStep[2] = step;
    }
    public void setS4(String step) {
        mStep[3] = step;
    }

    public String getS1(){return mStep[0];}
    public String getS2(){return mStep[1];}
    public String getS3(){return mStep[2];}
    public String getS4(){return mStep[3];}
}

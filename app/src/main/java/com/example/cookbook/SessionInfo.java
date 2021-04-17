package com.example.cookbook;

import android.content.Context;

public class SessionInfo {
    private static SessionInfo ourInstance;
    private String mFamily;
    private String mMember;
    private Context mContext;

    public static SessionInfo get(Context context) {
        if (ourInstance==null){
            ourInstance= new SessionInfo(context);
        }
        return ourInstance;
    }

    private SessionInfo(Context context) {
        mContext=context.getApplicationContext();
        mFamily="Devaux_Lion de ML";
        mMember="Fabrice";
    }

    public String getFamily() {
        return mFamily;
    }

    public void setFamily(String family) {
        mFamily = family;
    }

    public String getMember() {
        return mMember;
    }

    public void setMember(String member) {
        mMember = member;
    }
}

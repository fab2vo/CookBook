package com.example.cookbook;

import android.content.Context;

import java.util.UUID;

public class SessionInfo {
    private static SessionInfo ourInstance;
    private User mUser;
    private Context mContext;

    public static SessionInfo get(Context context) {
        if (ourInstance==null){
            ourInstance= new SessionInfo(context);
        }
        return ourInstance;
    }

    private SessionInfo(Context context) {
        mContext=context.getApplicationContext();
        mUser=new User("Devaux_Lion de ML","Fabrice");
        // UID constant pour phase debug
        mUser.setId(UUID.fromString("c81d4e2e-bcf2-11e6-869b-7df92533d2db"));
    }

    public String getUserNameComplete() {
        return mUser.getNameComplete();
    }

    public User getUser() {
        return mUser;
    }

}

package com.example.cookbook;

import android.content.Context;
import android.preference.PreferenceManager;

import java.util.UUID;

public class SessionInfo {
    private static SessionInfo ourInstance;
    private User mUser;
    private Context mContext;
    private String CB_FAMILY="family";
    private String CB_NAME="name";
    private String CB_ID="iduser";
    private String NOT_FOUND="Not found";

    public static SessionInfo get(Context context) {
        if (ourInstance==null){
            ourInstance= new SessionInfo(context);
        }
        return ourInstance;
    }

    private SessionInfo(Context context) {
        mContext=context.getApplicationContext();
        String sharedPref;
        sharedPref= PreferenceManager.getDefaultSharedPreferences(context)
                .getString(CB_ID, NOT_FOUND);
        if (sharedPref.equals(NOT_FOUND)){
            mUser = new User(NOT_FOUND, NOT_FOUND);
        } else {
            mUser = new User(PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(CB_FAMILY, NOT_FOUND), PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(CB_NAME, NOT_FOUND));
            mUser.setId(UUID.fromString(sharedPref));
        }
    }

    public String getUserNameComplete() {
        return mUser.getNameComplete();
    }

    public User getUser() {
        return mUser;
    }

    public void setStoredUser(User user){
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putString(CB_FAMILY, user.getFamily())
                .putString(CB_NAME, user.getName())
                .putString(CB_ID, user.getId().toString())
                .apply();
    }

    public Boolean IsEmpty(){
        if (mUser.getName().equals(NOT_FOUND)){return true;}
        return false;
    }

}

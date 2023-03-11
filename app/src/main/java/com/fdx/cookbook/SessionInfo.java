package com.fdx.cookbook;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.acra.ACRA;

import java.util.HashMap;
import java.util.UUID;

public class SessionInfo {
    private static SessionInfo ourInstance;
    private User mUser;
    private Context mContext;
    private Boolean mIsConnected;
    private Boolean mReqNewSession;
    private Boolean mIsRecipeRequest;
    private Boolean mNeedUpgrade;
    private String CB_FAMILY="family";
    private String CB_NAME="name";
    private String CB_ID="iduser";
    private String CB_PWD="pwd";
    private String NOT_FOUND="Not found";
    private String mMaskSerialized;
    private String mDevice;
    private String mPwd;
    private static final String TAG = "CB_Session";
    private static String URLPATH="https://cookbookfamily.000webhostapp.com/cb/";
    public static int CONNECT_TIMEOUT = 10000;
    public static int READ_TIMEOUT = 10000;

    public static SessionInfo get(Context context) {
        if (ourInstance==null){
            ourInstance= new SessionInfo(context);
        }
        return ourInstance;
    }

    private SessionInfo(Context context) {
        mContext=context.getApplicationContext();
        mIsConnected=false;
        mReqNewSession=false;
        mIsRecipeRequest=false;
        mNeedUpgrade=false;
        mMaskSerialized="";
        mDevice="Device " + android.os.Build.DEVICE+ " model "
                +android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")";
        String sharedPref;
        sharedPref= PreferenceManager.getDefaultSharedPreferences(context)
                .getString(CB_ID, NOT_FOUND);
        if (sharedPref.equals(NOT_FOUND)){
            mUser = new User(NOT_FOUND, NOT_FOUND);
            mPwd="";
        } else {
            mUser = new User(PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(CB_FAMILY, NOT_FOUND), PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(CB_NAME, NOT_FOUND));
            mUser.setId(UUID.fromString(sharedPref));
            mPwd=PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(CB_PWD, NOT_FOUND);
        }
    }
    private void deBugShow(String s){
        //Log.d(TAG, s);
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
                .putString(CB_PWD, mPwd)
                .apply();
        mUser=user;
    }
    public void clearStoredUser(){
        SharedPreferences settings = mContext.getSharedPreferences("PreferencesName", Context.MODE_PRIVATE);
        settings.edit().remove("CB_FAMILY").commit();
        settings.edit().remove("CB_NAME").commit();
        settings.edit().remove("CB_ID").commit();
    }

    public Boolean IsEmpty(){
        if (mUser.getName().equals(NOT_FOUND)){return true;}
        return false;
    }

    public Boolean IsReqNewSession() {
        return mReqNewSession;
    }

    public void setReqNewSession(Boolean reqNewSession) {
        mReqNewSession = reqNewSession;
    }

    public void setConnection(Boolean b){
        mIsConnected=b;
    }
    public Boolean IsConnected(){
        return mIsConnected;
    }

    public Context getContext(){return mContext;}

    public String getURLPath(){return URLPATH;}
    public int getConnectTimeout(){return CONNECT_TIMEOUT;}
    public int getReadTimeout(){return READ_TIMEOUT;}
    public String getListMask(){return mMaskSerialized;}
    public void setListMask(String s){mMaskSerialized=s;}
    public Boolean IsRecipeRequest(){return mIsRecipeRequest;}
    public void setIsRecipeRequest(Boolean b){mIsRecipeRequest=b;}

    public String getDevice() {
        return mDevice;
    }
    public void setPwd(String s){mPwd=s;}
    public void fillPwd(HashMap<String, String> data, Boolean withuser ){
        if ((mPwd!=null)&&(!mPwd.equals(""))) {
            data.put("pwd", mPwd);
            if (withuser) data.put("iduser", mUser.getId().toString());
        }
    }

    public void setNeedUpgrade(String s) {
        if (s==null) return;
        try {
            Integer newversion=Integer.parseInt(s);
            Integer oldversion=BuildConfig.VERSION_CODE;
            deBugShow("Old :)"+oldversion+" and new :"+newversion);
            if (newversion>oldversion) mNeedUpgrade=true;
        } catch (Exception e ) { deBugShow("Version code of playstore error :"+e);}
    }
    public Boolean appNeedUpgrade(){return mNeedUpgrade;}
}

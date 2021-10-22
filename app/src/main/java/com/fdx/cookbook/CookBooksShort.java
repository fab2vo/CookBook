package com.fdx.cookbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CookBooksShort {
    private static CookBooksShort ourInstance ;
    private Context mContext;
    private List<Recipe> mCookBook;
    private Integer mSelectType;
    private Integer mDLStatus;
    private static final Integer COMPLETED=2;
    private static final Integer STARTED=1;
    private static final Integer INACTIVE=0;
    private static final Integer RECENT=1;
    private static final Integer POPULAR=2;
    private static final Integer BESTNOTE=3;
    private static final String TAG = "CB_Community";

    public static CookBooksShort get(Context context) {
        if (ourInstance==null){
            ourInstance= new CookBooksShort(context);
        }
        return ourInstance;
    }
    private CookBooksShort(Context context) {
        mContext=context.getApplicationContext();
        mCookBook=new ArrayList<>();
        mDLStatus=INACTIVE;
        mSelectType=RECENT;
    }

    public void addRecipe(Recipe r){
        if (r!=null){
            mCookBook.add(r);
        }
    }

    public List<Recipe> getRecipes(){
        return mCookBook;
    }

    public void clear(){
        mCookBook.clear();
    }
    public void clearCompletely (){
        mDLStatus=INACTIVE;
        mSelectType=RECENT;
        mCookBook.clear();
    }

    public void fill(List<Recipe> recipes){
        if (recipes==null) return;
        for(Recipe r:recipes){
            if (r!=null) mCookBook.add(r);
        }
    }

    public void updatePhoto(Recipe rtoupdate){
        for(Recipe r:mCookBook){
            if(r.isTheSame(rtoupdate)) {
                r.setImage(rtoupdate.getImage());
                return;
            }
        }
    }

    public Integer getsize() {
        if (mCookBook == null) return 0;
        if (mCookBook.isEmpty()) return 0;
        return mCookBook.size();
    }

    public Integer getSelectType() {
        return mSelectType;
    }

    public void setSelectType(Integer selectType) {
        mSelectType = selectType;
    }

    public Integer getDLStatus() {
        return mDLStatus;
    }

    public void getDLStarted() {
        mDLStatus = STARTED;
    }
    public void setDLCompleted() {
        mDLStatus = COMPLETED;
    }
    public Boolean isDownloading(){return mDLStatus==STARTED;}
    public Boolean isNew(){return mDLStatus==INACTIVE;}
}

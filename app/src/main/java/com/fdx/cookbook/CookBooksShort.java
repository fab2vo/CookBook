package com.fdx.cookbook;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CookBooksShort {
    private static CookBooksShort ourInstance ;
    //private final Context mContext;
    private final List<Recipe> mCookBook;
    private Integer mSelectType;
    private Integer mSelectScope;
    private Integer mDLStatus;
    private static final Integer COMPLETED=2;
    private static final Integer STARTED=1;
    private static final Integer INACTIVE=0;
    private static final Integer RECENT=1;
    private static final Integer ALL=1;
    private static final String TAG = "CB_CBshort";

    public static CookBooksShort get(Context context) {
        if (ourInstance==null){
            ourInstance= new CookBooksShort(context);
        }
        return ourInstance;
    }
    private CookBooksShort(Context context) {
        //mContext=context.getApplicationContext();
        mCookBook=new ArrayList<>();
        mDLStatus=INACTIVE;
        mSelectType=RECENT;
        mSelectScope=ALL;
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
        mSelectScope=ALL;
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
        if (mCookBook.isEmpty()) return 0;
        return mCookBook.size();
    }

    public Integer getSelectScope() {
        return mSelectScope;
    }

    public void setSelectScope(Integer selectScope) {
        mSelectScope = selectScope;
    }

    public Integer getSelectType() {
        return mSelectType;
    }

    public void setSelectType(Integer selectType) {
        mSelectType = selectType;
    }

    public void getDLStarted() {
        mDLStatus = STARTED;
    }
    public void setDLCompleted() {
        mDLStatus = COMPLETED;
    }
    public Boolean isDownloading(){return Objects.equals(mDLStatus, STARTED);}
    public Boolean isNew(){return Objects.equals(mDLStatus, INACTIVE);}
}

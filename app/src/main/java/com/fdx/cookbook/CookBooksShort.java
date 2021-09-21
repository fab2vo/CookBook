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
        // fill while debugging ****************************
        Recipe r;
        User u;
        UUID uuid;
        //1
        r = new Recipe(UUID.fromString("7897204f-6b94-462f-a938-18c717288102"));
        uuid = UUID.fromString("c81d4e2e-bcf3-11e6-869b-7df92533d2db");
        u = new User(uuid);
        u.setName("Véronique");
        u.setFamily("Devaux_Lion de ML");
        r.setOwner(u);
        r.setTitle("Curry de crevettes");
        mCookBook.add(r);
        //2
        r = new Recipe(UUID.fromString("e0056458-c889-4451-bb56-db6d443469b3"));
        uuid = UUID.fromString("c81d4e2e-bcf2-11e6-869b-7df92533d2db");
        u = new User(uuid);
        u.setName("Fabrice");
        u.setFamily("Devaux_Lion de ML");
        r.setOwner(u);
        r.setTitle("Tomate mozza Genevieve");
        mCookBook.add(r);
        //3
        r = new Recipe(UUID.fromString("74afb02f-fe30-4b45-bf28-7c94b288eb21"));
        uuid = UUID.fromString("fce5a6c7-2dc3-4f4d-989f-69b308b6966a");
        u = new User(uuid);
        u.setName("Leandre");
        u.setFamily("Devaux_Lion de ML");
        r.setOwner(u);
        r.setTitle("Poulet curry coco");
        mCookBook.add(r);
        //4
        r = new Recipe(UUID.fromString("3c2ecac3-c421-43fa-a19b-54990db9221a"));
        uuid = UUID.fromString("fce5a6c7-2dc3-4f4d-989f-69b308b6966a");
        u = new User(uuid);
        u.setName("Leandre");
        u.setFamily("Devaux_Lion de ML");
        r.setOwner(u);
        r.setTitle("Guacamole maison");
        mCookBook.add(r);
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
}

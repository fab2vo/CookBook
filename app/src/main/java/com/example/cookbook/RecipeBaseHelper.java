package com.example.cookbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cookbook.RecipeDbSchema.RecipeTable;

public class RecipeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION=1;
    private static final String DATA_BASE_NAME="recipeBase2.db";  // REINIT BASE
    public RecipeBaseHelper(Context context) {
        super(context, DATA_BASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String s="";
        Recipe r=new Recipe();
        for(int i=0;i<r.getNbStepMax();i++){
            s=s+RecipeTable.Cols.STEP[i];
            if (i<(r.getNbStepMax()-1)){s=s+", ";}
        }
        db.execSQL("create table "+ RecipeTable.NAME+"("+
                " _id integer primary key autoincrement, "+
                RecipeTable.Cols.UUID+", "+
                RecipeTable.Cols.TITLE+", "+
                RecipeTable.Cols.SOURCE+", "+
                RecipeTable.Cols.SOURCE_URL+", "+
                RecipeTable.Cols.DATE+", "+
                RecipeTable.Cols.NOTE+", "+
                RecipeTable.Cols.NBPERS+", "+
                s +
                ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

package com.example.cookbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cookbook.RecipeDbSchema.RecipeTable;

public class RecipeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION=1;
    private static final String DATA_BASE_NAME="recipeBase.db";  // REINIT BASE
    public RecipeBaseHelper(Context context) {
        super(context, DATA_BASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String st="",si="";
        Recipe r=new Recipe();
        for(int i=0;i<r.getNbStepMax();i++){
            st=st+RecipeTable.Cols.STEP[i];
            st=st+", ";
        }
        for(int i=0;i<r.getNbIngMax();i++){
            si=si+RecipeTable.Cols.ING[i];
            si=si+", ";
        }
        db.execSQL("create table "+ RecipeTable.NAME+"("+
                " _id integer primary key autoincrement, "+
                RecipeTable.Cols.UUID+", "+
                RecipeTable.Cols.OWNER+", "+
                RecipeTable.Cols.TITLE+", "+
                RecipeTable.Cols.SOURCE+", "+
                RecipeTable.Cols.SOURCE_URL+", "+
                RecipeTable.Cols.DATE+", "+
                RecipeTable.Cols.DATE_PHOTO+", "+
                RecipeTable.Cols.NBPERS+", "+
                st +
                si +
                RecipeTable.Cols.SEASON+", "+
                RecipeTable.Cols.DIFFICULTY+","+
                RecipeTable.Cols.COMMENTS+", "+
                RecipeTable.Cols.STATUS+", "+
                RecipeTable.Cols.NOTES+", "+
                RecipeTable.Cols.MESSAGE+", "+
                RecipeTable.Cols.MESSAGE_FROM+
                ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

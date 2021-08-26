package com.fdx.cookbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
            st=st+ RecipeDbSchema.RecipeTable.Cols.STEP[i];
            st=st+", ";
        }
        for(int i=0;i<r.getNbIngMax();i++){
            si=si+ RecipeDbSchema.RecipeTable.Cols.ING[i];
            si=si+", ";
        }
        db.execSQL("create table "+ RecipeDbSchema.RecipeTable.NAME+"("+
                " _id integer primary key autoincrement, "+
                RecipeDbSchema.RecipeTable.Cols.UUID+", "+
                RecipeDbSchema.RecipeTable.Cols.OWNER+", "+
                RecipeDbSchema.RecipeTable.Cols.TITLE+", "+
                RecipeDbSchema.RecipeTable.Cols.SOURCE+", "+
                RecipeDbSchema.RecipeTable.Cols.SOURCE_URL+", "+
                RecipeDbSchema.RecipeTable.Cols.DATE+", "+
                RecipeDbSchema.RecipeTable.Cols.DATE_PHOTO+", "+
                RecipeDbSchema.RecipeTable.Cols.NBPERS+", "+
                st +
                si +
                RecipeDbSchema.RecipeTable.Cols.SEASON+", "+
                RecipeDbSchema.RecipeTable.Cols.DIFFICULTY+","+
                RecipeDbSchema.RecipeTable.Cols.TYPE+","+
                RecipeDbSchema.RecipeTable.Cols.COMMENTS+", "+
                RecipeDbSchema.RecipeTable.Cols.STATUS+", "+
                RecipeDbSchema.RecipeTable.Cols.NOTES+", "+
                RecipeDbSchema.RecipeTable.Cols.MESSAGE+", "+
                RecipeDbSchema.RecipeTable.Cols.MESSAGE_FROM+", "+
                RecipeDbSchema.RecipeTable.Cols.TS_RECIPE+", "+
                RecipeDbSchema.RecipeTable.Cols.TS_PHOTO+", "+
                RecipeDbSchema.RecipeTable.Cols.TS_COMMENT+", "+
                RecipeDbSchema.RecipeTable.Cols.TS_NOTE+
                ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

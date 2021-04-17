package com.example.cookbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cookbook.RecipeDbSchema.RecipeTable;

public class RecipeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION=1;
    private static final String DATA_BASE_NAME="recipeBase.db";
    public RecipeBaseHelper(Context context) {
        super(context, DATA_BASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ RecipeTable.NAME+"("+
                " _id integer primary key autoincrement, "+
                RecipeTable.Cols.UUID+", "+
                RecipeTable.Cols.TITLE+", "+
                RecipeTable.Cols.SOURCE+", "+
                RecipeTable.Cols.DATE+", "+
                RecipeTable.Cols.NOTE+", "+
                RecipeTable.Cols.STEP1+", "+
                RecipeTable.Cols.STEP2+", "+
                RecipeTable.Cols.STEP3+", "+
                RecipeTable.Cols.STEP4+
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

package com.example.cookbook;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import static com.example.cookbook.RecipeDbSchema.*;

public class RecipeCursorWrapper extends CursorWrapper {
    public RecipeCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Recipe getRecipe(){
        String uuidString = getString(getColumnIndex(RecipeTable.Cols.UUID));
        String title = getString(getColumnIndex(RecipeTable.Cols.TITLE));
        String source = getString(getColumnIndex(RecipeTable.Cols.SOURCE));
        long date = getLong(getColumnIndex(RecipeTable.Cols.DATE));
        int note = getInt(getColumnIndex(RecipeTable.Cols.NOTE));
        String step1 = getString(getColumnIndex(RecipeTable.Cols.STEP1));
        String step2 = getString(getColumnIndex(RecipeTable.Cols.STEP2));
        String step3 = getString(getColumnIndex(RecipeTable.Cols.STEP3));
        String step4 = getString(getColumnIndex(RecipeTable.Cols.STEP4));
        Recipe r=new Recipe(4,UUID.fromString(uuidString));
        r.setTitle(title);
        r.setSource(source);
        r.setDate(new Date(date));
        r.setNote(note);
        r.setS1(step1);
        r.setS2(step2);
        r.setS3(step3);
        r.setS4(step4);
        return r;
    }
}

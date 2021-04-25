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
        String step5 = getString(getColumnIndex(RecipeTable.Cols.STEP5));
        String step6 = getString(getColumnIndex(RecipeTable.Cols.STEP6));
        String step7 = getString(getColumnIndex(RecipeTable.Cols.STEP7));
        String step8 = getString(getColumnIndex(RecipeTable.Cols.STEP8));
        String step9 = getString(getColumnIndex(RecipeTable.Cols.STEP9));

        Recipe r=new Recipe(UUID.fromString(uuidString));
        r.setTitle(title);
        r.setSource(source);
        r.setDate(new Date(date));
        r.setNoteAvg(note);
        r.setStep(1, step1);
        r.setStep(2, step2);
        r.setStep(3, step3);
        r.setStep(4, step4);
        r.setStep(5, step5);
        r.setStep(6, step6);
        r.setStep(7, step7);
        r.setStep(8, step8);
        r.setStep(9, step9);
        return r;
    }
}

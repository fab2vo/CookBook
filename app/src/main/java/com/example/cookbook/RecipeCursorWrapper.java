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
        int nbpers=getInt(getColumnIndex(RecipeTable.Cols.NBPERS));
        Recipe r=new Recipe(UUID.fromString(uuidString));
        String[] step= new String[r.getNbStepMax()];
        for(int i=0;i<r.getNbStepMax();i++){
            step[i]=getString(getColumnIndex(RecipeTable.Cols.STEP[i]));
            r.setStep(i+1, step[i]);
        }
        r.setTitle(title);
        r.setSource(source);
        r.setDate(new Date(date));
        r.setNoteAvg(note);
        r.setNbPers(nbpers);
        return r;
    }
}

package com.example.cookbook;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import static com.example.cookbook.RecipeDbSchema.*;

public class RecipeCursorWrapper extends CursorWrapper {
    public RecipeCursorWrapper(Cursor cursor){
        super(cursor);
    }
    private static final String TAG = "RecipeCursorWrapper";

    public Recipe getRecipe(){
        String uuidString = getString(getColumnIndex(RecipeTable.Cols.UUID));
        Recipe r=new Recipe(UUID.fromString(uuidString));
        String title = getString(getColumnIndex(RecipeTable.Cols.TITLE));
        r.setTitle(title);
        String source = getString(getColumnIndex(RecipeTable.Cols.SOURCE));
        r.setSource(source);
        String sourceURL=getString(getColumnIndex(RecipeTable.Cols.SOURCE_URL));
        try {
            URL url = new URL(sourceURL);
            r.setSource_url(url);}
            catch (MalformedURLException e) {
                Log.d(TAG, "getURL from cursor failed");
            }
        long date = getLong(getColumnIndex(RecipeTable.Cols.DATE));
        r.setDate(new Date(date));
        int note = getInt(getColumnIndex(RecipeTable.Cols.NOTE));
        r.setNoteAvg(note);
        int nbpers=getInt(getColumnIndex(RecipeTable.Cols.NBPERS));
        r.setNbPers(nbpers);
        String[] step= new String[r.getNbStepMax()];
        for(int i=0;i<r.getNbStepMax();i++){
            step[i]=getString(getColumnIndex(RecipeTable.Cols.STEP[i]));
            r.setStep(i+1, step[i]);
        }
        String season = getString(getColumnIndex(RecipeTable.Cols.SEASON));
        r.setSeason(RecipeSeason.valueOf(season));
        String difficulty = getString(getColumnIndex(RecipeTable.Cols.DIFFICULTY));
        r.setDifficulty(RecipeDifficulty.valueOf(difficulty));

        return r;
    }
}

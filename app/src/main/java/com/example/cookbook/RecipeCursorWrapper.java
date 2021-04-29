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
        String source = getString(getColumnIndex(RecipeTable.Cols.SOURCE));
        String sourceURL=getString(getColumnIndex(RecipeTable.Cols.SOURCE_URL));
        try {
            URL url = new URL(sourceURL);
            r.setSource_url(url);}
            catch (MalformedURLException e) {
                Log.d(TAG, "getURL from cursor failed");
            }
        long date = getLong(getColumnIndex(RecipeTable.Cols.DATE));
        int note = getInt(getColumnIndex(RecipeTable.Cols.NOTE));
        int nbpers=getInt(getColumnIndex(RecipeTable.Cols.NBPERS));
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

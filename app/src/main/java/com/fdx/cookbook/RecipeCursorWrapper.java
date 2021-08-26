package com.fdx.cookbook;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import static com.fdx.cookbook.RecipeDbSchema.*;

public class RecipeCursorWrapper extends CursorWrapper {
    public RecipeCursorWrapper(Cursor cursor){
        super(cursor);
    }
    private static final String TAG = "DebugCursorWrapper";

    public Recipe getRecipe(){
        String uuidString = getString(getColumnIndex(RecipeTable.Cols.UUID));
        Recipe r=new Recipe(UUID.fromString(uuidString));
        String ownerString = getString(getColumnIndex(RecipeTable.Cols.OWNER));
        r.getOwnerDeserialized(ownerString);
        String title = getString(getColumnIndex(RecipeTable.Cols.TITLE));
        r.setTitle(title);
        String source = getString(getColumnIndex(RecipeTable.Cols.SOURCE));
        r.setSource(source);
        String sourceURL=getString(getColumnIndex(RecipeTable.Cols.SOURCE_URL));
        try {
            URL url = new URL(sourceURL);
            r.setSource_url(url);}
            catch (MalformedURLException e) {
                //fdx Log(TAG, "getURL from cursor failed");
            }
        long date = getLong(getColumnIndex(RecipeTable.Cols.DATE));
        r.setDate(new Date(date));
        date = getLong(getColumnIndex(RecipeTable.Cols.DATE_PHOTO));
        r.setDatePhoto(new Date(date));
        int nbpers=getInt(getColumnIndex(RecipeTable.Cols.NBPERS));
        r.setNbPers(nbpers);
        String[] step= new String[r.getNbStepMax()];
        for(int i=0;i<r.getNbStepMax();i++){
            step[i]=getString(getColumnIndex(RecipeTable.Cols.STEP[i]));
            r.setStep(i+1, step[i]);
        }
        String[] ing=new String[r.getNbIngMax()];
        for(int i=0;i<r.getNbIngMax(); i++){
            ing[i]=getString(getColumnIndex(RecipeTable.Cols.ING[i]));
            r.setIngredient(i+1, ing[i]);
        }
        String season = getString(getColumnIndex(RecipeTable.Cols.SEASON));
        r.setSeason(RecipeSeason.valueOf(season));
        String difficulty = getString(getColumnIndex(RecipeTable.Cols.DIFFICULTY));
        r.setDifficulty(RecipeDifficulty.valueOf(difficulty));
        String type = getString(getColumnIndex(RecipeTable.Cols.TYPE));
        r.setType(RecipeType.valueOf(type));
        String comments = getString(getColumnIndex(RecipeTable.Cols.COMMENTS));
        r.getCommentsDeserialised(comments);
        String status = getString(getColumnIndex(RecipeTable.Cols.STATUS));
        r.setStatus(StatusRecipe.valueOf(status));
        String notes = getString(getColumnIndex(RecipeTable.Cols.NOTES));
        r.getNotesDeserialised(notes);
        String message = getString(getColumnIndex(RecipeTable.Cols.MESSAGE));
        r.setMessage(message);
        String fromString = getString(getColumnIndex(RecipeTable.Cols.MESSAGE_FROM));
        r.getFromDeserialized(fromString);
        int tsrecipe = getInt(getColumnIndex(RecipeTable.Cols.TS_RECIPE));
        r.updateTS(AsynCallFlag.NEWRECIPE, (tsrecipe==1));
        int tsphoto = getInt(getColumnIndex(RecipeTable.Cols.TS_PHOTO));
        r.updateTS(AsynCallFlag.NEWPHOTO, (tsphoto==1));
        int tscomment = getInt(getColumnIndex(RecipeTable.Cols.TS_COMMENT));
        r.updateTS(AsynCallFlag.NEWCOMMENT, (tscomment==1));
        int tsnote = getInt(getColumnIndex(RecipeTable.Cols.TS_NOTE));
        r.updateTS(AsynCallFlag.NEWRATING, (tsnote==1));
        return r;
    }
}

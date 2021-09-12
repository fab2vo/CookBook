package com.fdx.cookbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CookBook {
    private static CookBook ourInstance ;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private static final String TAG = "CB_Cookbook";

    public static CookBook get(Context context) {
        if (ourInstance==null){
            ourInstance= new CookBook(context);
        }
        return ourInstance;
    }

    private CookBook(Context context) {
          mContext=context.getApplicationContext();
          mDatabase=new RecipeBaseHelper(mContext)
                  .getWritableDatabase();
    }

    public Recipe getRecipe(UUID id) {
        RecipeCursorWrapper cursor=queryRecipes(
                RecipeDbSchema.RecipeTable.Cols.UUID+" =?",
                new String[] {id.toString()}
        );
        try{
            if (cursor.getCount()==0) {return null;}
            cursor.moveToFirst();
            return cursor.getRecipe();
        } finally {cursor.close();}
    }

    public void updateRecipe(Recipe r) {
        String uuidString=r.getId().toString();
        //r.setDate(new Date());
        ContentValues values=getContentValues(r);
        mDatabase.update(RecipeDbSchema.RecipeTable.NAME, values,
                RecipeDbSchema.RecipeTable.Cols.UUID+" =?",
                new String[] {uuidString});
    }

    private RecipeCursorWrapper queryRecipes(String whereClause, String[] whereArgs){
        Cursor cursor=mDatabase.query(
                RecipeDbSchema.RecipeTable.NAME,
                null, // selct all columns
                whereClause,
                whereArgs,
                null, null, null
        );
        return new RecipeCursorWrapper(cursor);
    }

    public void addRecipe(Recipe r){
        if (this.getRecipe(r.getId())==null) {
            ContentValues values=getContentValues(r);
            mDatabase.insert(RecipeDbSchema.RecipeTable.NAME, null, values);
        }
    }

    public void removeRecipe(Recipe r){
        String uuidString=r.getId().toString();
        mDatabase.delete(RecipeDbSchema.RecipeTable.NAME,
                RecipeDbSchema.RecipeTable.Cols.UUID+" =?",
                new String[] {uuidString});
        return;
    }

    public void markRecipeToDelete(Recipe r){
        r.setStatus(StatusRecipe.Deleted);
        updateRecipe(r);
        return;
    }

    public List<Recipe> getRecipes(){
          List<Recipe> recipes=new ArrayList<>();
          RecipeCursorWrapper cursor=queryRecipes(null, null);
          try{
              cursor.moveToFirst();
              while (!cursor.isAfterLast()){
                  recipes.add(cursor.getRecipe());
                  cursor.moveToNext();
              }
          } finally { cursor.close();}
          return recipes;
    }

    public List<Recipe> getRecipesVisibles(){
        List<Recipe> recipes=this.getRecipes();
        for(Recipe r:recipes){
            if (!r.IsVisible()) recipes.remove(r);
        }
        return recipes;
    }

    private static ContentValues getContentValues(Recipe recipe) {
        ContentValues values=new ContentValues();
        values.put(RecipeDbSchema.RecipeTable.Cols.UUID, recipe.getId().toString());
        values.put(RecipeDbSchema.RecipeTable.Cols.OWNER, recipe.getSerializedOwner());
        values.put(RecipeDbSchema.RecipeTable.Cols.TITLE, recipe.getTitle());
        values.put(RecipeDbSchema.RecipeTable.Cols.SOURCE, recipe.getSource());
        values.put(RecipeDbSchema.RecipeTable.Cols.SOURCE_URL, recipe.getSource_url().toString());
        values.put(RecipeDbSchema.RecipeTable.Cols.DATE, recipe.getDate().getTime());
        if (recipe.getDatePhoto()!=null){
        values.put(RecipeDbSchema.RecipeTable.Cols.DATE_PHOTO, recipe.getDatePhoto().getTime());}
        values.put(RecipeDbSchema.RecipeTable.Cols.NBPERS, recipe.getNbPers());
        for(int i=0;i<recipe.getNbStepMax();i++){
            values.put(RecipeDbSchema.RecipeTable.Cols.STEP[i], recipe.getStep(i+1));
        }
        for(int i=0;i<recipe.getNbIngMax();i++){
            values.put(RecipeDbSchema.RecipeTable.Cols.ING[i], recipe.getIngredient(i+1));
        }
        values.put(RecipeDbSchema.RecipeTable.Cols.SEASON, recipe.getSeason().name());
        values.put(RecipeDbSchema.RecipeTable.Cols.DIFFICULTY, recipe.getDifficulty().name());
        values.put(RecipeDbSchema.RecipeTable.Cols.TYPE, recipe.getType().name());
        values.put(RecipeDbSchema.RecipeTable.Cols.COMMENTS, recipe.getSerializedComments());
        values.put(RecipeDbSchema.RecipeTable.Cols.STATUS, recipe.getStatus().toString());
        values.put(RecipeDbSchema.RecipeTable.Cols.NOTES, recipe.getSerializedNotes());
        values.put(RecipeDbSchema.RecipeTable.Cols.MESSAGE, recipe.getMessage());
        values.put(RecipeDbSchema.RecipeTable.Cols.MESSAGE_FROM, recipe.getSerializedFrom());
        values.put(RecipeDbSchema.RecipeTable.Cols.TS_RECIPE, recipe.getTS(AsynCallFlag.NEWRECIPE));
        values.put(RecipeDbSchema.RecipeTable.Cols.TS_PHOTO, recipe.getTS(AsynCallFlag.NEWPHOTO));
        values.put(RecipeDbSchema.RecipeTable.Cols.TS_COMMENT, recipe.getTS(AsynCallFlag.NEWCOMMENT));
        values.put(RecipeDbSchema.RecipeTable.Cols.TS_NOTE, recipe.getTS(AsynCallFlag.NEWRATING));
        return values;
    }

    public File getPhotoFile(Recipe r){
        if (r==null) {return null;}
        File filesDir= mContext.getFilesDir();
        return new File(filesDir, r.getPhotoFilename());
    }

    public Boolean deleteImage(Recipe r){
        Boolean success=false;
        File filesDir= mContext.getFilesDir();
        File im=new File(filesDir, r.getPhotoFilename());
        if (im.exists()){
            im.delete();
            success=true;
        }
        return success;
    }

    public void clearCookBook(){
        List<Recipe> recipes=new ArrayList<>();
        recipes=getRecipes();
        for (Recipe r:recipes){
            deleteImage(r);
            removeRecipe(r);
        }
    }

    public void fillCookBook(List<Recipe> recipes){
        for(Recipe r:recipes){
            addRecipe(r);
        }
    }

    public Boolean isThereMail(){
        List<Recipe> recipes=this.getRecipes();
        for(Recipe r:recipes){
            if (r.IsMessage()) return true;
        }
        return false;
    }
}

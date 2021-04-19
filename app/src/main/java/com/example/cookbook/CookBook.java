package com.example.cookbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CookBook {
    private static CookBook ourInstance ;
//    private List<Recipe> mRecipes;
    private Context mContext;
    private SQLiteDatabase mDatabase;

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
   //       for (int i = 0; i < 5; i++){addRecipe(randomRecipe());}
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
 //       mRecipes.add(r);
        ContentValues values=getContentValues(r);
        mDatabase.insert(RecipeDbSchema.RecipeTable.NAME, null, values);
    }

    public void removeRecipe(Recipe r){
        String uuidString=r.getId().toString();
        mDatabase.delete(RecipeDbSchema.RecipeTable.NAME,
                RecipeDbSchema.RecipeTable.Cols.UUID+" =?",
                new String[] {uuidString});
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

    private static ContentValues getContentValues(Recipe recipe) {
        ContentValues values=new ContentValues();
        values.put(RecipeDbSchema.RecipeTable.Cols.UUID, recipe.getId().toString());
        values.put(RecipeDbSchema.RecipeTable.Cols.TITLE, recipe.getTitle());
        values.put(RecipeDbSchema.RecipeTable.Cols.SOURCE, recipe.getSource());
        values.put(RecipeDbSchema.RecipeTable.Cols.DATE, recipe.getDate().getTime());
        values.put(RecipeDbSchema.RecipeTable.Cols.NOTE, recipe.getNote()); //Pb ?
        values.put(RecipeDbSchema.RecipeTable.Cols.STEP1, recipe.getS1());
        values.put(RecipeDbSchema.RecipeTable.Cols.STEP2, recipe.getS2());
        values.put(RecipeDbSchema.RecipeTable.Cols.STEP3, recipe.getS3());
        values.put(RecipeDbSchema.RecipeTable.Cols.STEP4, recipe.getS4());
        return values;
    }

    public File getPhotoFile(Recipe r){
        File filesDir= mContext.getFilesDir();
        return new File(filesDir, r.getPhotoFilename());
    }

    // methode pour generer des recettes aléatoires pour la phase de mise au point
    private Recipe randomRecipe(){
        Recipe r;
        Random rand=new Random();
        String s1;
        String[] mIngPrinc={"Poulet","Boeuf","Canard","Thon", "Homard"};
        String[] mFacon={"a la moutarde","a la marocaine"," laque", "a la mexicaine", "a l indienne", "braise", "saute"};
        String[] mSource={"Marmiton","Cyril Lignac", "Fabrice", "Veronique", "Cuisine actuelle"};
        String[] mS1={"Coupez","Tranchez", "Emincez","Coupez en lamelles","Coupez en cubes"};
        String[] mS2={"les tomates","les oignons", "les poivrons","les pommes de terre","les aubergines"};
        String[] mS3={"le poivre", "le curry", "la sauce soja", "le cumin", "le piment"};
        String[] mS4={"Mettre au four", "Faites revenir","Faites bouillir","Saisir","Faites mijoter"};
        r=new Recipe(4);
        s1=mIngPrinc[rand.nextInt(5)];
        r.setTitle(s1+" "+mFacon[rand.nextInt(7)]);
        r.setSource(mSource[rand.nextInt(5)]);
        r.setNote(rand.nextInt(6));
        r.setS1(mS1[rand.nextInt(5)]+" le "+s1+".");
        r.setS2("Apres quelques minutes, ajoutez au "+s1+
                " "+mS2[rand.nextInt(5)]+
                " et "+mS2[rand.nextInt(5)]);
        r.setS3("Salez, poivrez et ajoutez "+mS3[rand.nextInt(5)]);
        r.setS4(mS4[rand.nextInt(5)]+ " pendant environ "+ (5+ rand.nextInt(10))+ " minutes.");
        r.setDate(new Date());
        return r;
    }

}

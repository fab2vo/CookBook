package com.example.cookbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
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
    private String TAG="CookBook";

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
          // For initialisation of recipedB database : adding random recipes
          //for (int i = 0; i < 12; i++){addRecipe(randomRecipe());}
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
        values.put(RecipeDbSchema.RecipeTable.Cols.OWNER, recipe.getOwner().getId().toString());
        values.put(RecipeDbSchema.RecipeTable.Cols.TITLE, recipe.getTitle());
        values.put(RecipeDbSchema.RecipeTable.Cols.SOURCE, recipe.getSource());
        values.put(RecipeDbSchema.RecipeTable.Cols.SOURCE_URL, recipe.getSource_url().toString());
        values.put(RecipeDbSchema.RecipeTable.Cols.DATE, recipe.getDate().getTime());
        values.put(RecipeDbSchema.RecipeTable.Cols.NOTE, recipe.getNoteAvg()); //Pb ?
        values.put(RecipeDbSchema.RecipeTable.Cols.NBPERS, recipe.getNbPers());
        for(int i=0;i<recipe.getNbStepMax();i++){
            values.put(RecipeDbSchema.RecipeTable.Cols.STEP[i], recipe.getStep(i+1));
        }
        for(int i=0;i<recipe.getNbIngMax();i++){
            values.put(RecipeDbSchema.RecipeTable.Cols.ING[i], recipe.getIngredient(i+1));
        }
        values.put(RecipeDbSchema.RecipeTable.Cols.SEASON, recipe.getSeason().name());
        values.put(RecipeDbSchema.RecipeTable.Cols.DIFFICULTY, recipe.getDifficulty().name());
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

    // methode pour generer des recettes aléatoires pour la phase de mise au point
    private Recipe randomRecipe(){
        Recipe r;
        Random rand=new Random();
        // initialisations users
        User fab=new User("Devaux_Lion de ML","Fabrice");
        fab.setId(UUID.fromString("c81d4e2e-bcf2-11e6-869b-7df92533d2db"));
        User lucile=new User("Devaux_Lion de ML","Lucile");
        lucile.setId(UUID.fromString("c81d4e2e-bcf2-11e7-869b-7df92533d2db"));
        User vero=new User("Devaux_Lion de ML","Véronique");
        vero.setId(UUID.fromString("c81d4e2e-bcf3-11e6-869b-7df92533d2db"));
        // Init pour alea
        User[] mUserName={fab, lucile, vero};
        String[] mIngPrinc={"Poulet","Boeuf","Canard","Thon", "Homard"};
        String[] mFacon={"a la moutarde","a la marocaine"," laque", "a la mexicaine", "a l indienne", "braise", "saute"};
        String[] mSource={"Marmiton","Cyril Lignac", "Fabrice", "Veronique", "Cuisine actuelle"};
        String[] mS1={"Coupez","Tranchez", "Emincez","Coupez en lamelles","Coupez en cubes"};
        String[] mS2={"les tomates","les oignons", "les poivrons","les pommes de terre","les aubergines"};
        String[] mS3={"le poivre", "le curry", "la sauce soja", "le cumin", "le piment"};
        String[] mS4={"Mettre au four",
                "Faites revenir","Faites bouillir","Saisir","Faites mijoter"};
        String[] mURL={"http://www.marmiton.org","http://www.cuisinechef.com","http://www.lacuisinedefabrice.fr"};
        RecipeSeason[] mSeason={RecipeSeason.WINTER,RecipeSeason.SUMMER,RecipeSeason.ALLYEAR};
        RecipeDifficulty[] mDifficulty={RecipeDifficulty.EASY, RecipeDifficulty.QUICK, RecipeDifficulty.ELABORATE, RecipeDifficulty.SOPHISTICATED };
        String[] iNum={"5","20","3","100","2"};
        String[] iUnit={"g","dl","cuillerées à café", "litres", "pincées"};
        String[] iIngt={"poivre moulu","ail","lait","porc", "sucre","boeuf","pomme de terre","pruneaux"};
        //setting
        r=new Recipe();
        r.setOwner(mUserName[rand.nextInt(3)]);
        String s1=mIngPrinc[rand.nextInt(5)];
        r.setTitle(s1+" "+mFacon[rand.nextInt(7)]);
        r.setSource(mSource[rand.nextInt(5)]);
        String urls=mURL[rand.nextInt(3)];
        try {
            URL url= new URL(urls);
            r.setSource_url(url);
        } catch (MalformedURLException e){
            Log.d(TAG, "random recipe URL >" +urls+"< Failed");}
        r.setNoteAvg(rand.nextInt(6));
        r.setSeason(mSeason[rand.nextInt(3)]);
        r.setDifficulty(mDifficulty[rand.nextInt(4)]);
        r.setNbPers(3+rand.nextInt(3));
        r.setStep(1,mS1[rand.nextInt(5)]+" le "+s1+".");
        r.setStep(2,"Apres quelques minutes, ajoutez au "+s1+
                " "+mS2[rand.nextInt(5)]+
                " et "+mS2[rand.nextInt(5)]);
        r.setStep(3,"Salez, poivrez et ajoutez "+mS3[rand.nextInt(5)]);
        r.setStep(4,mS4[rand.nextInt(5)]+ " pendant environ "+ (5+ rand.nextInt(10))+ " minutes.");
        r.setStep(5,"");
        r.setStep(6,"");
        r.setStep(7,"");
        r.setStep(8,"");
        r.setStep(9,"");
        r.setDate(new Date());
        int ix=rand.nextInt(7)+3;
        for(int i=0; i<ix;i++){
            r.setIngredient(i+1,iNum[rand.nextInt(5)]+" "+
                    iUnit[rand.nextInt(5)]+" de "+iIngt[rand.nextInt(8)]);
        }
        return r;
    }

}

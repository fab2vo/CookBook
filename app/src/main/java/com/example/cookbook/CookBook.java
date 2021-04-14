package com.example.cookbook;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CookBook {
    private static CookBook ourInstance ;
    private List<Recipe> mRecipes;

    public static CookBook get(Context context) {
        if (ourInstance==null){
            ourInstance= new CookBook(context);
        }
        return ourInstance;
    }

    private CookBook(Context context) {
        mRecipes=new ArrayList<>();
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
        for (int i = 0; i < 30; i++) {
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
            mRecipes.add(r);
        }
    }

    public Recipe getRecipe(UUID id) {
        for (Recipe recipe:mRecipes) {
            if (recipe.getId().equals(id)) return recipe;
        }
        return null;
    }

    public List<Recipe> getRecipes(){
        return mRecipes;
    }
}

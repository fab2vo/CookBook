package com.example.cookbook;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

public class Recipe {
    private UUID mId;           //RecipdedB
    private User mOwner;
    private Date mDate;         //RecipdedB
    private Date mDate_crea;
    private Date mDate_modif;
    private String mTitle;      //RecipdedB
    private String mSource;     //RecipdedB
    private URL mSource_url;    //RecipdedB
    private int mNbPers;        //RecipdedB
    private String[] mSteps;    //RecipdedB

    private int mNoteAvg;       //RecipdedB
    //private ArrayList<Note> mNotes;
    //private ArrayList<Ingredient> mIngredients;
    //private ArrayList<Comment> mComments
    private RecipeSeason mSeason;
    // type
    private static final int NBSTEP_MAX=9;
    private String TAG="DebugRecipe";
    private String DEFAULT_URL="https://wwww.familycookbook.com";



    public Recipe() {
        this(UUID.randomUUID());
    }

    public Recipe( UUID id){
        mId=id;
        mDate=new Date();
        mSteps = new String[NBSTEP_MAX];
        for(int i=0;i<NBSTEP_MAX;i++){mSteps[i]="";}
        mNbPers=4;
        try {mSource_url=new URL(DEFAULT_URL);
        } catch (MalformedURLException e) {}
    }

    public UUID getId() {
        return mId;
    }

    public Date getDate() {
        return mDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSource() {
        return mSource;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setSource(String source) {
        mSource = source;
    }

    public void setSteps(String[] steps) {
        mSteps = steps;
    }

    public String[] getSteps() {
        return mSteps;
    }

    public int getNoteAvg() {
        return mNoteAvg;
    }

    public void setNoteAvg(int noteAvg) {
        mNoteAvg = noteAvg;
    }

    public void setStep(Integer i, String step) {
        if ((i > 0) && (i <= NBSTEP_MAX)) {
            mSteps[i - 1] = step;
        }
    }

    public URL getSource_url() {
        return mSource_url;
    }

    public void setSource_url(URL source_url) {
        mSource_url = source_url;
    }

    public RecipeSeason getSeason() {
        return mSeason;
    }

    public void setSeason(RecipeSeason season) {
        mSeason = season;
    }

    public String getStep(Integer i){
        if ((i>0)&&(i<=NBSTEP_MAX)) {return mSteps[i-1];}
        else{ return "";}
    }

    public int getNbStep(){
        int j=0;
        for(int i=NBSTEP_MAX; i>0; i--){
            if (!mSteps[i-1].isEmpty()) {j=i; break;}}
        return j;
    }

    public User getOwner() {
        return mOwner;
    }

    public void setOwner(User owner) {
        mOwner = owner;
    }

    public int getNbPers() {
        return mNbPers;
    }

    public void setNbPers(int nbPers) {
        mNbPers = nbPers;
    }

    public int getNbStepMax(){return NBSTEP_MAX;}

    public String getPhotoFilename(){
        return "IMG"+getId().toString()+".jpg";
    }
}

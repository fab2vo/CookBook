package com.example.cookbook;

import android.util.Log;

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
    private String mSource_http;
    private int mNbPers;        //RecipdedB
    private String[] mSteps;    //RecipdedB
    private static final int NBSTEP_MAX=9;
    private int mNoteAvg;       //RecipdedB
    //private ArrayList<Note> mNotes;
    //private ArrayList<Ingredient> mIngredients;
    //private ArrayList<Comment> mComments
    // season
    // type
    private String TAG="DebugRecipe";



    public Recipe() {
        this(UUID.randomUUID());
    }

    public Recipe( UUID id){
        mId=id;
        mDate=new Date();
        mSteps = new String[NBSTEP_MAX];
        for(int i=0;i<NBSTEP_MAX;i++){mSteps[i]="";}
        mNbPers=4;
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

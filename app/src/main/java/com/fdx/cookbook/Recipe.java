package com.fdx.cookbook;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


enum StatusRecipe {Submitted,Visible, Deleted }
public class Recipe {
    private UUID mId;
    private User mOwner;
    private Date mLastUpdateRecipe;
    private Date mLastUpdatePhoto;
    private String mTitle;
    private String mSource;
    private URL mSource_url;
    private int mNbPers;
    private String[] mSteps;
    private double mNoteAvg;
    private ArrayList<Note> mNotes;
    private ArrayList<Comment> mComments;
    private RecipeSeason mSeason;
    private RecipeType mType;
    private RecipeDifficulty mDifficulty;
    private String[] mIngredients;
    private StatusRecipe mStatus;
    private String mMessage;
    private User mIdFrom;
    private Boolean mTS_recipe;
    private Boolean mTS_photo;
    private Boolean mTS_comment;
    private Boolean mTS_note;
    private Bitmap mImage;

    private static final int NBSTEP_MAX=9;
    private static final int NBING_MAX=15;
    private static final int NBCOM_MAX=20;
    private String TAG="CB_Recipe";
    private String DEFAULT_URL="https://wwww.cookbookfamily.com";
    private String UUIDNULL="00000000-0000-0000-0000-000000000000";



    public Recipe() {
        this(UUID.randomUUID());
    }

    public Recipe( UUID id){
        mId=id;
        mTitle="";
        mLastUpdateRecipe =new Date();
        Calendar c=Calendar.getInstance();
        c.set(2000,0,1,0,0, 0);
        mLastUpdateRecipe=c.getTime();
        mLastUpdatePhoto=c.getTime();
        mSteps = new String[NBSTEP_MAX];
        mIngredients= new String[NBING_MAX];
        for(int i=0;i<NBSTEP_MAX;i++){mSteps[i]="";}
        for(int i=0;i<NBING_MAX;i++){mIngredients[i]="";}
        mNbPers=4;
        mSeason=RecipeSeason.ALLYEAR;
        mType=RecipeType.MAIN;
        mDifficulty=RecipeDifficulty.UNDEFINED;
        mStatus=StatusRecipe.Visible;
        mComments=new ArrayList<Comment>();
        mNotes=new ArrayList<Note>();
        mSource="";
        mIdFrom=new User(UUID.fromString( UUIDNULL));
        mOwner=new User(UUID.fromString( UUIDNULL));
        try {mSource_url=new URL(DEFAULT_URL);
        } catch (MalformedURLException e) {}
        mTS_recipe=false;
        mTS_photo=false;
        mTS_comment=false;
        mTS_note=false;
    }

    public Date getDate() {
        return mLastUpdateRecipe;
    }

    public Date getDatePhoto() {
        return mLastUpdatePhoto;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setDate(Date date) {mLastUpdateRecipe = date;}

    public void setDatePhoto(Date date) {mLastUpdatePhoto = date;}

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getNbPers() {
        return mNbPers;
    }

    public void setNbPers(int nbPers) {
        mNbPers = nbPers;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage=message;
    }


    // -----------------STEP-------------------
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

    public int getNbStepMax(){return NBSTEP_MAX;}

    //--------------Ingredients--------------------
    public void setIngredient(Integer i, String ing) {
        if ((i > 0) && (i <= NBING_MAX)) {
            mIngredients[i-1] = ing;
        }
    }

    public String getIngredient(Integer i){
        if ((i>0)&&(i<=NBING_MAX)) {return mIngredients[i-1];}
        else{ return "";}
    }
    public int getNbIng(){
        int j=0;
        for(int i=NBING_MAX; i>0; i--){
            if (!mIngredients[i-1].isEmpty()) {j=i; break;}}
        return j;
    }

    public int getNbIngMax(){return NBING_MAX;}

    //------------------- Users  ----------------------------------
    public UUID getId() {
        return mId;
    }

    public User getUserFrom() {
        return mIdFrom;
    }

    public void setUserFrom(User idFrom) {
        mIdFrom = idFrom;
    }

    public void setId(UUID id) {mId = id; }

    public User getOwner() {
        return mOwner;
    }

    public String getOwnerIdString() {
        if (mOwner==null) return UUIDNULL;
        if (mOwner.getId()==null) return UUIDNULL;
        return mOwner.getId().toString();
    }

    public void setOwner(User owner) {
        mOwner = owner;
    }

    //------------------- Source ----------------------------------
    public String getSource() {
        if (mSource==null) return "";
        return mSource;
    }
    public void setSource(String source) {
        mSource = source;
    }
    public URL getSource_url() {
        return mSource_url;
    }

    public String getSource_url_name() {
        String ret=mSource_url.toString();
        if (ret.equals("https:") || ret.equals("http:")){ret="";}
        return ret;
    }

    public void setSource_url(URL source_url) {
        mSource_url = source_url;
    }

    // -----------------------Enum ------------------------

    public RecipeSeason getSeason() {
        return mSeason;
    }

    public void setSeason(RecipeSeason season) {
        mSeason = season;
    }

    public boolean IsSummer(){
        switch (mSeason) {
            case SUMMER:
                return true;
            case ALLYEAR:
                return true;
            default:
                return false;
        }
    }
    public boolean IsWinter(){
        switch (mSeason) {
            case WINTER:
                return true;
            case ALLYEAR:
                return true;
            default:
                return false;
        }
    }
    public RecipeType getType() {
        return mType;
    }
    public void setType(RecipeType type) {
        mType = type;
    }

    public RecipeDifficulty getDifficulty() {
        return mDifficulty;
    }

    public void setDifficulty(RecipeDifficulty difficulty) {
        mDifficulty = difficulty;
    }

    public StatusRecipe getStatus() {return mStatus;}
    public void setStatus(StatusRecipe s) {
        mStatus = s;
    }
    public boolean IsVisible(){
        if (mStatus==StatusRecipe.Visible) {return true;}
        return false;
    }
    public boolean IsMessage(){
        if (mStatus==StatusRecipe.Submitted) {return true;}
        return false;
    }
    public boolean IsMarkedDeleted(){
        if (mStatus==StatusRecipe.Deleted) {return true;}
        return false;
    }

    public void updateTS(AsynCallFlag asyn, Boolean b){
        switch(asyn){
            case NEWRECIPE:{mTS_recipe=b;return;}
            case NEWPHOTO:{mTS_photo=b;return;}
            case NEWRATING:{mTS_note=b;return;}
            case NEWCOMMENT:{mTS_comment=b;return;}
        }
        return;
    }

    public int getTS(AsynCallFlag asyn){
        Boolean b=false;
        switch(asyn){
            case NEWRECIPE:{b=mTS_recipe; break;}
            case NEWPHOTO:{b=mTS_photo; break;}
            case NEWRATING:{b=mTS_note; break;}
            case NEWCOMMENT:{b=mTS_comment; break;}
        }
        return (b ? 1:0);
    }

    //-------------------- photo filename---------------------
    public String getPhotoFilename(){
        return "IMG"+getId().toString()+".jpg";
    }

    //--------------------- Arraylist Comments et Notes------------------------
    public void addComment(Comment c){ mComments.add(c);}

    public ArrayList<Comment> getComments() {return mComments;}

    public Comment getComment(int i){
        if (i<mComments.size()){
            return mComments.get(i);
        } else {return null;}

    }

    public int getNbComMax(){return NBCOM_MAX;} // nb max affiché

    public void addNote(Note note){ mNotes.add(note);}

    public ArrayList<Note> getNotes() {return mNotes;}

    public Note getNote(int i){
        if (mNotes==null) return null;
        if (i<mNotes.size()){ return mNotes.get(i);}
        else {return null;}
    }

    public double getNoteAvg() {
        mNoteAvg=0;
        if (mNotes==null) return mNoteAvg;
        if (mNotes.size()!=0) {
            for(Note n:mNotes){mNoteAvg+=n.getNote();}
            mNoteAvg=mNoteAvg/mNotes.size();
        } else {mNoteAvg=0;}
        return mNoteAvg;
    }
    public int getNbNotes() {
        if (mNotes==null) return 0;
        if (mNotes.isEmpty()) return 0;
        return mNotes.size();
    }

    //--------------------- Serialisation ------------------------
    public String getSerializedComments(){
        Gson gson = new Gson();
        return gson.toJson(mComments);
    }

    public void getCommentsDeserialised(String raw){
        Gson gson=new Gson();
        Type listOfNotesObject = new TypeToken<ArrayList<Comment>>() {}.getType();
        mComments=gson.fromJson(raw, listOfNotesObject);
    }
    public String getSerializedOwner(){
        Gson gson = new Gson();
        return gson.toJson(mOwner);
    }
    public String getSerializedFrom(){
        Gson gson = new Gson();
        return gson.toJson(mIdFrom);
    }
    public void getOwnerDeserialized(String raw){
        Gson gson=new Gson();
        mOwner=gson.fromJson(raw, User.class);
    }
    public void getFromDeserialized(String raw){
        Gson gson=new Gson();
        mIdFrom=gson.fromJson(raw, User.class);
    }

    public String getSerializedNotes(){
        Gson gson = new Gson();
        return gson.toJson(mNotes);
    }

    public void getNotesDeserialised(String raw){
        Gson gson=new Gson();
        Type listOfNotesObject = new TypeToken<ArrayList<Note>>() {}.getType();
        mNotes=gson.fromJson(raw, listOfNotesObject);
    }

    // ******************* tests **********************
    public Boolean hasNotChangedSince(Recipe r){
        if (!mId.toString().equals(r.getId().toString())) return false;
        if (!mOwner.getId().toString().equals(r.getOwner().getId().toString())) return false;
        if (!mTitle.equals(r.getTitle())) return false;
        if (!mSource.equals(r.getSource())) return false;
        if (!mSource_url.equals(r.getSource_url())) return false;
        if (mNbPers!=r.getNbPers()) return false;
        for (int i = 0; i < r.getNbStepMax(); i++) {
            if (!mSteps[i].equals(r.getStep(i+1))) return false;
        }
        for (int i = 0; i < r.getNbIngMax(); i++) {
            if (!mIngredients[i].equals(r.getIngredient(i + 1))) return false;
        }
        if (!mSeason.toString().equals(r.getSeason().toString())) return false;
        if (!mType.toString().equals(r.getType().toString())) return false;
        if (!mDifficulty.toString().equals(r.getDifficulty().toString())) return false;
        return true;
    }

    public boolean containQuery(String s){ //for search request
        if (sc(mTitle, s)) return true;
        if (sc(mOwner.getNameComplete(), s)) return true;
        for(String sloop:mIngredients){
            if (sloop==null) break;
            if (sc(sloop, s)) return true;
        }
        for(String sloop:mSteps){
            if (sloop==null) break;
            if (sc(sloop, s)) return true;
        }
        for(Comment c:mComments){
            if (c==null) break;
            if (sc(c.getTxt(),s)) return true;
        }
        return false;
    }

    private boolean sc(String s1, String s2){
        if (s1==null) return false;
        if (s2==null) return true;
        return s1.toLowerCase().contains(s2.toLowerCase());
    }

    public boolean isTheSame(Recipe rtotest){
        return (rtotest.getId().toString().equals(mId.toString()));
    }
    // -----------------------Image ------------------------

    public Bitmap getImage() {
        return mImage;
    }

    public void setImage(Bitmap image) {
        mImage = image;
    }
}

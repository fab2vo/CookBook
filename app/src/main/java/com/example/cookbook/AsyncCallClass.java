package com.example.cookbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

enum AsynCallFlag { NEWRECIPE, NEWPHOTO, NEWCOMMENT, NEWRATING, GLOBALSYNC, DELETERECIPE}

class AsyncCallClass extends AsyncTask<Void, Integer, Boolean> {
    // Constantes
    private static final String TAG = "CB_AsynCalls";
    private static final String PHP204 = "return204.php";
    private static final String PHPUPDATECREATE = "updateorcreaterecipe.php";
    private static final String PHPUPUPLOADPHOTO = "uploadphotointorecipe.php";
    private static final String PHPDELETERECIPE = "deleterecipe.php";
    private static final String PHPGETCOMMENTSOFRECIPE = "getcommentsofrecipe.php";
    private static final String PHPADDCOMMENTTORECIPE = "addcommentwithdate.php";
    private static final String PHPADDNOTETORECIPE = "addnotewithdate.php";
    private static final String PHPGETNOTESOFRECIPE = "getnotesofrecipe.php";
    private static final String PHPGETSTAMPSTIERS = "getrecipestampstiers.php";
    private static final String PHPGETRECIPEFROMCB = "getrecipefromcb.php";
    private static final String PHPGETRECIPEFROMCBWITHPHOTO = "getrecipefromcbwithphoto.php";
    private static final String MYSQLDATEFORMAT="yyyy-MM-dd HH:mm:ss";
    // Variable
    private static Context mContext;
    private SessionInfo mSession;
    private NetworkUtils mNetUtils;
    private CookBook mCookbook;

    public AsyncCallClass(Context context){
        mContext=context;
        mCookbook = CookBook.get(mContext);
        mSession = SessionInfo.get(mContext);
        mNetUtils = new NetworkUtils(mContext);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Boolean isChanged=false;
        if (!test204()) {
            return false;
        }
        Log.d(TAG, "*************** SYNC ****************************");
        if (syncTiersRecipe(mSession.getUser())) isChanged=true;
        List<Recipe> mrecipes=mCookbook.getRecipes();
        for(Recipe r:mrecipes){
            if (r.getOwnerIdString().equals(mSession.getUser().getId().toString())) {
                if (r.getTS(AsynCallFlag.NEWRECIPE) == 1) {
                    if (uploadRecipe(r)) {
                        Log.d(TAG, "Recette " + r.getTitle() + " mise à jour");
                        r.updateTS(AsynCallFlag.NEWRECIPE, false);
                        mCookbook.updateRecipe(r);
                        isChanged=true;
                    } else {
                        Log.d(TAG, "Recette " + r.getTitle() + " non mise à jour");
                    }
                }
                if (r.getTS(AsynCallFlag.NEWPHOTO) == 1) {
                    if (uploadPhoto(r)) {
                        Log.d(TAG, "Recette Photo " + r.getTitle() + " mise à jour");
                        r.updateTS(AsynCallFlag.NEWPHOTO, false);
                        mCookbook.updateRecipe(r);
                        isChanged=true;
                    } else {
                        Log.d(TAG, "Recette Photo " + r.getTitle() + " non mise à jour");
                    }
                }
            }
            if (syncCommentsRecipe(r)) {
                Log.d(TAG, "Comments de " + r.getTitle()+" updaté");
                isChanged=true;
                r.updateTS(AsynCallFlag.NEWCOMMENT,false);
                mCookbook.updateRecipe(r);
            }
            if (syncNotesRecipe(r)) {
                Log.d(TAG, "Notes de " + r.getTitle()+" updaté");
                isChanged=true;
                r.updateTS(AsynCallFlag.NEWRATING,false);
                mCookbook.updateRecipe(r);
            }
            if (r.IsMarkedDeleted()){
                if (deleteRecipeFromCB(r)) {
                    Log.d(TAG, "Recette effacée : " + r.getTitle());
                    mCookbook.removeRecipe(r);
                    mCookbook.deleteImage(r);
                    isChanged=true;
                } else{
                    Log.d(TAG, "Recette " + r.getTitle()+" non effacée");
                }
            }
        }
        return isChanged;
    }

    @Override
    protected void onPostExecute(Boolean isChanged) {
        super.onPostExecute(isChanged);
        Log.d(TAG, "Has changed ? " + isChanged);
    }

    private Boolean test204() {
        try {
            URL url = new URL(mSession.getURLPath() + PHP204);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(mSession.getConnectTimeout());
            conn.setReadTimeout(mSession.getReadTimeout());
            conn.setRequestMethod("HEAD");
            InputStream in = conn.getInputStream();
            int status = conn.getResponseCode();
            in.close();
            conn.disconnect();
            return (status == HttpURLConnection.HTTP_NO_CONTENT);
        } catch (Exception e) {
            Log.d(TAG, "Test 204 : " + e);
            return false;
        }
    }

    private Boolean uploadRecipe(Recipe r) {
        HashMap<String, String> data = new HashMap<>();
        String s1;
        DateFormat dateFormat = new SimpleDateFormat(MYSQLDATEFORMAT);
        data.put("idrecipe", r.getId().toString());
        data.put("iduser", r.getOwner().getId().toString());
        data.put("title", r.getTitle());
        data.put("source", r.getSource());
        data.put("sourceurl", r.getSource_url_name());
        DecimalFormat formatter = new DecimalFormat("00");
        data.put("nbpers", formatter.format(r.getNbPers()));
        s1 = dateFormat.format(r.getDate());
        data.put("date", s1);
        for (int i = 0; i < r.getNbStepMax(); i++) {
            s1 = "etape" + formatter.format(i + 1);
            data.put(s1, r.getStep(i + 1));
        }
        for (int i = 0; i < r.getNbIngMax(); i++) {
            s1 = "ing" + formatter.format(i + 1);
            data.put(s1, r.getIngredient(i + 1));
        }
        data.put("season", r.getSeason().toString());
        data.put("difficulty", r.getDifficulty().toString());
        String result = mNetUtils.sendPostRequestJson(mSession.getURLPath() + PHPUPDATECREATE, data);
        if (result==null) return false;
        if (!result.trim().equals("1")) {
            Log.d(TAG, "Retour de "+PHPUPDATECREATE+" =>"+result+"<");
        return false;}
        return true;
    }

    private Boolean uploadPhoto(Recipe r) {
        String s1;
        File file = mCookbook.getPhotoFile(r);
        Bitmap bitmap = PictureUtils.getBitmap(file.getPath());
        if (bitmap == null) {
            Log.d(TAG, "Pas de bitmap pour " + r.getTitle());
            return false;
        }
        String uploadImage = PictureUtils.getStringImage(bitmap);
        HashMap<String, String> data = new HashMap<>();
        data.put("idrecipe", r.getId().toString());
        if (uploadImage==null) return false;
        data.put("image", uploadImage);
        DateFormat dateFormat = new SimpleDateFormat(MYSQLDATEFORMAT);
        s1 = dateFormat.format(r.getDatePhoto());
        data.put("date", s1);
        String result = mNetUtils.sendPostRequestJson(mSession.getURLPath() + PHPUPUPLOADPHOTO, data);
        if (result==null) return false;
        if (!result.trim().equals("1")) {
            Log.d(TAG, "Retour de "+ PHPUPUPLOADPHOTO+" = "+result);
            return false;}
        return true;
    }
    private Boolean deleteRecipeFromCB(Recipe r) {
        HashMap<String, String> data = new HashMap<>();
        data.put("idrecipe", r.getId().toString());
        data.put("iduser", mSession.getUser().getId().toString());
        String result = mNetUtils.sendPostRequestJson(mSession.getURLPath() + PHPDELETERECIPE, data);
        if (result==null) return false;
        if (!result.trim().equals("1")) {
            Log.d(TAG, "Retour de "+ PHPDELETERECIPE+" = "+result);
            return false;}
        return true;
    }

    private Boolean syncCommentsRecipe(Recipe r) {
        boolean ret=false;
        HashMap<String, String> data = new HashMap<>();
        data.put("idrecipe", r.getId().toString());
        String result = mNetUtils.sendPostRequestJson(mSession.getURLPath() + PHPGETCOMMENTSOFRECIPE, data);
        List<Comment> downloadedComments=mNetUtils.parseCommentsOfRecipe(result);
        List<Comment> recipeComments=r.getComments();
        //----------- boucle local
        List<Comment> c1oc= new ArrayList<>();
        Comment c,ci;
        for (int i = 0; i < recipeComments.size(); i++){
            ci=recipeComments.get(i);
            c=new Comment(ci.getTxt(),ci.getUser(),ci.getDate());
            if (downloadedComments.indexOf(c)==-1) c1oc.add(c);
        }
        //----------- boucle serveur
        List<Comment> cser= new ArrayList<>();
        for (int i = 0; i < downloadedComments.size(); i++){
            ci=downloadedComments.get(i);
            c=new Comment(ci.getTxt(),ci.getUser(),ci.getDate());
            if (recipeComments.indexOf(c)==-1) {
                cser.add(c);
            }
        }
        for (int i = 0; i < cser.size(); i++){
            r.addComment(cser.get(i));
        }
        if (cser.size()>0) {
            mCookbook.updateRecipe(r);
            ret=true;
        }
        if (c1oc.size()>0) {
            if (!uploadComments(r,c1oc)) {ret=false;}
            else {ret=true;}
        }
        return ret;
    }

    private Boolean uploadComments(Recipe r, List<Comment> cs){
        if (cs==null) return false;
        if (cs.size()==0) return true;
        HashMap<String, String> data = new HashMap<>();
        DateFormat dateFormat = new SimpleDateFormat(MYSQLDATEFORMAT);
        String s1;
        boolean b=true;
        for (Comment c:cs) {
            data.clear();
            data.put("idrecipe", r.getId().toString());
            data.put("idfrom", c.getUser().getId().toString());
            data.put("comment", c.getTxt());
            s1 = dateFormat.format(c.getDate());
            data.put("date",s1  );
            String result = mNetUtils.sendPostRequestJson(mSession.getURLPath() + PHPADDCOMMENTTORECIPE, data);
            if (result==null) b=false;
            if (!result.equals("1")) {
                Log.d(TAG, "Retour de "+ PHPADDCOMMENTTORECIPE+" = "+result);
                b=false;}
        }
        return b;
    }
    private Boolean syncNotesRecipe(Recipe r) {
        boolean ret=false;
        HashMap<String, String> data = new HashMap<>();
        data.put("idrecipe", r.getId().toString());
        String result = mNetUtils.sendPostRequestJson(mSession.getURLPath() + PHPGETNOTESOFRECIPE, data);
        List<Note> downloadedNotes=mNetUtils.parseNotesOfRecipe(result);
        List<Note> recipeNotes=r.getNotes();
        //----------- boucle local
        List<Note> c1oc= new ArrayList<>();
        Note c,ci;
        for (int i = 0; i < recipeNotes.size(); i++){
            ci=recipeNotes.get(i);
            c=new Note(ci.getNote(),ci.getUser(),ci.getDate());
            if (downloadedNotes.indexOf(c)==-1) c1oc.add(c);
        }
        //----------- boucle serveur
        List<Note> cser= new ArrayList<>();
        for (int i = 0; i < downloadedNotes.size(); i++){
            ci=downloadedNotes.get(i);
            c=new Note(ci.getNote(),ci.getUser(),ci.getDate());
            if (recipeNotes.indexOf(c)==-1) {
                cser.add(c);
            }
        }
        for (int i = 0; i < cser.size(); i++){
            r.addNote(cser.get(i));
        }
        if (cser.size()>0) {
            mCookbook.updateRecipe(r);
            ret=true;
        }
        if (c1oc.size()>0) {
            if (!uploadNotes(r,c1oc)) {ret=false;}
            else {ret=true;}
        }
        return ret;
    }
    private Boolean uploadNotes(Recipe r, List<Note> cs){
        if (cs==null) return false;
        if (cs.size()==0) return true;
        HashMap<String, String> data = new HashMap<>();
        DateFormat dateFormat = new SimpleDateFormat(MYSQLDATEFORMAT);
        DecimalFormat formatter = new DecimalFormat("00");
        String s1;
        boolean b=true;
        for (Note c:cs) {
            data.clear();
            data.put("idrecipe", r.getId().toString());
            data.put("idfrom", c.getUser().getId().toString());
            data.put("note", formatter.format(c.getNote()));
            s1 = dateFormat.format(c.getDate());
            data.put("date",s1  );
            String result = mNetUtils.sendPostRequestJson(mSession.getURLPath() + PHPADDNOTETORECIPE, data);
            if (result==null) b=false;
            if (!result.equals("1")) {
                Log.d(TAG, "Retour de "+ PHPADDNOTETORECIPE+" = "+result);
                b=false;}
        }
        return b;
    }
    private Boolean syncTiersRecipe(User user) {
        boolean ret=false;
        HashMap<String, String> data = new HashMap<>();
        data.put("iduser", user.getId().toString());
        String result = mNetUtils.sendPostRequestJson(mSession.getURLPath() + PHPGETSTAMPSTIERS, data);
        if (result==null)return ret;
        List<Recipe> tiersRecipe=mNetUtils.parseStampsTiers(result);
        if (tiersRecipe==null) return ret;
        boolean withphoto=false,update=false;
        Recipe rloc, rnew;
        for(Recipe r:tiersRecipe) {
            rloc=mCookbook.getRecipe(r.getId());
            if (rloc==null) {
                rnew=downloadRecipe(r);
                mCookbook.addRecipe(rnew);
                Log.d(TAG, "Recette "+ rloc.getTitle()+" downloadée");
                ret=true;
            } else {
                if (rloc.IsVisible()){
                    withphoto=IsAfterAndNonNull(r.getDatePhoto(), rloc.getDatePhoto());
                    update=IsAfterAndNonNull(r.getDate(), rloc.getDate());
                    if(update || withphoto){
                        if (updateRecipe(rloc, withphoto)){
                            mCookbook.updateRecipe(rloc);
                            Log.d(TAG, "Recette "+ rloc.getTitle()+" updatée (with photo :"+withphoto+")");
                            ret=true;
                        }
                  } else continue;
                }
            }
        }
        return ret;
    }
    private boolean IsAfterAndNonNull(Date ds, Date dl){
        Calendar c=Calendar.getInstance();
        c.set(2000,0,1,0,0, 0);
        Date d0=c.getTime();
        boolean morerecent=(ds.compareTo(dl)==1);
        boolean isnotnull=(ds.compareTo(d0)==1);
        return morerecent && isnotnull;
    }

    private Recipe downloadRecipe(Recipe ref){
        if (ref==null) return null;
        HashMap<String,String> data = new HashMap<>();
        data.put("idrecipe", ref.getId().toString().trim());
        data.put("iduser", mSession.getUser().getId().toString().trim());
        String result = mNetUtils.sendPostRequestJson(mSession.getURLPath()+PHPGETRECIPEFROMCBWITHPHOTO,data);
        Recipe r=new Recipe();
        try {
            JSONArray jarr1 = new JSONArray(result);
            if (jarr1.length()!=1){
                Log.d(TAG, " Number of json objects from downloadRecipes =" + jarr1.length()+" !");
                return null;
            } else {
                JSONObject obj = jarr1.getJSONObject(0);
                r = new Recipe(UUID.fromString(obj.getString("id_recipe")));
                if (!mNetUtils.parseObjectRecipe(r,obj, true, true)) return null;
            }
        } catch (Exception e) {
            Log.d(TAG, " Error parsing CB in downloadRecipe" + e);
        }
        return r;
    }
    private Boolean updateRecipe(Recipe ref, boolean withphoto){
        if (ref==null) return null;
        HashMap<String,String> data = new HashMap<>();
        data.put("idrecipe", ref.getId().toString().trim());
        data.put("withphoto", (withphoto)?"1":"0");
        String result = mNetUtils.sendPostRequestJson(mSession.getURLPath()+ PHPGETRECIPEFROMCB,data);
        Recipe r=new Recipe();
        try {
            JSONArray jarr1 = new JSONArray(result);
            if (jarr1.length()!=1){
                Log.d(TAG, " Number of json objects from downloadRecipes =" + jarr1.length()+" !");
                return null;
            } else {
                JSONObject obj = jarr1.getJSONObject(0);
                if (!mNetUtils.parseObjectRecipe(ref,obj, withphoto, false)) {
                    Log.d(TAG, " Null recipe from JSOn object" + result);
                    return false;
                }
            }
        } catch (Exception e) {
            Log.d(TAG, " Error parsing CB in downloadRecipe" + e);
            return false;
        }
        return true;
    }
}

package com.example.cookbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

enum AsynCallFlag { NEWRECIPE, NEWPHOTO, NEWCOMMENT, NEWRATING, GLOBALSYNC, DELETERECIPE};

class AsyncCallClass extends AsyncTask<Void, Integer, String[]> {
    // Constantes
    private static final String TAG = "CB_AsynCalls";
    private static final String PHP204 = "return204.php";
    private static final String PHPUPDATECREATE = "updateorcreaterecipe.php";
    private static final String PHPUPUPLOADPHOTO = "uploadphotointorecipe.php";
    // Variable
    private Context mContext;
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
    protected String[] doInBackground(Void... params) {
        String[] s = {"Init", "true"};
        if (!test204()) {
            s[0]="Pas de connection";
            s[1]="false";
            return s;
        }
        //************ Boucle sur recettes dont l'utilisateur est l'auteur
        List<Recipe> mrecipes=mCookbook.getRecipes();
        List<Recipe> myrecipes=mCookbook.getRecipes();
        for(Recipe r:mrecipes){
            if (!r.getOwnerIdString().equals(mSession.getUser().getId().toString())){
                myrecipes.remove(r);
            }
        }
        for(Recipe r:myrecipes){
            if (r.getTS(AsynCallFlag.NEWRECIPE)==1){
                Log.d(TAG, "Recipe à updater :" + r.getTitle());
                if (uploadRecipe(r)) {
                    //mettre à jour TS
                    Log.d(TAG, "Recipe updatée :" + r.getTitle());
                    r.updateTS(AsynCallFlag.NEWRECIPE,false);
                    mCookbook.updateRecipe(r);
                } else{
                    Log.d(TAG, "Recipe non updatée :" + r.getTitle());
                }
            }
            if (r.getTS(AsynCallFlag.NEWPHOTO)==1){
                Log.d(TAG, "Photo à update" + r.getTitle());
            }
            if (r.getTS(AsynCallFlag.NEWCOMMENT)==1){
                Log.d(TAG, "Comments à update" + r.getTitle());
            }
            if (r.getTS(AsynCallFlag.NEWRATING)==1){
                Log.d(TAG, "Notes à updater" + r.getTitle());
            }
            if (r.IsMarkedDeleted()){
                Log.d(TAG, "Recette à effeacer" + r.getTitle());
            }

        }
        //************ Boucle recette à effacer
        //************ Boucle recette tiers
        /*
        switch (asyn) {
            case NEWRECIPE:{
                if (uploadRecipe(r)) { // update or create recipe
                    r.updateTS(asyn,false);
                    s[1]="True";
                } else { s[1]="False";}
                return s;
            }
            case NEWPHOTO:{
                if (uploadPhoto(r)) {
                    r.updateTS(asyn,false);
                    s[1]="True";
                } else { s[1]="False";}
                return s;
            }
            case NEWCOMMENT:
                return s;
            case NEWRATING:
                return s;
            case DELETERECIPE:
                return s;
            case GLOBALSYNC:
                return s;
            default:
                return s;
        } */
        return s;
    }

    @Override
    protected void onPostExecute(String[] retAsyn) {
        Log.d(TAG, "Post execute " + retAsyn[0]);
        return;
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
            if (status == HttpURLConnection.HTTP_NO_CONTENT) {
                //Log.d(TAG, "Test 204 : true ");
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.d(TAG, "Test 204 : " + e);
            return false;
        }
    }

    private Boolean uploadRecipe(Recipe r) {
        HashMap<String, String> data = new HashMap<>();
        String s1;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
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
            Log.d(TAG, "Retour de "+PHPUPDATECREATE+" = "+result);
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
                data.put("image", uploadImage);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");  // check format
                s1 = dateFormat.format(r.getDatePhoto());
                data.put("date", s1);
                String result = mNetUtils.sendPostRequestJson(mSession.getURLPath() + PHPUPUPLOADPHOTO, data);
                return (result.trim().equals("1"));
            }

}

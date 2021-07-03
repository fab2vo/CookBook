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

enum AsynCallFlag { NEWRECIPE, NEWPHOTO, NEWCOMMENT, NEWRATING, GLOBALSYNC, DELETERECIPE};

public class AsynCallClass {
    private String TAG = "AsynCalls";
    private static String PHP204 = "return204.php";
    private static final String PHPUPDATECREATE = "createnewrecipe.php";
    private static final String PHPUPUPLOADPHOTO = "uploadphotointorecipe.php";
    private Context mContext;
    private SessionInfo mSession;
    private NetworkUtils mNetUtils;
    private CookBook mCookbook;

    public AsynCallClass() {
        Log.d(TAG, "Creation classe");
    }

    public void setAsynCallClass(Context context) {
        mContext = context;
        Log.d(TAG, "Creation classe context");
    }

    public void LaunchAsync(AsynCallFlag aa, Recipe rr) {
        Log.d(TAG, "Dans LaunchAsync");

        class AsyncCalls extends AsyncTask<Object, Integer, String[]> {

            @Override
            protected String[] doInBackground(Object... params) {
                Log.d(TAG, "Dans LaunchAsync");
                AsynCallFlag asyn = (AsynCallFlag) params[0];
                Log.d(TAG, "params[0]=" + asyn.toString());
                if (!test204()) {
                    Log.d(TAG, "Test 204 negatif");
                    String[] s = {asyn.toString(), "false"};
                    return s;
                }
                mCookbook = CookBook.get(mContext);
                mSession = SessionInfo.get(mContext);
                mNetUtils = new NetworkUtils(mContext);
                String[] s = {asyn.toString(), "false"};
                Recipe r = (Recipe) params[1]; // can be null
                if (asyn != AsynCallFlag.GLOBALSYNC) {
                    Log.d(TAG, "params[1]=" + r.getTitle());
                }
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
                //
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
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");  // check format
                data.put("idrecipe", r.getId().toString());
                data.put("iduser", r.getOwner().getId().toString());
                data.put("title", r.getTitle().trim());
                data.put("source", r.getSource().trim());
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
                String result = mNetUtils.sendPostRequestJson(mSession.getURLPath() + PHPUPDATECREATE, data);
                return (result.trim().equals("1"));
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
        AsyncCalls runtask = new AsyncCalls();
        Log.d(TAG, "Aavant execute "+aa.toString()+" "+rr.getTitle());
        runtask.execute(aa,rr);
    }
}

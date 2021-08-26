package com.fdx.cookbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NetworkUtils {
    private SessionInfo mSession;
    private static final String TAG = "CB_NetworkUtils";
    private static final String MYSQLDATEFORMAT="yyyy-MM-dd HH:mm:ss";

    public NetworkUtils(Context c){
        mSession=SessionInfo.get(c);
    }

    public String sendPostRequestJson(String requestURL,
                                  HashMap<String, String> postDataParams) {

        URL url;
        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(mSession.getReadTimeout());
            conn.setConnectTimeout(mSession.getConnectTimeout());
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(this.getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            os.close();
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json;
            while ((json = bufferedReader.readLine()) != null) {
                sb.append(json + "\n");
            }
            return sb.toString().trim();
        } catch (Exception e) {
            Log.d(TAG, ">"+e);
            return null;
        }
    }
    private String getPostDataString(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            try {result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (Exception e){
                //fdx Log.d(TAG, "Pb with >"+entry.getKey()+":"+entry.getValue()+"<");
            }
        }
        return result.toString();
    }
    public List<Comment> parseCommentsOfRecipe(String json){
        Comment c;
        List<Comment> cs=new ArrayList<>();
        if ((json==null)||(json.equals(""))) return null;
        try {
            JSONArray jarr1=new JSONArray(json);
            for (int i=0; i<jarr1.length(); i++){
                JSONObject obj = jarr1.getJSONObject(i);
                c=parseObjectComment(obj);
                if (c!=null) cs.add(c);
            }
        } catch (Exception e){
            //fdx Log.d(TAG, "Failure in parsing JSON Array Comments "+e);
            return null;
        }
        return cs;
    }

    public Comment parseObjectComment(JSONObject obj){
        try {
        UUID uuid=UUID.fromString(obj.getString("id_user"));
        User u=new User(obj.getString("family"), obj.getString("name") );
        u.setId(uuid);
        String s=obj.getString("date_comment");
        Date date=new SimpleDateFormat(MYSQLDATEFORMAT).parse(s);
        return new Comment(obj.getString("comment"),u,date);}
        catch (Exception e){
            //fdx Log.d(TAG, "Failure in parsing JSONObject Comments "+e);
            return null;
        }
    }
    public List<Note> parseNotesOfRecipe(String json){
        Note n;
        List<Note> ns=new ArrayList<>();
        if ((json==null)||(json.equals(""))) return null;
        try {
            JSONArray jarr1=new JSONArray(json);
            for (int i=0; i<jarr1.length(); i++){
                JSONObject obj = jarr1.getJSONObject(i);
                n=parseObjectNote(obj);
                if (n!=null) ns.add(n);
            }
        } catch (Exception e){
            //fdx Log.d(TAG, "Failure in parsing JSON Array Comments "+e);
            return null;
        }
        return ns;
    }

    public Note parseObjectNote(JSONObject obj){
        try {
            // uuid=UUID.fromString(obj.getString("id_recipe"));
            UUID uuid=UUID.fromString(obj.getString("id_user"));
            User u=new User(obj.getString("family"), obj.getString("name") );
            u.setId(uuid);
            String s=obj.getString("date_note");
            Date date=new SimpleDateFormat(MYSQLDATEFORMAT).parse(s);
            return new Note(obj.getInt("note"),u,date);}
        catch (Exception e){
            //fdx Log.d(TAG, "Failure in parsing JSONObject Note : "+e);
            return null;
        }
    }
    public Recipe parseObjectRecipeStamp(JSONObject obj){
        try {
            //---------------- uuid and users
            UUID uuid = UUID.fromString(obj.getString("id_recipe"));
            Recipe r = new Recipe(uuid);
            uuid = UUID.fromString(obj.getString("id_owner"));
            User u = new User(uuid);
            r.setOwner(u);
            //dates
            Date date;
            String s1 = obj.getString("lastupdate_recipe");
            if (s1==null) return null;
            if ((!s1.equals("null"))&&(s1.length()>5)) {
                date = new SimpleDateFormat(MYSQLDATEFORMAT).parse(s1);
                r.setDate(date);
            } else {return null;}
            s1 = obj.getString("lastupdate_photo");
            if ((!s1.equals("null"))&&(s1.length()>5)) {
                date = new SimpleDateFormat(MYSQLDATEFORMAT).parse(s1);
                r.setDatePhoto(date);
            }
            s1=obj.getString("message");
            if ((s1==null)||(s1.equals("null"))) s1="";
            r.setMessage(s1);
            r.setStatus(StatusRecipe.valueOf(obj.getString("status")));
            s1 = obj.getString("id_from");
            if ((!s1.equals("null"))&&(s1.length()>5)) {
                uuid = UUID.fromString(s1);
                u = new User(uuid);
                r.setOwner(u);
            }
            return r;
        }
        catch (Exception e){
            //fdx Log.d(TAG, "Failure in parsing JSONObject Recette Stamp : "+e);
            return null;
        }
    }

    public List<Recipe> parseStampsTiers(String json){
        Recipe r;
        List<Recipe> rs=new ArrayList<>();
        if ((json==null)||(json.equals(""))) return null;
        try {
            JSONArray jarr1=new JSONArray(json);
            for (int i=0; i<jarr1.length(); i++){
                JSONObject obj = jarr1.getJSONObject(i);
                r=parseObjectRecipeStamp(obj);
                if (r!=null) rs.add(r);
            }
        } catch (Exception e){
            //fdx Log.d(TAG, "Failure in parsing JSON Array Comments "+e);
            return null;
        }
        return rs;
    }

    public boolean parseObjectRecipe(Recipe r, JSONObject obj, boolean withphoto, boolean full){
        try {
            UUID uuid;
            String s1, s2;
            User u;
            if (full) {
                uuid = UUID.fromString(obj.getString("id_owner"));
                s1 = obj.getString("owner_family");
                s2 = obj.getString("owner_name");
                u = new User(s1, s2);
                u.setId(uuid);
                r.setOwner(u);
                s1=obj.getString("message");
                if ((s1==null)||(s1.equals("null"))) s1="";
                r.setMessage(s1);
                r.setStatus(StatusRecipe.valueOf(obj.getString("status")));
                s1 = obj.getString("id_from");
                if ((!s1.equals("null"))&&(s1.length()>5)&&(s1!=null)) {
                    uuid = UUID.fromString(s1);
                    s1 = obj.getString("from_family");
                    s2 = obj.getString("from_name");
                    u = new User(s1, s2);
                    u.setId(uuid);
                    r.setUserFrom(u);
                }
            }
            //------------ dates
            Date date;
            s1 = obj.getString("lastupdate_recipe");
            if ((!s1.equals("null"))&&(s1.length()>5)&&(s1!=null)) {
                date = new SimpleDateFormat(MYSQLDATEFORMAT).parse(s1);
                r.setDate(date);
            } else {return false;}
            s1 = obj.getString("lastupdate_photo");
            if ((!s1.equals("null"))&&(s1.length()>5)&&(s1!=null)) {
                date = new SimpleDateFormat(MYSQLDATEFORMAT).parse(s1);
                r.setDatePhoto(date);
            }
            //----------------- titre, source x2, nbpers
            s1 = obj.getString("title");
            URL url;
            if (s1==null) return false;
            if (s1.equals("null")) return false;
            r.setTitle(s1);
            r.setSource(obj.getString("source"));
            s1 = obj.getString("source_url");
            if (!s1.equals("null")&&(s1.length()>5)&&(s1!=null)) {
                try {
                    url = new URL(s1);
                    r.setSource_url(url);
                } catch (MalformedURLException e) {
                    //fdx Log.d(TAG, "getURL from cookbook download failed in parse recipe");
                }
            }
            int nbp=obj.getInt("nb_pers");
            if (nbp==0) return false;
            r.setNbPers(nbp);
            //---------------- Etapes and ing
            DecimalFormat formatter = new DecimalFormat("00");
            for (int i = 0; i < r.getNbStepMax(); i++) {
                s1 = "etape" + formatter.format(i + 1);
                s2=obj.getString(s1);
                if (!s2.equals("null")) {
                    r.setStep(i + 1, s2);}
            }
            for (int i = 0; i < r.getNbIngMax(); i++) {
                s1 = "ing" + formatter.format(i + 1);
                s2=obj.getString(s1);
                if (!s2.equals("null")) {
                    r.setIngredient(i + 1, s2);}
            }
            //----------------- enum
            r.setSeason(RecipeSeason.valueOf(obj.getString("season")));
            r.setDifficulty(RecipeDifficulty.valueOf(obj.getString("difficulty")));
            r.setType(RecipeType.valueOf(obj.getString("type")));
            // --------------- photo
            CookBook cb = CookBook.get(mSession.getContext());
            File f=cb.getPhotoFile(r);
            if (withphoto) {
                s1 = obj.getString("photo");
                if ((!s1.equals("null"))&&(!s1.equals(""))&&(s1!=null)) {
                    Bitmap bm=PictureUtils.getBitmapFromString(s1);
                    if (f.exists()){
                        //fdx Log.d(TAG, "file  " + f.toString()+" ecrasée dans parsing recipe !");
                        f.delete();
                    }
                    try {
                        if(!f.createNewFile()) {
                            //fdx Log.d(TAG, "Error in creating file  "+f.toString());
                        }
                    } catch (IOException e) {
                        //fdx Log.d(TAG, "Error in creating file "+f.toString()+":" +e);
                    }
                    try {
                        FileOutputStream out = new FileOutputStream(f);
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        //fdx Log.d(TAG, "Storing bitmap error  " + e);
                    }

                }
            }

            return true;
        }
        catch (Exception e){
            //fdx Log.d(TAG, "Failure in parsing JSONObject Recette : "+e);
            return false;
        }
    }

}

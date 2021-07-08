package com.example.cookbook;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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

    public String sendGetRequest(String uri) {
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String result;
            StringBuilder sb = new StringBuilder();
            while((result = bufferedReader.readLine())!=null){
                sb.append(result);
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public String sendPostRequest(String requestURL,
                                  HashMap<String, String> postDataParams) {
        URL url;
        String response = "";
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
            writer.write(getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                response = br.readLine();
            } else {
                response = "Error Registering";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
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
                Log.d(TAG, "Pb with >"+entry.getKey()+":"+entry.getValue()+"<");
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
            Log.d(TAG, "Failure in parsing JSON Array Comments "+e);
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
            Log.d(TAG, "Failure in parsing JSONObject Comments "+e);
            return null;
        }
    }
}

package com.example.cookbook;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class SplashActivity extends AppCompatActivity {
    //private ListView mListView;
    private TextView mMoto;
    private Button mGo;
    private SessionInfo mSession;
    private Boolean mIsConnected;
    private TextView mEnterFamilyLbl;
    private EditText mEnterFamily;
    private TextView mEnterMemberLbl;
    private EditText mEnterMember;
    private TextView mEnterPwdLbl;
    private EditText mEnterPwd;
    private TextView mEnterMessage;
    private Button mNewSession;
    private TextView mNewSessionTxt;
    private Button mNewMember;
    private TextView mNewMemberTxt;
    private Button mNewFamily;
    private TextView mNewFamilyTxt;
    private String mState;
    private static final String TAG = "DebugSplashActivity";
    private static String NEW_FAMILY="Family";
    private static String NEW_MEMBER="Member";
    private static String NEW_Session="Session";

    private static String URLPATH="http://82.66.37.73:8085/cb/";
    private static String PHPREQ="getrecipestamps.php?id_user=c81d4e2e-bcf2-11e6-869b-8df92533d2db";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //mListView = (ListView) findViewById(R.id.splash_listView);
        mMoto=(TextView) findViewById(R.id.splash_moto);
        mMoto.setText(R.string.splash_moto_txt);
        mGo=(Button) findViewById(R.id.splash_button_go);
        mGo.setText(R.string.splash_go_txt);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.splash_progBar);
        progressBar.setProgress(0);
        mIsConnected=haveNetworkConnection();
        mSession= SessionInfo.get(getApplicationContext());

        if (!mSession.IsEmpty()){
            // utilisateur déjà défini => check user belong to family, sync et get started
            Toast.makeText(getApplicationContext(), mSession.getUserNameComplete()+" "+mIsConnected, Toast.LENGTH_SHORT).show();
        }
        if (!mIsConnected){
            Toast.makeText(getApplicationContext(), "No network connection", Toast.LENGTH_SHORT).show();
            finish();
        }
        // pas d'utilisateur pré-défini
        // il faudra mettre à jour le nouvel utilisateur
        User user=new User("Devaux_Lion de ML","Fabrice");
        user.setId(UUID.fromString("c81d4e2e-bcf2-11e6-869b-7df92533d2db"));
        // et le mettre dans sharedpref
        mSession.setStoredUser(user);
        ////
        mEnterFamilyLbl=(TextView) findViewById(R.id.splash_edit_family_lbl);
        mEnterFamily=(EditText) findViewById(R.id.splash_edit_family);
        mEnterMemberLbl=(TextView) findViewById(R.id.splash_edit_member_lbl);
        mEnterMember=(EditText) findViewById(R.id.splash_edit_member);
        mEnterPwdLbl=(TextView) findViewById(R.id.splash_edit_pwd_lbl);
        mEnterPwd=(EditText) findViewById(R.id.splash_edit_pwd);
        mEnterMessage=(TextView) findViewById(R.id.splash_edit_message);
        mNewSession=(Button) findViewById(R.id.splash_button_session);
        mNewSessionTxt=(TextView) findViewById(R.id.splash_button_session_textview);
        mNewMember=(Button) findViewById(R.id.splash_button_member);
        mNewMemberTxt=(TextView) findViewById(R.id.splash_button_member_textview);
        mNewFamily=(Button) findViewById(R.id.splash_button_family);
        mNewFamilyTxt=(TextView) findViewById(R.id.splash_button_family_textview);
        updateTop();

        getJSON(URLPATH+PHPREQ);
        mGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "OnClick : go");
                getStarted();
            }
        });

    }

    private void getJSON(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
 /*               try {
                    loadIntoListView(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                } */
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                         sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                return null;
                }
            }
        }

    GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }
/*    private void loadIntoListView(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        String[] idrecipe = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            idrecipe[i] = obj.getString("lastupdate_recipe");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, idrecipe);
        mListView.setAdapter(arrayAdapter);
    } */

    private void getStarted(){
        Intent intent=new Intent(getApplicationContext(), RecipeListActivity.class);
        startActivity(intent);
    }
    private boolean haveNetworkConnection() {
        boolean HaveConnectedWifi = false;
        boolean HaveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    HaveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    HaveConnectedMobile = true;
        }
        return HaveConnectedWifi || HaveConnectedMobile;
    }

    private void updateTop(){
        mEnterFamilyLbl.setText(R.string.splash_edit_family_label);
        mEnterFamily.setText(R.string.splash_edit_family_hint);
        mEnterMemberLbl.setText(R.string.splash_edit_member_label);
        mEnterMember.setText(R.string.splash_edit_member_hint);
        mEnterPwdLbl.setText(R.string.splash_edit_pwd_label);
        mEnterPwd.setText(R.string.splash_edit_pwd_hint);
        mEnterMessage.setText("Hello");
        mNewSession.setText(R.string.splash_button_session_txt);
        mNewSessionTxt.setText(R.string.splash_button_session_expl);
        mNewMember.setText(R.string.splash_button_member_txt);
        mNewMemberTxt.setText(R.string.splash_button_member_expl);
        mNewFamily.setText(R.string.splash_button_family_txt);
        mNewSessionTxt.setText(R.string.splash_button_family_expl);
    }
}

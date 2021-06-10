package com.example.cookbook;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;

public class SplashActivity extends AppCompatActivity {
    //private ListView mListView;
    private TextView mMoto;
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
    private ImageView mPwdLight;
    private Button mNewMember;
    private TextView mNewMemberTxt;
    private ImageView mMemberLight;
    private Button mNewFamily;
    private TextView mNewFamilyTxt;
    private ImageView mFamilyLight;
    private String mState;
    private static final String TAG = "DebugSplashActivity";
    private static String NEW_FAMILY="Family";
    private static String NEW_MEMBER="Member";
    private static String NEW_Session="Session";
    private static final String REGEX_FAMILY="[-_!?\\w\\p{javaLowerCase}\\p{javaUpperCase}()\\p{Space}]*";
    private static final String REGEX_MEMBER="[-_\\w\\p{javaLowerCase}\\p{javaUpperCase}]*";
    private static final String REGEX_PWD="[-_!?\\w\\p{javaLowerCase}\\p{javaUpperCase}()]*";
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
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.splash_progBar);
        progressBar.setProgress(0);
        mSession= SessionInfo.get(getApplicationContext());

        TestConnection t;
        t = new TestConnection();
        t.setTestConnexion(getApplicationContext());
        t.testGo();

        if (!mSession.IsEmpty()){
            // utilisateur déjà défini => check user belong to family, sync et get started
            //Toast.makeText(getApplicationContext(), mSession.getUserNameComplete()+" "+mIsConnected, Toast.LENGTH_SHORT).show();
        }
        /*if (!mIsConnected){
            Toast.makeText(getApplicationContext(), "No network connection", Toast.LENGTH_SHORT).show();
            finish();
        }*/
        // pas d'utilisateur pré-défini
        // il faudra mettre à jour le nouvel utilisateur
        User user=new User("Devaux_Lion de ML","Fabrice");
        user.setId(UUID.fromString("c81d4e2e-bcf2-11e6-869b-7df92533d2db"));
        // et le mettre dans sharedpref
        mSession.setStoredUser(user);
        ////
        mEnterFamilyLbl=(TextView) findViewById(R.id.splash_edit_family_lbl);
        mEnterFamily=(EditText) findViewById(R.id.splash_edit_family);
        mFamilyLight=(ImageView) findViewById(R.id.splash_edit_family_light);
        mEnterMemberLbl=(TextView) findViewById(R.id.splash_edit_member_lbl);
        mEnterMember=(EditText) findViewById(R.id.splash_edit_member);
        mMemberLight=(ImageView) findViewById(R.id.splash_edit_member_light);
        mEnterPwdLbl=(TextView) findViewById(R.id.splash_edit_pwd_lbl);
        mEnterPwd=(EditText) findViewById(R.id.splash_edit_pwd);
        mPwdLight=(ImageView) findViewById(R.id.splash_edit_pwd_light);
        mEnterMessage=(TextView) findViewById(R.id.splash_edit_message);
        mNewSession=(Button) findViewById(R.id.splash_button_session);
        mNewSessionTxt=(TextView) findViewById(R.id.splash_button_session_textview);
        mNewMember=(Button) findViewById(R.id.splash_button_member);
        mNewMemberTxt=(TextView) findViewById(R.id.splash_button_member_textview);
        mNewFamily=(Button) findViewById(R.id.splash_button_family);
        mNewFamilyTxt=(TextView) findViewById(R.id.splash_button_family_textview);
        updateTop();
        getJSON(mSession.getURLPath()+PHPREQ);
        mEnterFamily.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String z=s.toString();
                mFamilyLight.setImageResource((Pattern.matches(REGEX_FAMILY,z)&&(IsLenOK(z,8,45)))?
                        R.drawable.ic_splash_light_green : R.drawable.ic_splash_light_red);
                if (Pattern.matches(REGEX_FAMILY,z)){
                    mNewSession.setEnabled(true);
                    mNewFamily.setEnabled(true);
                }
                else{
                    mNewSession.setEnabled(false);
                    mNewFamily.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        mEnterMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String z=s.toString();
                mMemberLight.setImageResource((Pattern.matches(REGEX_MEMBER,z)&&(IsLenOK(z,1,25)))?
                        R.drawable.ic_splash_light_green : R.drawable.ic_splash_light_red);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        mEnterPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String z=s.toString();
                mPwdLight.setImageResource((Pattern.matches(REGEX_PWD,z)&&(IsLenOK(z,4,12)))?
                        R.drawable.ic_splash_light_green : R.drawable.ic_splash_light_red);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        mNewSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(testAndUpdateLight()){
                    getStarted();
                }
                t.testGo();
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

    private void updateTop(){
        mEnterFamilyLbl.setText(R.string.splash_edit_family_label);
        mEnterFamily.setText(R.string.splash_edit_family_hint);
        mEnterMemberLbl.setText(R.string.splash_edit_member_label);
        mEnterMember.setText(R.string.splash_edit_member_hint);
        mEnterPwdLbl.setText(R.string.splash_edit_pwd_label);
        mEnterPwd.setText(R.string.splash_edit_pwd_hint);
        mEnterMessage.setText("");
        mNewSession.setText(R.string.splash_button_session_txt);
        mNewSessionTxt.setText(R.string.splash_button_session_expl);
        mNewMember.setText(R.string.splash_button_member_txt);
        mNewMemberTxt.setText(R.string.splash_button_member_expl);
        mNewFamily.setText(R.string.splash_button_family_txt);
        mNewFamilyTxt.setText(R.string.splash_button_family_expl);
    }
    private Boolean IsLenOK(String s, int min, int max){
        int l=s.length();
        return ((l>min)&&(l<max));
    }
    private Boolean testAndUpdateLight(){
        Boolean ret=true;
        String message="";
        String z="";
        if(!mSession.IsConnected()){
            ret=false;
            message += getResources().getString(R.string.network_err_no_connection)+"\n";
        } else {
            message += "Connection OK"+"\n";}
        z=mEnterFamily.getText().toString();
        if (Pattern.matches(REGEX_FAMILY,z)&&(IsLenOK(z,8,45))){
                mFamilyLight.setImageResource(R.drawable.ic_splash_light_green);
        } else{
            ret=false;
            message += getResources().getString(R.string.enter_err_family)+"\n";
            mFamilyLight.setImageResource(R.drawable.ic_splash_light_red);}
        z=mEnterMember.getText().toString();
        if (Pattern.matches(REGEX_MEMBER,z)&&IsLenOK(z,1,25)){
                mMemberLight.setImageResource(R.drawable.ic_splash_light_green);
        } else{
            ret=false;
            message += getResources().getString(R.string.enter_err_member)+"\n";
            mMemberLight.setImageResource(R.drawable.ic_splash_light_red);}
        z=mEnterPwd.getText().toString();
        if (Pattern.matches(REGEX_PWD,z)&&IsLenOK(z,4,12)){
            mPwdLight.setImageResource(R.drawable.ic_splash_light_green);
        } else{
            ret=false;
            message += getResources().getString(R.string.enter_err_pwd)+"\n";
            mPwdLight.setImageResource(R.drawable.ic_splash_light_red);}
        mEnterMessage.setText(message);
        return ret;
    }
}

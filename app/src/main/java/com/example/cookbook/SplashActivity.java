package com.example.cookbook;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

public class SplashActivity extends AppCompatActivity {
    private TextView mMoto;
    private String mFamilyEntered;
    private String mMemberEntered;
    private String mPwdEntered;
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
    private ProgressBar mProgressBar;
    private ArrayList<User> memberFamily;
    private SessionInfo mSession;
    private int mState;
    private static final String TAG = "DebugSplashActivity";
    private final static int NEW_FAMILY=1;
    private final static int NEW_MEMBER=2;
    private final static int NEW_SESSION=3;
    private final static int NEW_PWD=3;
    private final static Integer MINMAX[][]={{8,45},{1,25},{4,25}}; // min max pour family, member, pwd
    private static final String REGEX_FAMILY="[-_!?\\w\\p{javaLowerCase}\\p{javaUpperCase}()\\p{Space}]*";
    private static final String REGEX_MEMBER="[-_\\w\\p{javaLowerCase}\\p{javaUpperCase}]*";
    private static final String REGEX_PWD="[-_!?\\w\\p{javaLowerCase}\\p{javaUpperCase}()]*";
    private static final String REGEX[]={REGEX_FAMILY, REGEX_MEMBER, REGEX_PWD};
    private static final String PHPREQFAMILYCOMP="getfamilycomposition.php";
    private static final String PHPREQNEWMEMBER="createnewmember.php";
    private static final String PHPREQNEWFAMILY="createnewfamily.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        mMoto=(TextView) findViewById(R.id.splash_moto);
        mMoto.setText(R.string.splash_moto_txt);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.splash_progBar);
        progressBar.setProgress(0);
        mSession= SessionInfo.get(getApplicationContext());
        mEnterMessage=(TextView) findViewById(R.id.splash_edit_message);
        mEnterMessage.setText("");
        ArrayList <User> memberFamily=new ArrayList<>();

        TestConnection t;
        t = new TestConnection();
        t.setTestConnexion(getApplicationContext());
        t.testGo();

        if (!mSession.IsEmpty()){
            mEnterMessage.setText("Synchronising");
            //getStarted(); //**********SESSION EXSTANTE**************************

        }

        // pas d'utilisateur pré-défini*****************************************
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
        mNewSession=(Button) findViewById(R.id.splash_button_session);
        mNewSessionTxt=(TextView) findViewById(R.id.splash_button_session_textview);
        mNewMember=(Button) findViewById(R.id.splash_button_member);
        mNewMemberTxt=(TextView) findViewById(R.id.splash_button_member_textview);
        mNewFamily=(Button) findViewById(R.id.splash_button_family);
        mNewFamilyTxt=(TextView) findViewById(R.id.splash_button_family_textview);
        updateTop();
        mProgressBar=(ProgressBar) findViewById(R.id.splash_progBar);

        mEnterFamily.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String z=s.toString();
                mFamilyEntered=z;
                Integer d=getDrawableOnCondition(z,NEW_FAMILY);
                mFamilyLight.setImageResource(d);
                buttonEnabled((d==R.drawable.ic_splash_light_green));
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
                mMemberEntered=z;
                Integer d=getDrawableOnCondition(z,NEW_MEMBER);
                mMemberLight.setImageResource(d);
                buttonEnabled((d==R.drawable.ic_splash_light_green));
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
                mPwdEntered=z;
                Integer d=getDrawableOnCondition(z,NEW_PWD);
                mPwdLight.setImageResource(d);
                buttonEnabled((d==R.drawable.ic_splash_light_green));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        mNewSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(testConnectAndDisplay()){
                    mState=NEW_SESSION;
                    goAsyncTasks();
                }
                t.testGo();
            }
        });
        mNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(testConnectAndDisplay()){
                    mState=NEW_MEMBER;
                    goAsyncTasks();
                }
                t.testGo();
            }
        });
        mNewFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(testConnectAndDisplay()){
                    mState=NEW_FAMILY;
                    goAsyncTasks();
                }
                t.testGo();
            }
        });
    }
    private void updateTop(){
        mEnterFamilyLbl.setText(R.string.splash_edit_family_label);
        mEnterFamily.setText(R.string.splash_edit_family_hint);
        mFamilyEntered=getResources().getString(R.string.splash_edit_family_hint);
        mEnterMemberLbl.setText(R.string.splash_edit_member_label);
        mEnterMember.setText(R.string.splash_edit_member_hint);
        mMemberEntered=getResources().getString(R.string.splash_edit_member_hint);
        mEnterPwdLbl.setText(R.string.splash_edit_pwd_label);
        mEnterPwd.setText(R.string.splash_edit_pwd_hint);
        mPwdEntered=getResources().getString(R.string.splash_edit_pwd_hint);
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

    private Integer getDrawableOnCondition(String z, Integer state){
        state=state-1;
        int retDraw =0;
        retDraw=((Pattern.matches(REGEX[state],z)&&(IsLenOK(z,MINMAX[state][0],MINMAX[state][1])))?
                R.drawable.ic_splash_light_green : R.drawable.ic_splash_light_red);
        Integer err_mess[]={R.string.enter_err_family,R.string.enter_err_member,R.string.enter_err_pwd};
        if (retDraw==R.drawable.ic_splash_light_red){
        mEnterMessage.setText(getResources().getString(err_mess[state],MINMAX[state][0],MINMAX[state][1]));}
        else {mEnterMessage.setText("");}
        return retDraw;
    }
    private Boolean testConnectAndDisplay(){
        Boolean ret=true;
        String message="";
        if(!mSession.IsConnected()){
            ret=false;
            message += getResources().getString(R.string.network_err_no_connection)+"\n";
        }
        mEnterMessage.setText(message);
        return ret;
    }


    private void goAsyncTasks() {

        class GoAsyncTasks extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
 /*               if (s==null){
                    mEnterMessage.setText(R.string.enter_err_com);
                    return;
                }
                else {
                    memberFamily=parseJsonFamily(s);
                }
                goFamilyGet(); */
            }

            @Override
            protected String doInBackground(Void... voids) {
                String s=getFamilyComp();
                if (s==null){
                    mEnterMessage.setText(R.string.enter_err_com);
                    return null;
                }
                else {
                    memberFamily=parseJsonFamily(s);
                }
                mProgressBar.setProgress(5);
                Integer cas=goFamilyGet();
                Log.d(TAG, "DoBackGround Retour goFamilyGet : >"+cas+"<");
                String mess="Attention creation";
                if ((cas==NEW_FAMILY)||(cas==NEW_MEMBER)){
                    if (createMember(cas)){
                        mess="membre créé";
                        mProgressBar.setProgress(10);
                    } else mess="membre pas créé";
                }
                mEnterMessage.setText(mess);
              // fin
                return null;
            }
        }
        GoAsyncTasks getAsync = new GoAsyncTasks();
        getAsync.execute();
    }

    private String getFamilyComp(){
        try {
            String link=mSession.getURLPath()+PHPREQFAMILYCOMP;
            String data  = URLEncoder.encode("family", "UTF-8") + "=" +
                    URLEncoder.encode(mFamilyEntered.trim(), "UTF-8");
            data += "&" + URLEncoder.encode("pwd", "UTF-8") + "=" +
                    URLEncoder.encode(mPwdEntered.trim(), "UTF-8");
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json;
            while ((json = bufferedReader.readLine()) != null) {
                sb.append(json + "\n");
            }
            return sb.toString().trim();
        } catch (Exception e) {
            return null;
        }
    }

    private Boolean createMember(int flag){
        try {
            String link=mSession.getURLPath();
            if (flag==NEW_MEMBER) {
                link=link+PHPREQNEWMEMBER;
            } else {
                if (flag==NEW_FAMILY) {
                    link=link+PHPREQNEWFAMILY;
                } else {
                    Log.d(TAG, "Function createmember flag bad value");
                    return false;
                }
            }
            String data  = URLEncoder.encode("family", "UTF-8") + "=" +
                    URLEncoder.encode(mFamilyEntered.trim(), "UTF-8");
            data += "&" + URLEncoder.encode("pwd", "UTF-8") + "=" +
                    URLEncoder.encode(mPwdEntered.trim(), "UTF-8");
            data += "&" + URLEncoder.encode("name", "UTF-8") + "=" +
                    URLEncoder.encode(mMemberEntered.trim(), "UTF-8");
            data += "&" + URLEncoder.encode("iduser", "UTF-8") + "=" +
                    URLEncoder.encode(UUID.randomUUID().toString().trim(), "UTF-8");
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json;
            while ((json = bufferedReader.readLine()) != null) {
                sb.append(json + "\n");
            }
            if (sb.toString().trim().equals("1")) {
                Log.d(TAG, "Function createmember succeed");
                return true;
            } else {
                Log.d(TAG, "Function createmember failed");
                return false;
            }
        } catch (Exception e) {
            Log.d(TAG, "Function createmember feedback from php req : error catched");
            return false;
        }
    }

    private void getStarted(){
        //User user=new User(mFamilyEntered.trim(),mMemberEntered.trim());
        //user.setId(UUID.fromString("c81d4e2e-bcf2-11e6-869b-7df92533d2db"));
        // et le mettre dans sharedpref
        //mSession.setStoredUser(user);
        Intent intent=new Intent(getApplicationContext(), RecipeListActivity.class);
        startActivity(intent);
    }


    public ArrayList<User> parseJsonFamily(String json){
        ArrayList<User> ret=new ArrayList<User>();
        User u;
        String name,dateString;
        UUID uuid;
        Date date;
        try {
            JSONArray jarr1=new JSONArray(json);
            for (int i=0; i<jarr1.length(); i++){
                JSONObject obj = jarr1.getJSONObject(i);
                name=obj.getString("name");
                uuid=UUID.fromString(obj.getString("id_user"));
                u=new User(mFamilyEntered, name );
                u.setId(uuid);
                dateString=obj.getString("last_sync");
                date=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(dateString);
                u.setDate(date);
                ret.add(u);
            }
        }
        catch (Exception e){
            //Log.d(TAG, "Failure in parsing JSON");
        }

        return ret;
    }

    private void buttonEnabled(Boolean b){
        mNewSession.setEnabled(b);
        mNewMember.setEnabled(b);
        mNewFamily.setEnabled(b);
    }

    private Integer goFamilyGet(){
        switch(mState){
            case NEW_SESSION : {
                if (memberFamily.size()==0) {
                    mEnterMessage.setText(R.string.enter_err_member_wo_family);
                    mFamilyLight.setImageResource(R.drawable.ic_splash_light_red);}
                else {
                    mEnterMessage.setText("Family found");
                    User u= new User("","");
                    for(User user:memberFamily){
                        if (user.getName().equals(mMemberEntered)){u=user;}
                    }
                    if (u.getName().equals("")) {
                        mEnterMessage.setText(R.string.enter_err_member_in_family);
                        mMemberLight.setImageResource(R.drawable.ic_splash_light_red);
                    } else {
                        // **** Ready to go foe a new session ********************
                        mEnterMessage.setText("Let us go for a new session");
                        return NEW_SESSION;
                    }
                }
                break;
            }
            case NEW_FAMILY : {
                if (memberFamily.size()==0) {
                    mEnterMessage.setText("Welcome !");
                    // create user ************************************************
                    return NEW_FAMILY;
                }
                else {
                    mEnterMessage.setText(R.string.enter_err_family_already);
                    mFamilyLight.setImageResource(R.drawable.ic_splash_light_red);;
                }
                break;
            }
            case NEW_MEMBER : {
                if (memberFamily.size()==0) {
                    mEnterMessage.setText(R.string.enter_err_member_wo_family);
                    mFamilyLight.setImageResource(R.drawable.ic_splash_light_red);;
                }
                else {
                    // family found
                    User u= new User("","");
                    for(User user:memberFamily){
                        if (user.getName().equals(mMemberEntered)){u=user;}
                    }
                    if (u.getName().equals("")) {
                        // ************** Ready to create member ****************
                        mEnterMessage.setText("Let us go for a new member");
                        return NEW_MEMBER;
                    } else {
                        mEnterMessage.setText(R.string.enter_err_member_notin_family);
                        mMemberLight.setImageResource(R.drawable.ic_splash_light_red);
                    }
                }
                break;
            }
            default : {
                Log.d(TAG, "Switch case on mState anomaly : "+mState);
            }
        }
        return 0;
    }
}

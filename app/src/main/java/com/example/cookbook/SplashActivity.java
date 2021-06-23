package com.example.cookbook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

public class SplashActivity extends AppCompatActivity {
    private TextView mMoto;
    private String mFamilyEntered;
    private String mMemberEntered;
    private String mPwdEntered;
    private String mPwdRead;
    private TextView mEnterFamilyLbl;
    private EditText mEnterFamily;
    private TextView mEnterMemberLbl;
    private EditText mEnterMember;
    private TextView mEnterPwdLbl;
    private EditText mEnterPwd;
    private TextView mEnterMessage;
    private Button mNewSession;
    private Button mNewMember;
    private Button mNewFamily;
    private ProgressBar mProgressBar;
    private ImageView mNetAnim;
    private AnimationDrawable frameNetAnim;
    private ArrayList<User> memberFamily;
    private SessionInfo mSession;
    private NetworkUtils mNetUtils;
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
    private static final String PHPREQUPLOADPHOTO="uploadphotointorecipe.php";

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
        mNetUtils=new NetworkUtils(getApplicationContext());
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
        mEnterMemberLbl=(TextView) findViewById(R.id.splash_edit_member_lbl);
        mEnterMember=(EditText) findViewById(R.id.splash_edit_member);
        mEnterPwdLbl=(TextView) findViewById(R.id.splash_edit_pwd_lbl);
        mEnterPwd=(EditText) findViewById(R.id.splash_edit_pwd);
        mNewSession=(Button) findViewById(R.id.splash_button_session);
        mNewMember=(Button) findViewById(R.id.splash_button_member);
        mNewFamily=(Button) findViewById(R.id.splash_button_family);
        updateTop();
        mProgressBar=(ProgressBar) findViewById(R.id.splash_progBar);
        mNetAnim=(ImageView) findViewById(R.id.net_anim);
        mNetAnim.setBackgroundResource(R.drawable.network_animation);
        mNetAnim.setVisibility(View.INVISIBLE);
        frameNetAnim = (AnimationDrawable) mNetAnim.getBackground();
        updateDisplayOnAsync(false);

        mEnterFamily.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String z=s.toString();
                mFamilyEntered=z;
                Integer d= getColorOnCondition(z,NEW_FAMILY);
                mEnterFamily.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), d));
                buttonEnabled((d==R.color.bg_enter_val));
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
                Integer d= getColorOnCondition(z,NEW_MEMBER);
                mEnterMember.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), d));
                buttonEnabled((d==R.color.bg_enter_val));
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
                Integer d= getColorOnCondition(z,NEW_PWD);
                mEnterPwd.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), d));
                buttonEnabled((d==R.color.bg_enter_val));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        mNewSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(testConnectStatus()){
                    mState=NEW_SESSION;
                    updateDisplayOnAsync(true);
                    goAsyncTasks();
                }
                t.testGo();
            }
        });
        mNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(testConnectStatus()){
                    mState=NEW_MEMBER;
                    updateDisplayOnAsync(true);
                    goAsyncTasks();
                }
                t.testGo();
            }
        });
        mNewFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(testConnectStatus()){
                    mState=NEW_FAMILY;
                    updateDisplayOnAsync(true);
                    goAsyncTasks();
                }
                t.testGo();
            }
        });
    }

    public void ShowHidePass(View view){
        if(view.getId()==R.id.show_pass_btn){
            if(mEnterPwd.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.ic_pwd_no_vis);
                //Show Password
                mEnterPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.ic_pwd_vis);

                //Hide Password
                mEnterPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
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
        mNewMember.setText(R.string.splash_button_member_txt);
        mNewFamily.setText(R.string.splash_button_family_txt);
    }

    private void updateDisplayOnAsync(Boolean b){
        int indNet,indClick;
        if (b){
            indNet=View.VISIBLE;
            indClick=View.INVISIBLE;
            frameNetAnim.start();
            mProgressBar.setProgress(1);
        }
        else {
            indNet=View.INVISIBLE;
            indClick=View.VISIBLE;
            frameNetAnim.stop();
            mProgressBar.setProgress(0);
        }
        mNetAnim.setVisibility(indNet);
        mProgressBar.setVisibility(indNet);
        mNewFamily.setVisibility(indClick);
        mNewMember.setVisibility(indClick);
        mNewSession.setVisibility(indClick);
    }

    private Boolean IsLenOK(String s, int min, int max){
        int l=s.length();
        return ((l>min)&&(l<max));
    }

    private Integer getColorOnCondition(String z, Integer state){
        state=state-1;
        int retDraw =0;
        int TRUE=R.color.bg_enter_val;
        int FALSE=R.color.light_red;
        retDraw=((Pattern.matches(REGEX[state],z)&&(IsLenOK(z,MINMAX[state][0],MINMAX[state][1])))?
                TRUE : FALSE);
        Integer err_mess[]={R.string.enter_err_family,R.string.enter_err_member,R.string.enter_err_pwd};
        if (retDraw==FALSE){
        mEnterMessage.setText(getResources().getString(err_mess[state],MINMAX[state][0],MINMAX[state][1]));}
        else {mEnterMessage.setText("");}
        return retDraw;
    }

    private Boolean testConnectStatus(){
        mEnterFamily.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_enter_val));
        mEnterMember.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_enter_val));
        mEnterPwd.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_enter_val));
        Boolean ret=true;
        String message="";
        if(!mSession.IsConnected()){
            ret=false;
            message += getResources().getString(R.string.network_err_no_connection)+"\n";
        }
        mEnterMessage.setText(message);
        return ret;
    }

    // ******************* ASYNC *****************************************

    private void goAsyncTasks() {

        class GoAsyncTasks extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                updateDisplayOnAsync(false);
            }

            @Override
            protected String doInBackground(Void... voids) {
                String s=getFamilyComp();
                mProgressBar.setProgress(2);
                if (s==null){
                    mEnterMessage.setText(R.string.enter_err_com);
                    return null; } else {
                    memberFamily=parseJsonFamily(s); }
                mProgressBar.setProgress(5);
                Integer cas=goFamilyGet();
                Log.d(TAG, "goFamoGet :>"+cas+"<");
                if (cas==0) { return null;}
                if ((cas==NEW_FAMILY)||(cas==NEW_MEMBER)){
                    if (createMember(cas)){
                        mProgressBar.setProgress(10);
                        mEnterMessage.setText(getString(R.string.enter_noerr_new_member));
                    } else {
                        mEnterMessage.setText(getString(R.string.enter_err_new_member));
                        return null;}
                }
                //Boolean test=upload("f819bcc2-ab09-4ed8-8a7c-98fade172da2","f819bcc2-ab09-4ed8-8a7c-98fade172da2");
                //mEnterMessage.setText("Upload result:"+test);
              // fin
                return null;
            }
        }
        GoAsyncTasks getAsync = new GoAsyncTasks();
        getAsync.execute();
    }

    private String getFamilyComp(){
        HashMap<String,String> data = new HashMap<>();
        data.put("family", mFamilyEntered.trim());
        String result = mNetUtils.sendPostRequestJson(mSession.getURLPath()+PHPREQFAMILYCOMP,data);
        return result;
    }

    private Boolean createMember(int flag){
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
        HashMap<String,String> data = new HashMap<>();
        data.put("family", mFamilyEntered.trim());
        data.put("pwd", mPwdEntered.trim());
        data.put("name", mMemberEntered.trim());
        data.put("iduser", UUID.randomUUID().toString().trim());
        String result = mNetUtils.sendPostRequestJson(link,data);
        if (result.trim().equals("1")) {
            Log.d(TAG, "Function createmember succeed");
            return true;
        } else {
            Log.d(TAG, "Function createmember failed");
            return false;
        }
    }
    public Boolean upload(String idrecipeOfPhoto, String idrecipe_destination){
        String link=mSession.getURLPath()+PHPREQUPLOADPHOTO;
        UUID uuid=UUID.fromString(idrecipeOfPhoto);
        CookBook cb=CookBook.get(getApplicationContext());
        Recipe r=cb.getRecipe(uuid);
        File file=cb.getPhotoFile(r);
        Bitmap bitmap=PictureUtils.getBitmap(file.getPath());
        if (bitmap==null){
            Log.d(TAG, "Pas de bitmap !");
            return false;
        }
        String uploadImage = PictureUtils.getStringImage(bitmap);
        HashMap<String,String> data = new HashMap<>();
        data.put("idrecipe", idrecipe_destination);
        data.put("image", uploadImage);
        String result = mNetUtils.sendPostRequestJson(link,data);
        if (!result.trim().equals("1")) {
            Log.d(TAG, "Function upload image failed");
            return false;}
        return true;
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
        mPwdRead="";
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
                mPwdRead=obj.getString("pass");
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
                    mEnterFamily.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_red)); }
                else {
                    mEnterMessage.setText("Family found");
                    User u= new User("","");
                    for(User user:memberFamily){
                        if (user.getName().equals(mMemberEntered)){u=user;}
                    }
                    if (u.getName().equals("")) {
                        mEnterMessage.setText(R.string.enter_err_member_in_family);
                        mEnterMember.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_red));
                    } else {
                        if (!mPwdRead.equals(mPwdEntered.trim())){
                            mEnterMessage.setText(R.string.enter_err_wrong_pass);
                            mEnterPwd.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_red));
                        } else {
                        mEnterMessage.setText("Let us go for a new session");
                        return NEW_SESSION; }
                    }
                }
                break;
            }
            case NEW_FAMILY : {
                if (memberFamily.size()==0) {
                    mEnterMessage.setText("Welcome !");
                    return NEW_FAMILY;
                }
                else {
                    mEnterMessage.setText(R.string.enter_err_family_already);
                    mEnterFamily.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_red));
                }
                break;
            }
            case NEW_MEMBER : {
                if (memberFamily.size()==0) {
                    mEnterMessage.setText(R.string.enter_err_member_wo_family);
                    mEnterFamily.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_red));
                }
                else {
                    // family found
                    User u= new User("","");
                    for(User user:memberFamily){
                        if (user.getName().equals(mMemberEntered)){u=user;}
                    }
                    if (u.getName().equals("")) {
                        if (!mPwdRead.equals(mPwdEntered.trim())){
                            mEnterMessage.setText(R.string.enter_err_wrong_pass);
                            mEnterPwd.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_red));
                        } else {
                        mEnterMessage.setText("Let us go for a new member");
                        return NEW_MEMBER;}
                    } else {
                        mEnterMessage.setText(R.string.enter_err_member_notin_family);
                        mEnterMember.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_red));
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

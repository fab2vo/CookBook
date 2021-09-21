package com.fdx.cookbook;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private ArrayList<Recipe> mRecipes;
    private SessionInfo mSession;
    private NetworkUtils mNetUtils;
    private User mUser;
    private TestConnection tc;
    private int mState;
    private static final String TAG = "CB_SplashActivity";
    private final static int NEW_FAMILY=1;
    private final static int NEW_MEMBER=2;
    private final static int NEW_SESSION=3;
    private final static int NEW_PWD=3;
    private final static Integer MINMAX[][]={{8,45},{1,25},{3,25}}; // min max pour family, member, pwd strings
    private static final String REGEX_FAMILY="[-_!?\\w\\p{javaLowerCase}\\p{javaUpperCase}()\\p{Space}]*";
    private static final String REGEX_MEMBER="[-_\\w\\p{javaLowerCase}\\p{javaUpperCase}]*";
    private static final String REGEX_PWD="[-_!?\\w\\p{javaLowerCase}\\p{javaUpperCase}()]*";
    private static final String REGEX[]={REGEX_FAMILY, REGEX_MEMBER, REGEX_PWD};
    private static final String PHPREQFAMILYCOMP="getfamilycomposition.php";
    private static final String PHPREQNEWMEMBER="createnewmember.php";
    private static final String PHPREQNEWFAMILY="createnewfamily.php";
    private static final String PHPREQUPLOADPHOTO="uploadphotointorecipe.php";
    private static final String PHPREQGETCB="getcookbook.php";
    private static final String PHPREQGETNOTES="getnotes.php";
    private static final String PHPREQGETCOMMENTS="getcomments.php";
    private static final String MYSQLDATEFORMAT="yyyy-MM-dd HH:mm:ss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSession= SessionInfo.get(getApplicationContext());
        if ((!mSession.IsEmpty())&&(!mSession.IsReqNewSession())){
            Intent intent=new Intent(getApplicationContext(), RecipeListActivity.class);
            startActivity(intent);
            finish();
        }
        mNetUtils=new NetworkUtils(getApplicationContext());
        memberFamily=new ArrayList<>();
        mRecipes=new ArrayList<>();
        tc = new TestConnection();
        tc.setTestConnexion(getApplicationContext());
        tc.testGo();

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        mMoto=(TextView) findViewById(R.id.splash_moto);
        mMoto.setText(R.string.P0_moto);
        mProgressBar=(ProgressBar) findViewById(R.id.splash_progBar);
        mProgressBar.setProgress(0);
        mEnterMessage=(TextView) findViewById(R.id.splash_edit_message);
        mEnterMessage.setText("");
        mEnterFamilyLbl=(TextView) findViewById(R.id.splash_edit_family_lbl);
        mEnterFamilyLbl.setText(R.string.P0LF);
        mEnterFamily=(EditText) findViewById(R.id.splash_edit_family);
        mEnterMemberLbl=(TextView) findViewById(R.id.splash_edit_member_lbl);
        mEnterMemberLbl.setText(R.string.P0LM);
        mEnterMember=(EditText) findViewById(R.id.splash_edit_member);
        mEnterPwdLbl=(TextView) findViewById(R.id.splash_edit_pwd_lbl);
        mEnterPwd=(EditText) findViewById(R.id.splash_edit_pwd);
        mEnterPwdLbl.setText(R.string.P0LP);
        mNewSession=(Button) findViewById(R.id.splash_button_session);
        mNewMember=(Button) findViewById(R.id.splash_button_member);
        mNewFamily=(Button) findViewById(R.id.splash_button_family);
        if (mSession.IsReqNewSession()){
            mFamilyEntered=mSession.getUser().getFamily();
            mEnterFamily.setText(mFamilyEntered);
            mMemberEntered=mSession.getUser().getName();
            mEnterMember.setText(mMemberEntered);
            mPwdEntered="";
            mEnterPwd.setText(mPwdEntered);
        } else {
            mFamilyEntered="";
            mEnterFamily.setText(mFamilyEntered);
            mEnterFamily.setHint(R.string.P0HF);
            mMemberEntered="";
            mEnterMember.setText(mMemberEntered);
            mEnterMember.setHint(R.string.P0HM);
            mPwdEntered="";
            mEnterPwd.setText(mPwdEntered);
            mEnterPwd.setHint(R.string.P0HP);
        }
        mEnterMessage.setText("");
        mNewSession.setText(R.string.P0BP);
        mNewMember.setText(R.string.P0BM);
        mNewFamily.setText(R.string.P0BF);
        //updateTop();
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
                } else {
                tc.testGo();}
            }
        });
        mNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(testConnectStatus()){
                    mState=NEW_MEMBER;
                    updateDisplayOnAsync(true);
                    goAsyncTasks();
                } else {
                tc.testGo();}
            }
        });
        mNewFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(testConnectStatus()){
                    mState=NEW_FAMILY;
                    updateDisplayOnAsync(true);
                    goAsyncTasks();
                } else {
                tc.testGo(); }
            }
        });
    }

    public void ShowHidePass(View view){
        if(view.getId()==R.id.show_pass_btn){
            if(mEnterPwd.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.ic_pwd_no_vis);
                mEnterPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.ic_pwd_vis);
                mEnterPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
    }
    /*private void updateTop(){
        mEnterFamilyLbl.setText(R.string.P0LF);
        mEnterFamily.setText(mFamilyEntered);
        mEnterMemberLbl.setText(R.string.P0LM);
        mEnterMember.setText(mMemberEntered);
        mEnterPwdLbl.setText(R.string.P0LP);
        mEnterPwd.setText(mPwdEntered);
        mEnterMessage.setText("");
        mNewSession.setText(R.string.P0BP);
        mNewMember.setText(R.string.P0BM);
        mNewFamily.setText(R.string.P0BF);
    }*/

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
        Integer err_mess[]={R.string.P0ERUF,R.string.P0RUM,R.string.P0RUP};
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
        //String message="";
        if(!mSession.IsConnected()){
            ret=false;
            Toast.makeText(getApplicationContext(), getString(R.string.P0ER_nocon), Toast.LENGTH_LONG).show();
            //message += getResources().getString(R.string.P0ER_nocon)+"\n";
        }
        //mEnterMessage.setText(message);
        return ret;
    }

    private void buttonEnabled(Boolean b){
        mNewSession.setEnabled(b);
        mNewMember.setEnabled(b);
        mNewFamily.setEnabled(b);
    }

    /******************************************************************************************
    *                            ASYNC                                                        *
    *******************************************************************************************/

    private void goAsyncTasks() {

        class GoAsyncTasks extends AsyncTask<Void, Void, Boolean> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Boolean b) {
                super.onPostExecute(b);
                updateDisplayOnAsync(false);
                if (b) {
                    mSession.setStoredUser(mUser);
                    Intent intent=new Intent(getApplicationContext(), RecipeListActivity.class);
                    startActivity(intent);
                    finish();
                }
                return;
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                CookBook cb=CookBook.get(getApplicationContext());
                String s=getFamilyComp();
                mProgressBar.setProgress(2);
                if (s==null){
                    mEnterMessage.setText(R.string.P0ER_com);
                    return false; } else {
                    memberFamily=parseJsonFamily(s); }
                mProgressBar.setProgress(5);
                Integer cas=goFamilyGet();
                if (cas==0) { return false;}
                if ((cas==NEW_FAMILY)||(cas==NEW_MEMBER)){
                    if (createMember(cas)){
                        mProgressBar.setProgress(10);
                        mEnterMessage.setText(getString(R.string.P0OKM));
                    } else {
                        mEnterMessage.setText(getString(R.string.P0ERM_new));
                        return false;}
                }
                cb.clearCookBook();
                s=downloadCB();
                if (s==null){
                    //fdx Log(TAG, "download cookbook failed");
                    return false;
                }
                mProgressBar.setProgress(50);
                mRecipes=parseJsonCB(s);
                if (!downloadNotesAndParse()){
                    //fdx Log(TAG, "Failure in reading and parsing Notes");
                    return false;
                }
                mProgressBar.setProgress(70);
                if (!downloadCommentsAndParse()){
                    //fdx Log(TAG, "Failure in reading and parsing Comments");
                    return false;
                }
                mProgressBar.setProgress(70);
                cb.fillCookBook(mRecipes);
                mProgressBar.setProgress(90);
                return true;
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
                //fdx Log(TAG, "Function createmember flag bad value");
                return false;
            }
        }
        HashMap<String,String> data = new HashMap<>();
        data.put("family", mFamilyEntered.trim());
        data.put("pwd", mPwdEntered.trim());
        data.put("name", mMemberEntered.trim());
        mUser=new User(mFamilyEntered.trim(),mMemberEntered.trim());
        data.put("iduser", mUser.getId().toString().trim());
        String result = mNetUtils.sendPostRequestJson(link,data);
        if (result.trim().equals("1")) {
            //fdx Log(TAG, "Function createmember succeed");
            return true;
        } else {
            //fdx Log(TAG, "Function createmember failed");
            return false;
        }
    }

    private String downloadCB(){
        HashMap<String,String> data = new HashMap<>();
        data.put("iduser", mUser.getId().toString().trim());
        data.put("pwd", mPwdEntered.trim());
        String result = mNetUtils.sendPostRequestJson(mSession.getURLPath()+PHPREQGETCB,data);
        return result;
    }

    private Boolean downloadNotesAndParse(){
        HashMap<String,String> data = new HashMap<>();
        data.put("iduser", mUser.getId().toString().trim());
        String json = mNetUtils.sendPostRequestJson(mSession.getURLPath()+PHPREQGETNOTES,data);
        UUID uuid;
        Note n;
        try {
            JSONArray jarr1=new JSONArray(json);
            for (int i=0; i<jarr1.length(); i++){
                JSONObject obj = jarr1.getJSONObject(i);
                uuid=UUID.fromString(obj.getString("id_recipe"));
                n=mNetUtils.parseObjectNote(obj);
                for(Recipe r:mRecipes){
                    if (r.getId().equals(uuid)){
                        r.addNote(n);
                        break;
                    }
                }
            }
        } catch (Exception e){
                //fdx Log(TAG, "Failure in parsing JSON Notes "+e);
                return false;
            }
        return true;
    }

    private Boolean downloadCommentsAndParse(){
        HashMap<String,String> data = new HashMap<>();
        data.put("iduser", mUser.getId().toString().trim());
        String json = mNetUtils.sendPostRequestJson(mSession.getURLPath()+PHPREQGETCOMMENTS,data);
        UUID uuid;
        Comment c;
        try {
            JSONArray jarr1=new JSONArray(json);
            for (int i=0; i<jarr1.length(); i++){
                JSONObject obj = jarr1.getJSONObject(i);
                c=mNetUtils.parseObjectComment(obj);
                uuid=UUID.fromString(obj.getString("id_recipe"));
                if (c!=null) {
                    for(Recipe r:mRecipes){
                        if (r.getId().equals(uuid)){
                            r.addComment(c);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e){
            //fdx Log(TAG, "Failure in parsing JSON Array Comments "+e);
            return false;
        }
        return true;
    }

    public ArrayList<User> parseJsonFamily(String json){
        ArrayList<User> ret=new ArrayList<>();
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
                date=new SimpleDateFormat(MYSQLDATEFORMAT).parse(dateString);
                u.setDate(date);
                ret.add(u);
                mPwdRead=obj.getString("pass");
            }
        }
        catch (Exception e){
            //fdx Log(TAG, "Failure in parsing JSON FamilyGet" +e);
        }
        return ret;
    }
    public ArrayList<Recipe> parseJsonCB(String json){
        ArrayList<Recipe> result=new ArrayList<>();
        Recipe r;
        try {
            JSONArray jarr1 = new JSONArray(json);
            for (int j = 0; j < jarr1.length(); j++) {
                JSONObject obj = jarr1.getJSONObject(j);
                r = new Recipe(UUID.fromString(obj.getString("id_recipe")));
                if (mNetUtils.parseObjectRecipe(r,obj, true, true)) result.add(r);
            }
        } catch (Exception e) {
            //fdx Log(TAG, " Error parsing CB" + e);
        }
        return result;
    }

    private Integer goFamilyGet(){
        switch(mState){
            case NEW_SESSION : {
                if (memberFamily.size()==0) {
                    mEnterMessage.setText(R.string.P0ERM_nofam);
                    mEnterFamily.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_red)); }
                else {
                    //mEnterMessage.setText(R.string.P0OKF);
                    User u= new User("","");
                    for(User user:memberFamily){
                        if (user.getName().equals(mMemberEntered)){u=user;}
                    }
                    if (u.getName().equals("")) {
                        mEnterMessage.setText(R.string.P0ERF_nomem);
                        mEnterMember.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_red));
                    } else {
                        if (!mPwdRead.equals(mPwdEntered.trim())){
                            mEnterMessage.setText(R.string.P0ERP_wrong);
                            mEnterPwd.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_red));
                        } else {
                            mUser=u;
                        mEnterMessage.setText(getString(R.string.P0OKP, u.getName()));
                        return NEW_SESSION; }
                    }
                }
                break;
            }
            case NEW_FAMILY : {
                if (memberFamily.size()==0) {
                    mEnterMessage.setText(R.string.P0OK_done);
                    return NEW_FAMILY;
                }
                else {
                    mEnterMessage.setText(R.string.P0ERF_notnew);
                    mEnterFamily.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_red));
                }
                break;
            }
            case NEW_MEMBER : {
                if (memberFamily.size()==0) {
                    mEnterMessage.setText(R.string.P0ERM_nofam);
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
                            mEnterMessage.setText(R.string.P0ERP_wrong);
                            mEnterPwd.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_red));
                        } else {
                        mEnterMessage.setText(R.string.P0OKM);
                        return NEW_MEMBER;}
                    } else {
                        mEnterMessage.setText(R.string.P0ERM_notnew);
                        mEnterMember.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_red));
                    }
                }
                break;
            }
            default : {
                //fdx Log(TAG, "Switch case on mState anomaly : "+mState);
            }
        }
        return 0;
    }
}

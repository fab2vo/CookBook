package com.fdx.cookbook;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

public class RecipeDisplayFragment extends Fragment {
    private static final String ARG_RECIPE_ID="recipe_id";
    private static final String TAG = "CB_RecipeDisplayFrag";
    private Recipe mRecipe;
    private File mPhotoFile;
    private int mStepNb;
    private int mIngNb;
    private String newcomment;
    private String DEFAULT_URL="https://cookbookfamily.cloud/cb/ix.php";
    private SessionInfo mSession;
    private ImageView mDPhotoView;
    private TextView mDTitleText;
    private RatingBar mDRatingBar;
    private TextView mDRatingBarText;
    private TextView mDAuthorText;
    private TextView mDDifficulty;
    private TextView mDType;
    private ImageView mSunIcon;
    private ImageView mIceIcon;
    private TextView mDSourceText;
    private TextView mDSourceUrl;
    private TextView mDIngTitle;
    private TextView mDStepTitle;
    private TextView mDSourceTitle;
    private TextView mDComTitle;
    private String mToFamily;
    private String mToMember;
    private String mToMessage;
    private TextView[] mDStepText;
    private TextView[] mDIngText;
    private TextView[] mDComText;
    private EditText mDNexComment;
    private ImageView mDEnterComment;
    private ScrollView mScroll;
    private final static Integer MINMAX[][]={{6,45},{2,25},{3,25}}; // min max pour family, member, pwd strings
    private static final String REGEX_FAMILY="[-_!?\\w\\p{javaLowerCase}\\p{javaUpperCase}()\\p{Space}]*";
    private static final String REGEX_MEMBER="[-_\\w\\p{javaLowerCase}\\p{javaUpperCase}]*";

    public static RecipeDisplayFragment newInstance(UUID recipeId) {
        Bundle args=new Bundle();
        args.putSerializable(ARG_RECIPE_ID, recipeId);
        RecipeDisplayFragment fragment=new RecipeDisplayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipe=new Recipe();
        UUID recipeId=(UUID) getArguments().getSerializable(ARG_RECIPE_ID);
        mRecipe=CookBook.get(getActivity()).getRecipe(recipeId);
        mPhotoFile=CookBook.get(getActivity()).getPhotoFile(mRecipe);
        mSession= SessionInfo.get(getActivity());
        setHasOptionsMenu(true);
        mStepNb=mRecipe.getNbStep();
        mIngNb=mRecipe.getNbIng();
        mToFamily="";
        mToMember="";
    }
    @Override
    public void onPause(){
        super.onPause();
        //todo pourquoi update recipe ?
        CookBook.get(getActivity()).updateRecipe(mRecipe);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_recipe_display, menu);
        MenuItem menuItem = menu.findItem(R.id.recipe_menu_edit);
        if(!mRecipe.getOwner().getId().equals(mSession.getUser().getId())){
            menuItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.recipe_menu_report:
                Intent i=new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getRecipeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.P2MU_send));
                startActivity(i);
                return true;
            case R.id.recipe_menu_edit:
                Intent intent= RecipeActivity.newIntent(getActivity(),mRecipe.getId());
                startActivity(intent);
                return true;
            case R.id.recipe_menu_html:
                String s= mSession.getURLPath()+"ix.php";
                // todo later : find pknum  and then add ?recipenum=pknum
                Uri uri = Uri.parse(s);
                Intent intent4 = new Intent(Intent.ACTION_VIEW, uri);
                if (intent4.resolveActivity(mSession.getContext().getPackageManager()) != null) {
                    startActivity(intent4);
                }
                return true;
            case R.id.recipe_mail:
                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText familyBox = new EditText(getContext());
                familyBox.setHint(getString(R.string.P0HF));
                layout.addView(familyBox);
                final EditText memberBox = new EditText(getContext());
                memberBox.setHint(getString(R.string.P0HM));
                layout.addView(memberBox);
                final EditText messageBox = new EditText(getContext());
                messageBox.setHint(getString(R.string.P4M_mes));
                layout.addView(messageBox);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.P4M_title));
                builder.setView(layout);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mToFamily = familyBox.getText().toString();
                        mToMember = memberBox.getText().toString();
                        mToMessage = messageBox.getText().toString();
                        Boolean b=(Pattern.matches(REGEX_FAMILY,mToFamily));
                        b=b&&(Pattern.matches(REGEX_MEMBER,mToMember));
                        b=b&&(IsLenOK(mToFamily,MINMAX[0][0],MINMAX[0][1]));
                        b=b&&(IsLenOK(mToMember,MINMAX[1][0],MINMAX[1][1]));
                        b=b&&(Pattern.matches(REGEX_FAMILY,mToMessage));
                        if (b) {
                        sendMailAsync sendmail = new sendMailAsync();
                        sendmail.execute();
                        } else {
                            Toast.makeText(getActivity(),getString(R.string.P4M_err), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                return true;
            case R.id.recipe_menu_delete:
                CookBook.get(getActivity()).markRecipeToDelete(mRecipe);
                getActivity().onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View v=inflater.inflate(R.layout.fragment_display_recipe, container, false);
        mScroll=(ScrollView) v.findViewById(R.id.fragment_recipe_scroll);
        mDTitleText =(TextView) v.findViewById(R.id.recipe_display_title);
        mDPhotoView=(ImageView) v.findViewById(R.id.recipe_display_photo);
        updatePhotoView();
        mDRatingBar=(RatingBar) v.findViewById(R.id.recipe_display_ratingBar);
        mDRatingBarText=(TextView) v.findViewById(R.id.recipe_display_ratingBar_txt);
        mDDifficulty=(TextView) v.findViewById(R.id.recipe_display_difficulty);
        mDType=(TextView) v.findViewById(R.id.recipe_display_type);
        mSunIcon=(ImageView) v.findViewById(R.id.recipe_img_sun) ;
        mIceIcon=(ImageView) v.findViewById(R.id.recipe_img_ice) ;
        mDAuthorText=(TextView) v.findViewById(R.id.recipe_display_author);
        mDSourceText=(TextView) v.findViewById(R.id.recipe_display_source);
        mDSourceUrl=(TextView) v.findViewById(R.id.recipe_display_source_url);
        mDIngTitle=(TextView) v.findViewById(R.id.recipe_display_title_ing);
        mDStepTitle=(TextView) v.findViewById(R.id.recipe_display_title_step);
        mDSourceTitle=(TextView) v.findViewById(R.id.recipe_display_source_title);
        mDComTitle=(TextView)v.findViewById(R.id.recipe_display_comment_title);
        final int[] rIngID= {R.id.recipe_display_I01,R.id.recipe_display_I02,R.id.recipe_display_I03,
                R.id.recipe_display_I04,R.id.recipe_display_I05,R.id.recipe_display_I06,
                R.id.recipe_display_I07,R.id.recipe_display_I08,R.id.recipe_display_I09,
                R.id.recipe_display_I10,R.id.recipe_display_I11,R.id.recipe_display_I12,
                R.id.recipe_display_I13,R.id.recipe_display_I14,R.id.recipe_display_I15};
        mDIngText= new TextView[mRecipe.getNbIngMax()];
        for(int i=0;i<mRecipe.getNbIngMax();i++) {
            mDIngText[i] = (TextView) v.findViewById(rIngID[i]);
        }
        final int[] rID= {R.id.recipe_display_S1,R.id.recipe_display_S2,R.id.recipe_display_S3,
                R.id.recipe_display_S4,R.id.recipe_display_S5,R.id.recipe_display_S6,
                R.id.recipe_display_S7,R.id.recipe_display_S8,R.id.recipe_display_S9};
        mDStepText=new TextView[mRecipe.getNbStepMax()];
        for(int i=0;i<mRecipe.getNbStepMax();i++) {
            mDStepText[i] = (TextView) v.findViewById(rID[i]);
        }
        final int[] rComID= {R.id.recipe_display_C01,R.id.recipe_display_C02,R.id.recipe_display_C03,
                R.id.recipe_display_C04,R.id.recipe_display_C05,R.id.recipe_display_C06,
                R.id.recipe_display_C07,R.id.recipe_display_C08,R.id.recipe_display_C09,
                R.id.recipe_display_C10,R.id.recipe_display_C11,R.id.recipe_display_C12,
                R.id.recipe_display_C13,R.id.recipe_display_C14,R.id.recipe_display_C15,
                R.id.recipe_display_C16,R.id.recipe_display_C17,R.id.recipe_display_C18,
                R.id.recipe_display_C19,R.id.recipe_display_C20};
        mDComText=new TextView[mRecipe.getNbComMax()];
        for(int i=0;i<mRecipe.getNbComMax();i++) {
            mDComText[i] = (TextView) v.findViewById(rComID[i]);
        }
        updateUI();
        mDNexComment=(EditText) v.findViewById(R.id.recipe_display_enter_comment);
        mDNexComment.setText("");
        mDNexComment.setHint(getString(R.string.P2C_hint));
        newcomment="";
        //todo test comment versus pattern
        mDNexComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newcomment=s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDEnterComment=(ImageView) v.findViewById(R.id.recipe_img_enter);
        mDEnterComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!newcomment.equals(""))&&(!newcomment.equals("..."))) {
                    mRecipe.addComment(new Comment(newcomment, mSession.getUser()));
                    mRecipe.updateTS(AsynCallFlag.NEWCOMMENT,true);
                    mDNexComment.setText("...");
                updateUI();
                }
            }
        });
        return v;
    }
    private void updatePhotoView(){
        if (mPhotoFile==null || !mPhotoFile.exists()){
            mDPhotoView.setImageDrawable(getResources().getDrawable(R.drawable.ic_recipe_camera));
        } else {
            Bitmap bitmap=PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mDPhotoView.setImageBitmap(bitmap);
        }
    }
    private void updateUI(){
        String s,s2;
        User u=mRecipe.getOwner();
        int ingMax=mRecipe.getNbIngMax();
        int stepMax=mRecipe.getNbStepMax();
        int comMax=mRecipe.getNbComMax();
        int NbCom=mRecipe.getComments().size();
        int gone=View.GONE, visible=View.VISIBLE;
        DecimalFormat df = new DecimalFormat("#.#");
        mDTitleText.setText(mRecipe.getTitle());
        mDRatingBar.setRating((float) mRecipe.getNoteAvg());
        mDRatingBarText.setText(df.format(mRecipe.getNoteAvg())+"  ("+ mRecipe.getNotes().size()+")");
        s=getString(R.string.P2U_txt,u.getName(),u.getFamily());
        int idiff= RecipeDifficulty.getIndex(mRecipe.getDifficulty());
        String[] stringArrayDiff = getResources().getStringArray(R.array.recipe_difficulty_array);
        mDDifficulty.setText(stringArrayDiff[idiff]);
        int ityp= RecipeType.getIndex(mRecipe.getType());
        String[] stringArrayTyp = getResources().getStringArray(R.array.recipe_type_array);
        mDType.setText(stringArrayTyp[ityp]);
        mSunIcon.setImageResource((mRecipe.IsSummer()) ? R.drawable.ic_recipe_sun : R.drawable.ic_recipe_sun_disabled);
        mIceIcon.setImageResource((mRecipe.IsWinter()) ? R.drawable.ic_recipe_ice : R.drawable.ic_recipe_ice_disabled);
        mDAuthorText.setText(s);
        // source
        s=mRecipe.getSource();
        s2=mRecipe.getSource_url_name();
        if (s.equals("")) mDSourceText.setVisibility(gone);
        else mDSourceText.setText(s);
        if ((s2.equals(DEFAULT_URL)) || (s2.equals(""))) {
            mDSourceUrl.setVisibility(gone);
            if (s.equals(""))  mDSourceTitle.setVisibility(gone);
        }
        else mDSourceUrl.setText(s2);
        // ingredients
        s=getString(R.string.P2IT, ""+mRecipe.getNbPers());
        if (mRecipe.getNbIng()>0) mDIngTitle.setText(s); else mDIngTitle.setVisibility(gone);
        for(int i=0;i<ingMax;i++){
            if (mIngNb>0){mDIngText[i].setText("- "+mRecipe.getIngredient(i+1));}
            if (i>=0){mDIngText[i].setVisibility((mIngNb>i)? visible:gone);}
        }
        // steps
        if (mRecipe.getNbStep()==0)  mDStepTitle.setVisibility(gone);
        for(int i=0;i<stepMax;i++){
            if (mStepNb>0){mDStepText[i].setText((i+1)+". "+mRecipe.getStep(i+1));}
            if (i>=0){mDStepText[i].setVisibility((mStepNb>i)? visible:gone);}
        }
        // comments
        s=getString(R.string.P2CT, ""+mRecipe.getComments().size());
        mDComTitle.setText(s);
        for(int i=0;i<comMax;i++){
            if (NbCom>0){
                if (i<NbCom) {mDComText[i].setText("- "+mRecipe.getComment(NbCom-i-1).toTxt());}
                else {mDComText[i].setText("");}
            }
            if (i>=0){mDComText[i].setVisibility((NbCom>i)? visible:gone);}
        }
    }
    private Boolean IsLenOK(String s, int min, int max){
        int l=s.length();
        return ((l>=min)&&(l<=max));
    }
    private String getRecipeReport(){
        String report;
        Integer iplus;
        report =getString(R.string.P2RE_title, mRecipe.getTitle())+"\n";
        report +=getString(R.string.P2RE_user,mRecipe.getOwner().getNameComplete())+"\n";
        if(!mRecipe.getSource().equals("")){
            report +=  getString(R.string.P2RE_src, mRecipe.getSource())+"\n";
        }
        if(!mRecipe.getSource_url_name().equals("")){
            report +=  getString(R.string.P2RE_url, mRecipe.getSource_url_name())+"\n";
        }
        if (mRecipe.getNbIng()>0){
            report +=getString(R.string.P1R_ing)+"\n";
            for(int i=0;i<mRecipe.getNbIng();i++){
                report += getString(R.string.P2RE_ing, mRecipe.getIngredient(i+1))+"\n";
            }
        }
        if (mRecipe.getNbStep()>0) {
            report +=getString(R.string.P1R_step)+"\n";
            for (int i = 0; i < mRecipe.getNbStep(); i++) {
                iplus = i + 1;
                report += getString(R.string.P2RE_step, iplus + "",
                        mRecipe.getStep(i + 1)) + "\n";
            }
        }
        report +=getString(R.string.P2RE_end);
        return report;
    }

    /******************************************************************************************
     *                            ASYNC                                                        *
     *******************************************************************************************/


    class sendMailAsync extends AsyncTask<Void, Void, Boolean> {
        private static final String PHP204 = "return204.php";
        private static final String MYSQLDATEFORMAT="yyyy-MM-dd HH:mm:ss";
        private static final String PHPSENDMAIL = "sendmail.php";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(),getString(R.string.P4_start), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b) {
                Toast.makeText(getActivity(), getString(R.string.P4_OK,mToMember,mToFamily), Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getActivity(), getString(R.string.P4_fail), Toast.LENGTH_LONG).show();
            }
         }

        @Override
        protected Boolean doInBackground(Void... voids) {
            NetworkUtils mNetUtils = new NetworkUtils(getContext());
            if (!test204()) {
                return false;
            }
            HashMap<String, String> data = new HashMap<>();
            data.put("idrecipe", mRecipe.getId().toString());
            if ((mToFamily==null)||(mToFamily.length()<3)) return false;
            data.put("family",mToFamily.trim());
            if ((mToMember==null)||(mToMember.length()<1)) return false;
            data.put("membre", mToMember.trim());
            data.put("idfrom", mSession.getUser().getId().toString());
            if (mToMessage==null) return false;
            data.put("message", mToMessage.trim());
            mSession.fillPwd(data,false);
            String result = mNetUtils.sendPostRequestJson(mSession.getURLPath() + PHPSENDMAIL, data);
            if (result==null) return false;
            if (!result.trim().equals("1")) {
                return false;}
            return true;
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
                return false;
            }
        }

    }

}
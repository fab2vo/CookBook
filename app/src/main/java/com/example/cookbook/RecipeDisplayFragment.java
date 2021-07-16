package com.example.cookbook;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.UUID;

public class RecipeDisplayFragment extends Fragment {
    private static final String ARG_RECIPE_ID="recipe_id";
    private static final String TAG = "CB_RecipeDisplayFrag";
    private static final int REQUEST_PHOTO= 2;
    private Recipe mRecipe;
    private File mPhotoFile;
    private int mStepNb;
    private int mIngNb;
    private String newcomment;
    private String DEFAULT_URL="https://wwww.cookbookfamily.com";

    private SessionInfo mSession;
    private ImageView mDPhotoView;
    private TextView mDTitleText;
    private RatingBar mDRatingBar;
    private TextView mDRatingBarText;
    private TextView mDAuthorText;
    private TextView mDDifficulty;
    private ImageView mSunIcon;
    private ImageView mIceIcon;
    private TextView mDSourceText;
    private TextView mDSourceUrl;
    private TextView mDIngTitle;
    private TextView mDComTitle;
    private TextView[] mDStepText;
    private TextView[] mDIngText;
    private TextView[] mDComText;
    private EditText mDNexComment;
    private ImageView mDEnterComment;
    private ScrollView mScroll;

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
    }
    @Override
    public void onPause(){
        super.onPause();
        CookBook.get(getActivity()).updateRecipe(mRecipe);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_recipe_display, menu);
        MenuItem menuItem = menu.findItem(R.id.recipe_menu_edit);
        if(!mRecipe.getOwner().getId().equals(mSession.getUser().getId())){
            //getActivity().invalidateOptionsMenu();
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
                i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.recipe_report_subject));
                startActivity(i);
                return true;
            case R.id.recipe_menu_edit:
                Intent intent= RecipeActivity.newIntent(getActivity(),mRecipe.getId());
                startActivity(intent);
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
        mSunIcon=(ImageView) v.findViewById(R.id.recipe_img_sun) ;
        mIceIcon=(ImageView) v.findViewById(R.id.recipe_img_ice) ;
        mDAuthorText=(TextView) v.findViewById(R.id.recipe_display_author);
        mDSourceText=(TextView) v.findViewById(R.id.recipe_display_source);
        mDSourceUrl=(TextView) v.findViewById(R.id.recipe_display_source_url);
        mDIngTitle=(TextView) v.findViewById(R.id.recipe_display_title_ing);
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
        mDNexComment.setHint(getString(R.string.recipe_comment_hint));
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
                mRecipe.addComment(new Comment(newcomment, mSession.getUser()));
                mRecipe.updateTS(AsynCallFlag.NEWCOMMENT,true);
                mDNexComment.setText("...");
                updateUI();
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
        String s;
        User u=mRecipe.getOwner();
        int ingMax=mRecipe.getNbIngMax();
        int stepMax=mRecipe.getNbStepMax();
        int comMax=mRecipe.getNbComMax();
        int NbCom=mRecipe.getComments().size();
        int gone=View.GONE;
        int visible=View.VISIBLE;
        DecimalFormat df = new DecimalFormat("#.#");
        mDTitleText.setText(mRecipe.getTitle());
        mDRatingBar.setRating((float) mRecipe.getNoteAvg());
        mDRatingBarText.setText(df.format(mRecipe.getNoteAvg())+"  ("+ mRecipe.getNotes().size()+")");
        s=getString(R.string.recipe_display_author,u.getName(),u.getFamily());
        int idiff= RecipeDifficulty.getIndex(mRecipe.getDifficulty());
        String[] stringArrayDiff = getResources().getStringArray(R.array.recipe_difficulty_array);
        mDDifficulty.setText(stringArrayDiff[idiff]);
        mSunIcon.setImageResource((mRecipe.IsSummer()) ? R.drawable.ic_recipe_sun : R.drawable.ic_recipe_sun_disabled);
        mIceIcon.setImageResource((mRecipe.IsWinter()) ? R.drawable.ic_recipe_ice : R.drawable.ic_recipe_ice_disabled);
        mDAuthorText.setText(s);
        mDSourceText.setText(mRecipe.getSource());
        if (mRecipe.getSource_url_name().equals(DEFAULT_URL)) {
            mDSourceUrl.setText(""); }
        else {mDSourceUrl.setText(mRecipe.getSource_url_name());}
        s=getString(R.string.recipe_display_title_ing, ""+mRecipe.getNbPers());
        mDIngTitle.setText(s);
        for(int i=0;i<ingMax;i++){
            if (mIngNb>0){mDIngText[i].setText("- "+mRecipe.getIngredient(i+1));}
            if (i>=0){mDIngText[i].setVisibility((mIngNb>i)? visible:gone);}
        }
        for(int i=0;i<stepMax;i++){
            if (mStepNb>0){mDStepText[i].setText((i+1)+". "+mRecipe.getStep(i+1));}
            if (i>=0){mDStepText[i].setVisibility((mStepNb>i)? visible:gone);}
        }
        s=getString(R.string.recipe_display_title_comment, ""+mRecipe.getComments().size());
        mDComTitle.setText(s);
        for(int i=0;i<comMax;i++){
            if (NbCom>0){
                if (i<NbCom) {mDComText[i].setText("- "+mRecipe.getComment(NbCom-i-1).toTxt());}
                else {mDComText[i].setText("");}
            }
            if (i>=0){mDComText[i].setVisibility((NbCom>i)? visible:gone);}
        }
    }
    private String getRecipeReport(){
        String report;
        Integer iplus;
        report =getString(R.string.recipe_report_title, mRecipe.getTitle())+"\n";
        report +=getString(R.string.recipe_report_owner,mRecipe.getOwner().getNameComplete())+"\n";
        //String dateFormat = "dd MMM yyyy";
        //String dateString=DateFormat.format(dateFormat,mRecipe.getDate()).toString();
        if(!mRecipe.getSource_url_name().equals("")){
            report += getString(R.string.recipe_report_url, mRecipe.getSource_url_name())+"\n";
        }
        for(int i=0;i<mRecipe.getNbIng();i++){
            report += getString(R.string.recipe_report_ing, mRecipe.getIngredient(i+1))+"\n";
        }
        for(int i=0;i<mRecipe.getNbStep();i++){
            iplus=i+1;
            report += getString(R.string.recipe_report_step, iplus+"",
                    mRecipe.getStep(i+1))+"\n";
        }
        report +=getString(R.string.recipe_report_final);
        return report;
    }

}

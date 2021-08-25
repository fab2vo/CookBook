package com.fdx.cookbook;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RecipeEditFragment extends Fragment {
    // Constantes
    private static final String ARG_RECIPE_ID="recipe_id";
    private static final String DIALOG_DATE="DialogDate";
    private static final int REQUEST_DATE=0;
    private static final int REQUEST_PHOTO= 2;
    private static final String TAG = "CB_RecipeEditFragment";
    private static final String FPROVIDER="com.fdx.cookbook.fileprovider";
    private static final String UUIDNULL="00000000-0000-0000-0000-000000000000";
    // Variables internes
    private Recipe mRecipe;
    private Recipe mRecipeInit;
    private UUID mRecipeId;
    private File mPhotoFile;
    private EditText mTitleField;
    private EditText mSourceField;
    private EditText mSourceUrl;
    private EditText mNbPersField;
    //    private Button mDateButton;
    private TextView[] mSTextView;
    private EditText mNewStepField;
    private ImageButton mNewStepEnter;
    private ImageButton mNewStepBack;
    private int mStepNb;
    private TextView[] mSTextIngView;
    private EditText mNewIngField;
    private ImageButton mNewIngEnter;
    private ImageButton mNewIngBack;
    private int mIngNb;
    private Spinner mSeasonSpinner;
    private Spinner mDifficultySpinner;
    private ScrollView mScroll;
    private ImageView mPhotoView;

    public static RecipeEditFragment newInstance(UUID recipeId){
        Bundle args=new Bundle();
        args.putSerializable(ARG_RECIPE_ID, recipeId);
        RecipeEditFragment fragment=new RecipeEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mRecipe=new Recipe();
        mRecipeInit=new Recipe();
        mRecipeId=(UUID) getArguments().getSerializable(ARG_RECIPE_ID);
        setHasOptionsMenu(true);
        SessionInfo session=SessionInfo.get(getActivity());
        if (!IsRecipeNew(mRecipeId)) {
            mRecipe=CookBook.get(getActivity()).getRecipe(mRecipeId);
            mRecipeInit=CookBook.get(getActivity()).getRecipe(mRecipeId);
            mPhotoFile=CookBook.get(getActivity()).getPhotoFile(mRecipe);
        } else {
            mRecipe.setOwner(session.getUser());
            mRecipeInit.setOwner(session.getUser());
            mRecipeInit.setId(mRecipe.getId());
        }
        mStepNb=mRecipe.getNbStep();
        mIngNb=mRecipe.getNbIng();
    }

    @Override
    public void onPause(){
        super.onPause();
        //mRecipe.updateTS(AsynCallFlag.NEWRECIPE, !mRecipe.hasNotChangedSince(mRecipeInit));
        //CookBook.get(getActivity()).updateRecipe(mRecipe);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_recipe, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.recipe_menu_done:
                if (mRecipe.getTitle().length()>0){
                    if (!mRecipe.hasNotChangedSince(mRecipeInit)){
                    mRecipe.updateTS(AsynCallFlag.NEWRECIPE, true);}
                    if (IsRecipeNew(mRecipeId)){
                        CookBook.get(getActivity()).addRecipe(mRecipe);
                    } else {
                        CookBook.get(getActivity()).updateRecipe(mRecipe);
                    }
                }
                getActivity().onBackPressed();
                return true;
            case R.id.recipe_menu_delete:
                if (!IsRecipeNew(mRecipeId)){
                    CookBook.get(getActivity()).markRecipeToDelete(mRecipe);
                }
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View v=inflater.inflate(R.layout.fragment_recipe_edit, container, false);
        mScroll=(ScrollView) v.findViewById(R.id.fragment_recipe_scroll);
        PackageManager packageManager=getActivity().getPackageManager();
            final Intent captureImage=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            boolean canTakePhoto=(mPhotoFile != null) &&
                    (captureImage.resolveActivity(packageManager)!=null);
        mPhotoView=(ImageView) v.findViewById(R.id.recipe_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri= FileProvider.getUriForFile(getActivity(),
                        FPROVIDER, mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities=getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for(ResolveInfo activity : cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        updatePhotoView();
        mTitleField= (EditText) v.findViewById(R.id.recipe_title);
        mTitleField.setText(mRecipe.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRecipe.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //blank
            }
        });

        mSourceField= (EditText) v.findViewById(R.id.recipe_source);
        mSourceField.setText(mRecipe.getSource());
        mSourceField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRecipe.setSource(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSourceUrl=(EditText) v.findViewById(R.id.recipe_source_url);
        mSourceUrl.setText(mRecipe.getSource_url().toString());
        mSourceUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {} else {
                    try {
                        URL url=new URL(s.toString());
                        mRecipe.setSource_url(url);
                    } catch (MalformedURLException e) {
                        //fdx Log(TAG, "onTextChanged de mSource_url >" +s.toString()+"< Failed >"+ e);
                    }
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mNbPersField= (EditText) v.findViewById(R.id.recipe_nbpers);
        mNbPersField.setText(mRecipe.getNbPers()+"");
        mNbPersField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {} else {
                    int nb_entered = Integer.parseInt(s.toString());
                    if ((nb_entered > 0) && (nb_entered < 13)) {
                        mRecipe.setNbPers(nb_entered);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        mSeasonSpinner= (Spinner) v.findViewById(R.id.recipe_season);
        ArrayAdapter<CharSequence> adapterSeason = ArrayAdapter.createFromResource(getContext(),
                R.array.recipe_season_array, android.R.layout.simple_spinner_item);
        mSeasonSpinner.setAdapter(adapterSeason);
        mSeasonSpinner.setSelection(RecipeSeason.getIndex(mRecipe.getSeason()));
        mSeasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRecipe.setSeason(RecipeSeason.getSeason(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mDifficultySpinner= (Spinner) v.findViewById(R.id.recipe_difficulty);
        ArrayAdapter<CharSequence> adapterDifficulty = ArrayAdapter.createFromResource(getContext(),
                R.array.recipe_difficulty_array, android.R.layout.simple_spinner_item);
        mDifficultySpinner.setAdapter(adapterDifficulty);
        mDifficultySpinner.setSelection(RecipeDifficulty.getIndex(mRecipe.getDifficulty()));
        mDifficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRecipe.setDifficulty(RecipeDifficulty.getDifficulty(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final int[] rID= {R.id.recipe_S1,R.id.recipe_S2,R.id.recipe_S3,R.id.recipe_S4,
                R.id.recipe_S5,R.id.recipe_S6,R.id.recipe_S7,R.id.recipe_S8,R.id.recipe_S9};
        mSTextView= new TextView[mRecipe.getNbStepMax()];
        for(int i=0;i<mRecipe.getNbStepMax();i++) {
            mSTextView[i] = (TextView) v.findViewById(rID[i]);
        }
        mNewStepEnter=(ImageButton) v.findViewById(R.id.recipe_step_enter);
        mNewStepBack=(ImageButton) v.findViewById(R.id.recipe_step_back);
        updateListStep(v);

        mNewStepField= (EditText) v.findViewById(R.id.recipe_new_step);
        mNewStepField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRecipe.setStep(mStepNb+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mNewStepEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), mRecipe.getStep(mStepNb+1)+"/"+mStepNb,
//                        Toast.LENGTH_SHORT).show();
                mNewStepField.setVisibility(updateListStep(v));
                mNewStepField.getText().clear();
            }
        });

        mNewStepBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecipe.setStep(mStepNb, "");
                mRecipe.setStep(mStepNb+1, "");
                mNewStepField.setVisibility(updateListStep(v));
                mNewStepField.getText().clear();
            }
        });

        final int[] rIngID= {R.id.recipe_I01,R.id.recipe_I02,R.id.recipe_I03,R.id.recipe_I04,
                R.id.recipe_I05,R.id.recipe_I06,R.id.recipe_I07,R.id.recipe_I08,
                R.id.recipe_I09,R.id.recipe_I10,R.id.recipe_I11,R.id.recipe_I12,
                R.id.recipe_I13,R.id.recipe_I14,R.id.recipe_I15};
        mSTextIngView= new TextView[mRecipe.getNbIngMax()];
        for(int i=0;i<mRecipe.getNbIngMax();i++) {
            mSTextIngView[i] = (TextView) v.findViewById(rIngID[i]);
        }
        mNewIngEnter=(ImageButton) v.findViewById(R.id.recipe_ing_enter);
        mNewIngBack=(ImageButton) v.findViewById(R.id.recipe_ing_back);
        updateListIng(v);

        mNewIngField= (EditText) v.findViewById(R.id.recipe_new_ing);
        mNewIngField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRecipe.setIngredient(mIngNb+1, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mNewIngEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewIngField.setVisibility(updateListIng(v));
                mNewIngField.getText().clear();
            }
        });
        mNewIngBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecipe.setIngredient(mIngNb, "");
                mRecipe.setIngredient(mIngNb+1, "");
                mNewIngField.setVisibility(updateListIng(v));
                mNewIngField.getText().clear();
            }
        });

       return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode==REQUEST_PHOTO){
            Uri uri=FileProvider.getUriForFile(getActivity(),
                    FPROVIDER, mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            if (!ResizePhoto(mRecipe)){
                // pb
            }
            mRecipe.setDatePhoto(new Date());
            mRecipe.updateTS(AsynCallFlag.NEWPHOTO,true);
            updatePhotoView();
        }
    }

    private String getRecipeReport(){
        String report="";
        Integer iplus=0;
        report =getString(R.string.P2RE_title, mRecipe.getTitle())+"\n";
        report +=getString(R.string.P2RE_user,mRecipe.getOwner().getNameComplete())+"\n";

         if(!mRecipe.getSource_url_name().equals("")){
            report += getString(R.string.P2RE_url, mRecipe.getSource_url_name())+"\n";
        }
        for(int i=0;i<mRecipe.getNbIng();i++){
            report += getString(R.string.P2RE_ing, mRecipe.getIngredient(i+1))+"\n";
        }
        for(int i=0;i<mRecipe.getNbStep();i++){
            iplus=i+1;
            report += getString(R.string.P2RE_step, iplus+"",
                    mRecipe.getStep(i+1))+"\n";
        }
        report +=getString(R.string.P2RE_end);
        return report;
    }

    private void updatePhotoView(){
        if (mPhotoFile==null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(getResources().getDrawable(R.drawable.ic_recipe_camera));
        } else {
            Bitmap bitmap=PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private int updateListStep(View v) {
        int imax=mRecipe.getNbStepMax(), iplus;
        String[] display=new String[imax];
        mStepNb = mRecipe.getNbStep();
        int gone=View.GONE;
        int visible=View.VISIBLE;
        mSTextView[0].setText("1...");
        mSTextView[0].setVisibility(visible);
        for(int i=0;i<imax;i++){
            iplus=i+1; display[i]=iplus+". "+mRecipe.getStep(i+1);
            if (mStepNb>0){mSTextView[i].setText(display[i]);}
            if (i>=0){mSTextView[i].setVisibility((mStepNb>i)? visible:gone);}
        }
        mNewStepBack.setVisibility((mStepNb==0)? gone:visible);
        mNewStepEnter.setVisibility((mStepNb==imax)? gone:visible);
        return ((mStepNb==imax)?gone:visible);
    }

    private int updateListIng(View v) {
        int imax=mRecipe.getNbIngMax(), iplus;
        String[] display=new String[imax];
        mIngNb = mRecipe.getNbIng();
        int gone=View.GONE;
        int visible=View.VISIBLE;
        mSTextIngView[0].setText("-...");
        mSTextIngView[0].setVisibility(visible);
        for(int i=0;i<imax;i++){
            iplus=i+1; display[i]="-"+mRecipe.getIngredient(i+1);
            if (mIngNb>0){mSTextIngView[i].setText(display[i]);}
            if (i>=0){mSTextIngView[i].setVisibility((mIngNb>i)? visible:gone);}
        }
        mNewIngBack.setVisibility((mIngNb==0)? gone:visible);
        mNewIngEnter.setVisibility((mIngNb==imax)? gone:visible);
        return ((mIngNb==imax)?gone:visible);
    }

    private Boolean IsRecipeNew(UUID uuid){
        return uuid.toString().equals(UUIDNULL);
    }

    private Boolean ResizePhoto(Recipe r) {
        File f = CookBook.get(getActivity()).getPhotoFile(r);
        if (f==null || !f.exists()){
            //fdx Log(TAG, "No file from Cookbook for this recipe :" + r.getTitle()+" Error CB004");
            return false;
        } else {
            Bitmap bitmap=PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity(), 600);
            f.delete();
            try {
                if(!f.createNewFile()) {
                    //fdx Log(TAG, "File not deleted ! Error CB001");
                    return false;
                }
            } catch (IOException e) {
                //fdx Log(TAG, "Error in creating new file : "+e+" Error CB002");
                return false;
            }
            try {

                FileOutputStream fOut = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
                return true;
            } catch (IOException e) {
                //fdx Log(TAG, "Error in reducing and saving file "+" Error CB003");
                return false;
            }
        }
    }
}

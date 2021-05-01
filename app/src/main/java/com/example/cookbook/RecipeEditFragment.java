package com.example.cookbook;

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
import android.text.format.DateFormat;
import android.util.Log;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RecipeEditFragment extends Fragment {
    private static final String ARG_RECIPE_ID="recipe_id";
    private static final String DIALOG_DATE="DialogDate";
    private static final int REQUEST_DATE=0;
    private static final int REQUEST_PHOTO= 2;
    private static final String TAG = "DebugRecipeEditFragment";
    private Recipe mRecipe;
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
        UUID recipeId=(UUID) getArguments().getSerializable(ARG_RECIPE_ID);
        mRecipe=CookBook.get(getActivity()).getRecipe(recipeId);
        setHasOptionsMenu(true);
        mPhotoFile=CookBook.get(getActivity()).getPhotoFile(mRecipe);
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
        inflater.inflate(R.menu.fragment_recipe, menu);
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
                        "com.example.cookbook.fileprovider", mPhotoFile);
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
                    //Log.d(TAG, "onTextChanged de mSource_url input>" + s.toString()+"<");
                    try {
                        URL url=new URL(s.toString());
                    //    Log.d(TAG, "onTextChanged de mSource_url output >" + url.toString()+"<");
                        mRecipe.setSource_url(url);
                    } catch (MalformedURLException e) {
                        Log.d(TAG, "onTextChanged de mSource_url >" +s.toString()+"< Failed");
                    }
                    //
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
//                Log.d(TAG, "onTextChanged de mNbPersField >" + s.toString()+"<");
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
                //Log.d(TAG, "mSeasonSpinner>" + mRecipe.getSeason().name()+"<");
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
/*        mNoteField= (EditText) v.findViewById(R.id.recipe_note);
        mNoteField.setText(mRecipe.getNoteAvg()+"");
        mNoteField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int entry=Integer.parseInt(s.toString());
                if ((entry<0)||(entry>5)){entry=0;}
                mRecipe.setNoteAvg(entry);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
         });
*/
/*        mDateButton= (Button) v.findViewById(R.id.recipe_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm= getFragmentManager();
                DatePickerFragment dialog=DatePickerFragment
                        .newInstance(mRecipe.getDate());
                dialog.setTargetFragment(RecipeEditFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });
*/
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
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mRecipe.setDate(date);
 //           updateDate();
        } else if (requestCode==REQUEST_PHOTO){
            Uri uri=FileProvider.getUriForFile(getActivity(),
                    "com.example.cookbook.fileprovider", mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }

/*    private void updateDate() {
        mDateButton.setText(mRecipe.getDate().toString());
    }
*/
    private String getRecipeReport(){
        String report="";
        SessionInfo session=SessionInfo.get(getActivity());
        String header=getString(R.string.recipe_report_header,session.getName());
        String dateFormat = "dd MMM yyyy";
        String dateString=DateFormat.format(dateFormat,mRecipe.getDate()).toString();
        String title=getString(R.string.recipe_report_title,
                mRecipe.getTitle(), mRecipe.getSource(), dateString );
        String step1=getString(R.string.recipe_report_step1, mRecipe.getStep(1));
        String step2=getString(R.string.recipe_report_step2, mRecipe.getStep(2));
        String step3=getString(R.string.recipe_report_step3, mRecipe.getStep(3));
        String step4=getString(R.string.recipe_report_step4, mRecipe.getStep(4));
        String fin=getString(R.string.recipe_report_final);
        report= header+"\n"+title+"\n"+step1+"\n"+step2+"\n"+step3+"\n"+step4+ "\n" + fin;
        return report;
    }

    private void updatePhotoView(){
        if (mPhotoFile==null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
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
}

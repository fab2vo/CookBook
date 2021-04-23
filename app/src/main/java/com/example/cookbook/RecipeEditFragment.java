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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RecipeEditFragment extends Fragment {
    private static final String ARG_RECIPE_ID="recipe_id";
    private static final String DIALOG_DATE="DialogDate";
    private static final int REQUEST_DATE=0;
    private static final int REQUEST_PHOTO= 2;
    private Recipe mRecipe;
    private File mPhotoFile;
    private EditText mTitleField;
    private EditText mSourceField;
//    private Button mDateButton;
    private TextView mS1TextView;
    private TextView mS2TextView;
    private TextView mS3TextView;
    private TextView mS4TextView;
    private TextView mS5TextView;
    private EditText mNewStepField;
    private ImageButton mNewStepEnter;
    private ImageButton mNewStepBack;
    private int mStepNb;
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
        mStepNb=mRecipe.getNbStep();;
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
        final View v=inflater.inflate(R.layout.fragment_recipe, container, false);
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
        mS1TextView= (TextView) v.findViewById(R.id.recipe_S1);
        mS2TextView= (TextView) v.findViewById(R.id.recipe_S2);
        mS3TextView= (TextView) v.findViewById(R.id.recipe_S3);
        mS4TextView= (TextView) v.findViewById(R.id.recipe_S4);
        mS5TextView= (TextView) v.findViewById(R.id.recipe_S5);
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
                updateListStep(v);
                mNewStepField.getText().clear();
            }
        });

        mNewStepBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecipe.setStep(mStepNb, "");
                mRecipe.setStep(mStepNb+1, "");
                updateListStep(v);
                mNewStepField.getText().clear();
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
        String step1=getString(R.string.recipe_report_step1, mRecipe.getS1());
        String step2=getString(R.string.recipe_report_step2, mRecipe.getS2());
        String step3=getString(R.string.recipe_report_step3, mRecipe.getS3());
        String step4=getString(R.string.recipe_report_step4, mRecipe.getS4());
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

    private void updateListStep(View v) {
        mStepNb = mRecipe.getNbStep();
        mS1TextView.setText("1. " + mRecipe.getStep(1));
        if (mStepNb > 1) {
            mS2TextView.setText("2. " + mRecipe.getStep(2));
            mS2TextView.setVisibility(View.VISIBLE);
        } else {
            mS2TextView.setVisibility(View.GONE);
        }

        if (mStepNb > 2) {
            mS3TextView.setText("3. " + mRecipe.getStep(3));
            mS3TextView.setVisibility(View.VISIBLE);
        } else {
            mS3TextView.setVisibility(View.GONE);
        }

        if (mStepNb > 3) {
            mS4TextView.setText("4. " + mRecipe.getStep(4));
            mS4TextView.setVisibility(View.VISIBLE);
        } else {
            mS4TextView.setVisibility(View.GONE);
        }

        if (mStepNb > 4) {
            mS5TextView.setText("5. " + mRecipe.getStep(5));
            mS5TextView.setVisibility(View.VISIBLE);
        } else {
            mS5TextView.setVisibility(View.GONE);
        }
    }
}

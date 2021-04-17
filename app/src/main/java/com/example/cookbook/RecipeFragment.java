package com.example.cookbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.cookbook.Recipe;

import java.util.Date;
import java.util.UUID;

public class RecipeFragment extends Fragment {
    private static final String ARG_RECIPE_ID="recipe_id";
    private static final String DIALOG_DATE="DialogDate";
    private static final int REQUEST_DATE=0;
    private Recipe mRecipe;
    private EditText mTitleField;
    private EditText mSourceField;
    private EditText mNoteField;
    private Button mDateButton;
    private EditText mS1Field;
    private EditText mS2Field;
    private EditText mS3Field;
    private EditText mS4Field;

    public static RecipeFragment newInstance(UUID recipeId){
        Bundle args=new Bundle();
        args.putSerializable(ARG_RECIPE_ID, recipeId);
        RecipeFragment fragment=new RecipeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mRecipe=new Recipe(4);
        UUID recipeId=(UUID) getArguments().getSerializable(ARG_RECIPE_ID);
        mRecipe=CookBook.get(getActivity()).getRecipe(recipeId);
    }

    @Override
    public void onPause(){
        super.onPause();
        CookBook.get(getActivity()).updateRecipe(mRecipe);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.fragment_recipe, container, false);

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

        mNoteField= (EditText) v.findViewById(R.id.recipe_note);
        mNoteField.setText(mRecipe.getNote()+"");
        mNoteField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int entry=Integer.parseInt(s.toString());
                if ((entry<0)||(entry>5)){entry=0;}
                mRecipe.setNote(entry);
            }

            @Override
            public void afterTextChanged(Editable s) {
//
            }
        });

        mDateButton= (Button) v.findViewById(R.id.recipe_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm= getFragmentManager();
                DatePickerFragment dialog=DatePickerFragment
                        .newInstance(mRecipe.getDate());
                dialog.setTargetFragment(RecipeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mS1Field= (EditText) v.findViewById(R.id.recipe_S1);
        mS1Field.setText(mRecipe.getS1());
        mS1Field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRecipe.setS1(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mS2Field= (EditText) v.findViewById(R.id.recipe_S2);
        mS2Field.setText(mRecipe.getS2());
        mS2Field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRecipe.setS2(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mS3Field= (EditText) v.findViewById(R.id.recipe_S3);
        mS3Field.setText(mRecipe.getS3());
        mS3Field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRecipe.setS3(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mS4Field= (EditText) v.findViewById(R.id.recipe_S4);
        mS4Field.setText(mRecipe.getS4());
        mS4Field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRecipe.setS4(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

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
            updateDate();
        }
    }

    private void updateDate() {
        mDateButton.setText(mRecipe.getDate().toString());
    }
}

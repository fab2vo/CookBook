package com.example.cookbook;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.cookbook.Recipe;

public class RecipeFragment extends Fragment {
    private Recipe mRecipe;
    private EditText mTitleField;
    private EditText mSourceField;
    private EditText mNoteField;
    private EditText mS1Field;
    private EditText mS2Field;
    private EditText mS3Field;
    private EditText mS4Field;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mRecipe=new Recipe(4);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.fragment_recipe, container, false);

        mTitleField= (EditText) v.findViewById(R.id.recipe_title);
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
        mNoteField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRecipe.setNote(Integer.parseInt(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {
                // blank
            }
        });

        mS1Field= (EditText) v.findViewById(R.id.recipe_S1);
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
}

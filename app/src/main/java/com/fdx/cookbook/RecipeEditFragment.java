package com.fdx.cookbook;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuCompat;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

public class RecipeEditFragment extends Fragment {
    // Constantes
    private static final String ARG_RECIPE_ID="recipe_id";
    private static final int PICK_IMAGE= 3;
    private static final String TAG = "CB_EditFrag";
    private static final String UUIDNULL="00000000-0000-0000-0000-000000000000";
    private static final String PUNCT="#-_!?.,:()&$£€%*+0-9"; //javaLowercase : A-Za-ÿ Upper et Lower [A-Za-zÀ-ÿ]
    private static final String PTI="[\\p{javaUpperCase}][\\p{javaUpperCase}\\p{javaLowerCase}"+PUNCT+"\\p{Space}]{0,80}";
    private static final String PSO="[\\p{javaUpperCase}\\p{javaLowerCase}"+PUNCT+"\\p{Space}]{0,254}";
    private static final String PURL="https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static final String PST="[\\p{javaUpperCase}\\p{javaLowerCase}"+PUNCT+"\\p{Space}]{0,499}";
    private static final String PIN="[\\p{javaUpperCase}\\p{javaLowerCase}"+PUNCT+"\\p{Space}]{0,254}";
    private static final int MAXNBPERS= 12;
    private Recipe mRecipe;
    private Recipe mRecipeInit;
    private UUID mRecipeId;
    private File mPhotoFile;
    private EditText mTitleField;
    private EditText mSourceField;
    private EditText mSourceUrl;
    private EditText mNbPersField;
    private TextView[] mStepTextNum;
    private EditText[] mStepTextEdit;
    private ImageButton mStepInc;
    private int mStepNb;
    private TextView[] mIngTextNum;
    private EditText[] mIngTextEdit;
    private ImageButton mIngInc;
    private int mIngNb;
    private Spinner mSeasonSpinner;
    private Spinner mTypeSpinner;
    private Spinner mDifficultySpinner;
    private ScrollView mScroll;
    private ImageView mPhotoView;
    private Bitmap mBmp;
    private FloatingActionButton mFAB;

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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_recipe, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.recipe_menu_done:
                if (mRecipe.getTitle().length()>0){
                    if (!mRecipe.hasNotChangedSince(mRecipeInit)){
                    mRecipe.updateTS(AsynCallFlag.NEWRECIPE, true);
                    mRecipe.setDate(new Date());
                    }
                    if (mBmp!=null){
                        NetworkUtils networkutils=new NetworkUtils(getContext());
                        networkutils.saveBmpInRecipe(mBmp, mRecipe);
                        mRecipe.setDatePhoto(new Date());
                        mRecipe.updateTS(AsynCallFlag.NEWPHOTO,true);
                    }
                    if (IsRecipeNew(mRecipeId)){
                        CookBook.get(getActivity()).addRecipe(mRecipe);
                    } else {
                        CookBook.get(getActivity()).updateRecipe(mRecipe);
                    }

                }
                getActivity().onBackPressed();
                return true;*/
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
        mPhotoView=(ImageView) v.findViewById(R.id.recipe_photo);
        updatePhotoView();
        mFAB= (FloatingActionButton) v.findViewById(R.id.floating_action_button2);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                if (mRecipe.getTitle().length()>0){
                    if (!mRecipe.hasNotChangedSince(mRecipeInit)){
                        mRecipe.updateTS(AsynCallFlag.NEWRECIPE, true);
                        mRecipe.setDate(new Date());
                    }
                    if (mBmp!=null){
                        NetworkUtils networkutils=new NetworkUtils(getContext());
                        networkutils.saveBmpInRecipe(mBmp, mRecipe);
                        mRecipe.setDatePhoto(new Date());
                        mRecipe.updateTS(AsynCallFlag.NEWPHOTO,true);
                    }
                    if (IsRecipeNew(mRecipeId)){
                        CookBook.get(getActivity()).addRecipe(mRecipe);
                    } else {
                        CookBook.get(getActivity()).updateRecipe(mRecipe);
                    }

                }
                getActivity().onBackPressed();
            }
        });
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
        mTitleField= (EditText) v.findViewById(R.id.recipe_title);
        mTitleField.setText(mRecipe.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(), PTI, mTitleField))
                    mRecipe.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSourceField= (EditText) v.findViewById(R.id.recipe_source);
        mSourceField.setText(mRecipe.getSource());
        mSourceField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(), PSO, mSourceField))
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
                if (!s.toString().equals(""))  {
                    if (checkField(s.toString(),PURL,mSourceUrl)){
                        try {
                            URL url=new URL(s.toString());
                            mRecipe.setSource_url(url);
                        } catch (MalformedURLException e) {
                            deBug( "onTextChanged de mSource_url >" +s.toString()+"< Failed >"+ e);
                        }
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
                if (!s.toString().equals(""))  {
                    int nb_entered = Integer.parseInt(s.toString());
                    if ((nb_entered > 0) && (nb_entered < (MAXNBPERS+1))) {
                        mRecipe.setNbPers(nb_entered);
                        mNbPersField.setBackgroundColor(getResources().getColor(R.color.bg_enter_val));
                    } else mNbPersField.setBackgroundColor(getResources().getColor(R.color.light_red));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSeasonSpinner= (Spinner) v.findViewById(R.id.recipe_season);
        ArrayAdapter<CharSequence> adapterSeason = ArrayAdapter.createFromResource(getContext(),
                R.array.recipe_season_array, R.layout.edit_spinner_item);
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

        mTypeSpinner= (Spinner) v.findViewById(R.id.recipe_type);
        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(getContext(),
                R.array.recipe_type_array, R.layout.edit_spinner_item);
        mTypeSpinner.setAdapter(adapterType);
        mTypeSpinner.setSelection(RecipeType.getIndex(mRecipe.getType()));
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRecipe.setType(RecipeType.getType(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mDifficultySpinner= (Spinner) v.findViewById(R.id.recipe_difficulty);
        ArrayAdapter<CharSequence> adapterDifficulty = ArrayAdapter.createFromResource(getContext(),
                R.array.recipe_difficulty_array, R.layout.edit_spinner_item);
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
        final int[] rID_in= {R.id.recipe_S1_in,R.id.recipe_S2_in,R.id.recipe_S3_in,R.id.recipe_S4_in,
                R.id.recipe_S5_in,R.id.recipe_S6_in,R.id.recipe_S7_in,R.id.recipe_S8_in,R.id.recipe_S9_in};
        mStepTextNum= new TextView[mRecipe.getNbStepMax()];
        mStepTextEdit=new EditText[mRecipe.getNbStepMax()];
        for(int i=0;i<mRecipe.getNbStepMax();i++) {
            mStepTextNum[i] = (TextView) v.findViewById(rID[i]);
            mStepTextEdit[i] = (EditText) v.findViewById(rID_in[i]);
        }
        mStepInc=(ImageButton) v.findViewById(R.id.step_add);
        updateListStep(v);

        mStepTextEdit[0].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PST,mStepTextEdit[0]))
                    mRecipe.setStep(0+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mStepTextEdit[1].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PST,mStepTextEdit[1]))
                    mRecipe.setStep(1+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mStepTextEdit[2].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PST,mStepTextEdit[2]))
                    mRecipe.setStep(2+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mStepTextEdit[3].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PST,mStepTextEdit[3]))
                    mRecipe.setStep(3+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mStepTextEdit[4].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PST,mStepTextEdit[4]))
                    mRecipe.setStep(4+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mStepTextEdit[5].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PST,mStepTextEdit[5]))
                    mRecipe.setStep(5+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mStepTextEdit[6].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PST,mStepTextEdit[6]))
                    mRecipe.setStep(6+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mStepTextEdit[7].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PST,mStepTextEdit[7]))
                    mRecipe.setStep(7+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mStepTextEdit[8].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PST,mStepTextEdit[8]))
                    mRecipe.setStep(8+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        mStepInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateListStep(v);
            }
        });

        final int[] rIngID= {R.id.recipe_I01,R.id.recipe_I02,R.id.recipe_I03,R.id.recipe_I04,
                R.id.recipe_I05,R.id.recipe_I06,R.id.recipe_I07,R.id.recipe_I08,
                R.id.recipe_I09,R.id.recipe_I10,R.id.recipe_I11,R.id.recipe_I12,
                R.id.recipe_I13,R.id.recipe_I14,R.id.recipe_I15};
        final int[] rIngID_in= {R.id.recipe_I01_in,R.id.recipe_I02_in,R.id.recipe_I03_in,R.id.recipe_I04_in,
                R.id.recipe_I05_in,R.id.recipe_I06_in,R.id.recipe_I07_in,R.id.recipe_I08_in,
                R.id.recipe_I09_in,R.id.recipe_I10_in,R.id.recipe_I11_in,R.id.recipe_I12_in,
                R.id.recipe_I13_in,R.id.recipe_I14_in,R.id.recipe_I15_in};
        mIngTextNum= new TextView[mRecipe.getNbIngMax()];
        mIngTextEdit=new EditText[mRecipe.getNbIngMax()];
        for(int i=0;i<mRecipe.getNbIngMax();i++) {
            mIngTextNum[i] = (TextView) v.findViewById(rIngID[i]);
            mIngTextEdit[i] = (EditText) v.findViewById(rIngID_in[i]);
        }
        mIngInc=(ImageButton) v.findViewById(R.id.ing_add);
        mIngTextEdit[0].setText(mRecipe.getIngredient(0 + 1));
        mIngTextEdit[1].setText(mRecipe.getIngredient(1 + 1));
        mIngTextEdit[2].setText(mRecipe.getIngredient(2 + 1));
        mIngInc=(ImageButton) v.findViewById(R.id.ing_add);
        updateListIng(v);

        mIngTextEdit[0].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PIN,mIngTextEdit[0]))
                    mRecipe.setIngredient(0+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mIngTextEdit[1].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PIN,mIngTextEdit[1]))
                    mRecipe.setIngredient(1+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mIngTextEdit[2].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PIN,mIngTextEdit[2]))
                    mRecipe.setIngredient(2+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mIngTextEdit[3].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PIN,mIngTextEdit[3]))
                    mRecipe.setIngredient(3+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mIngTextEdit[4].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PIN,mIngTextEdit[4]))
                    mRecipe.setIngredient(4+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mIngTextEdit[5].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PIN,mIngTextEdit[5]))
                    mRecipe.setIngredient(5+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mIngTextEdit[6].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PIN,mIngTextEdit[6]))
                    mRecipe.setIngredient(6+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mIngTextEdit[7].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PIN,mIngTextEdit[7]))
                    mRecipe.setIngredient(7+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mIngTextEdit[8].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PIN,mIngTextEdit[8]))
                    mRecipe.setIngredient(8+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mIngTextEdit[9].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PIN,mIngTextEdit[9]))
                    mRecipe.setIngredient(9+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mIngTextEdit[10].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PIN,mIngTextEdit[10]))
                    mRecipe.setIngredient(10+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mIngTextEdit[11].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PIN,mIngTextEdit[11]))
                    mRecipe.setIngredient(11+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mIngTextEdit[12].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PIN,mIngTextEdit[12]))
                    mRecipe.setIngredient(12+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mIngTextEdit[13].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PIN,mIngTextEdit[13]))
                    mRecipe.setIngredient(13+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mIngTextEdit[14].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkField(s.toString(),PIN,mIngTextEdit[14]))
                    mRecipe.setIngredient(14+1, s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mIngInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateListIng(v);
            }
        });

       return v;
    }
    private Boolean checkField(String s, String pattern, EditText editext){
        if (s==null) return true;
        if (pattern==null) return true;
        if (Pattern.matches(pattern,s)) {
            editext.setBackgroundColor(getResources().getColor(R.color.bg_enter_val));
        return true;}
        else {
            editext.setBackgroundColor(getResources().getColor(R.color.light_red));
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode==PICK_IMAGE){
            if (data == null) return ;
            try{
            InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
            PictureUtils picutil=new PictureUtils();
            Bitmap bmp=BitmapFactory.decodeStream(inputStream);
            mBmp=picutil.getScaledBitmap(bmp,600);
            mPhotoView.setImageBitmap(mBmp);
            } catch(Exception e) {
                deBug("recover onActivityResult error:"+e);
            }
        }
    }

    private void updatePhotoView(){
        if (mPhotoFile==null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(getResources().getDrawable(R.drawable.ic_recipe_camera));
        } else {
            Bitmap bitmap=PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private void updateListStep(View v) {
        String cas;
        int i_cur=mRecipe.getNbStep();
        int i_max=mRecipe.getNbStepMax();
        for (int i=0;i<mRecipe.getNbStepMax();i++) {
            cas = "NOTVISIBLE";
            if (i == 0) cas = "FIRST";
            if (i < i_cur) cas = "ACTIVE";
            if ((i == i_cur)&&(i_cur<i_max)) cas = "EMPTY_READY";
            if ((i == i_cur)&&(i_cur==i_max)) cas = "ACTIVE";
            switch (cas) {
                case "FIRST":
                    if (mRecipe.getNbStep() == 0) {
                        mStepTextEdit[i].setText("");
                        mStepTextEdit[i].setHint(R.string.P3S1H);}
                    else mStepTextEdit[i].setText(mRecipe.getStep(i + 1));
                    break;
                case "ACTIVE":
                    mStepTextNum[i].setVisibility(View.VISIBLE);
                    mStepTextEdit[i].setVisibility(View.VISIBLE);
                    mStepTextEdit[i].setText(mRecipe.getStep(i + 1));
                    mStepTextEdit[i].setHint("");
                    break;
                case "EMPTY_READY":
                    mStepTextNum[i].setVisibility(View.VISIBLE);
                    mStepTextEdit[i].setVisibility(View.VISIBLE);
                    mStepTextEdit[i].setText("");
                    mStepTextEdit[i].setHint(R.string.P3S1H);
                    break;
                case "NOTVISIBLE":
                    mStepTextNum[i].setVisibility(View.GONE);
                    mStepTextEdit[i].setVisibility(View.GONE);
                    mStepTextEdit[i].setText("");
                    mStepTextEdit[i].setHint("");
            }
        }
    }

    private void updateListIng(View v) {
        String cas;
        int i_cur=mRecipe.getNbIng();
        int i_max=mRecipe.getNbIngMax();
        for (int i=0;i<mRecipe.getNbIngMax();i++) {
            cas = "NOTVISIBLE";
            if (i == 0) cas = "FIRST";
            if (i < i_cur) cas = "ACTIVE";
            if ((i == i_cur)&&(i_cur<i_max)) cas = "EMPTY_READY";
            if ((i == i_cur)&&(i_cur==i_max)) cas = "ACTIVE";
            switch (cas) {
                case "FIRST":
                    if (mRecipe.getNbIng() == 0) {
                        mIngTextEdit[i].setText("");
                        mIngTextEdit[i].setHint(R.string.P3IT);}
                    else mIngTextEdit[i].setText(mRecipe.getIngredient(i + 1));
                    break;
                case "ACTIVE":
                    mIngTextNum[i].setVisibility(View.VISIBLE);
                    mIngTextEdit[i].setVisibility(View.VISIBLE);
                    mIngTextEdit[i].setText(mRecipe.getIngredient(i + 1));
                    mIngTextEdit[i].setHint("");
                    break;
                case "EMPTY_READY":
                    mIngTextNum[i].setVisibility(View.VISIBLE);
                    mIngTextEdit[i].setVisibility(View.VISIBLE);
                    mIngTextEdit[i].setText("");
                    mIngTextEdit[i].setHint(R.string.P3IT);
                    break;
                case "NOTVISIBLE":
                    mIngTextNum[i].setVisibility(View.GONE);
                    mIngTextEdit[i].setVisibility(View.GONE);
                    mIngTextEdit[i].setText("");
                    mIngTextEdit[i].setHint("");
            }
        }
    }

    private Boolean IsRecipeNew(UUID uuid){
        return uuid.toString().equals(UUIDNULL);
    }

    private void deBug(String s){
        // Log.d(TAG, s);
    }
}

package com.fdx.cookbook;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeMailDisplayFragment extends Fragment {
    private RecyclerView mRecipeRecyclerView;
    private RecipeAdapter mAdapter;
    private SessionInfo mSession;
    private ArrayList<MailCard> mMailCards;
    private static final String TAG = "CB_R.MailDisplayFrag";

    public static RecipeMailDisplayFragment newInstance() {
        RecipeMailDisplayFragment fragment=new RecipeMailDisplayFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSession= SessionInfo.get(getActivity());
        mMailCards=new ArrayList<>();
        List<Recipe> recipes =CookBook.get(getActivity()).getRecipes();
        for (Recipe r:recipes){
            if (r.IsMessage()) mMailCards.add(new MailCard(r));
        }
        //deBugShow("Nombre de cards : "+mMailCards.size());
        getRequests gR=new getRequests();
        gR.execute(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        mRecipeRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_recycler_view);
        mRecipeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        List<MailCard> mailcards=new ArrayList<>();
        for(MailCard mc:mMailCards){
            if (mc.isSubmitted()) mailcards.add(mc);
        }
        if (mailcards.isEmpty()){
            Toast.makeText(getContext(), getString(R.string.P4M0),Toast.LENGTH_SHORT ).show();
        }
        if (mAdapter==null){
            mAdapter=new RecipeMailDisplayFragment.RecipeAdapter(mailcards);
            mRecipeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setRecipes(mailcards);
            mAdapter.notifyDataSetChanged();
        }
    }
// -----------------------------------RECIPE HOLDER ------------------------
    private class RecipeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mMessage;
        private TextView mFrom;
        private ImageView mPhotoView;
        private ImageView mDelete;
        private ImageView mAdd;
        private File mPhotoFile;
        private Recipe mRecipe;
        private MailCard mMailCard;

        public RecipeHolder(LayoutInflater inflater,ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_mail_display, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView= (TextView) itemView.findViewById(R.id.recipe_MD_title);
            mMessage=(TextView) itemView.findViewById(R.id.mail_display_message);
            mFrom=(TextView) itemView.findViewById(R.id.mail_display_author);
            mDelete=(ImageView) itemView.findViewById(R.id.mail_display_delete);
            mAdd=(ImageView) itemView.findViewById(R.id.mail_display_add);
            mPhotoView=(ImageView) itemView.findViewById(R.id.recipe_MD_photo);
            mPhotoFile=CookBook.get(getActivity()).getPhotoFile(mRecipe);

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMailCard.isReceived()){
                        mRecipe.setStatus(StatusRecipe.Deleted);
                        CookBook cookbook=CookBook.get(getActivity());
                        cookbook.updateRecipe(mRecipe);
                        mMailCard.setRefused();
                        deBugShow("Received "+mRecipe.getTitle()+" refused");
                        updateUI();
                    }
                }
            });
            mAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMailCard.isReceived()){
                        mRecipe.setStatus(StatusRecipe.Visible);
                        CookBook cookbook=CookBook.get(getActivity());
                        cookbook.updateRecipe(mRecipe);
                        mMailCard.setAccepted();
                        deBugShow("Received "+mRecipe.getTitle()+" accepted");
                        updateUI();
                    }
                }
            });

        }
        public void bind(MailCard mc){
            mMailCard=mc;
            CookBook cookbook=CookBook.get(getActivity());
            mRecipe=cookbook.getRecipe(mc.getRecipeId());
            mTitleTextView.setText(mRecipe.getTitle());
            mMessage.setText(mc.getMessage());
            if (mc.isReceived()) mFrom.setText(getString(R.string.P4MES_IN,mc.getUser().getNameComplete()));
            if (mc.isRequest()) mFrom.setText(getString(R.string.P4MES_OUT,mc.getUser().getNameComplete()));
            mPhotoFile=CookBook.get(getActivity()).getPhotoFile(mRecipe);
            if (mPhotoFile==null || !mPhotoFile.exists()){
                mPhotoView.setImageDrawable(getResources().getDrawable(R.drawable.ic_recipe_see));
            } else {
                Bitmap bitmap=PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
                mPhotoView.setImageBitmap(bitmap);
            }
        }
    @Override
    public void onClick(View v){    }
    }
    // -----------------------------------ADAPTER------------------------
    private class RecipeAdapter extends RecyclerView.Adapter<RecipeMailDisplayFragment.RecipeHolder> {
        private List<MailCard> maMailCards;
        public RecipeAdapter(List<MailCard> mailcards){
            maMailCards=mailcards;
        }

        @NonNull
        @Override
        public RecipeMailDisplayFragment.RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            return new RecipeMailDisplayFragment.RecipeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecipeMailDisplayFragment.RecipeHolder recipeHolder, int i) {
            MailCard mc=maMailCards.get(i);
            recipeHolder.bind(mc);
        }

        @Override
        public int getItemCount() {
            return maMailCards.size();
        }

        public void setRecipes(List<MailCard> mailcards){
            maMailCards=mailcards;
        }
    }

    /******************************************************************************************
     *                            ASYNC                                                        *
     *******************************************************************************************/

    class getRequests extends AsyncTask<Integer, Integer, Boolean> {
        private static final String PHPGETREQUESTS = "getrequests.php";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(),"Debut sync ", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b) {
                Toast.makeText(getActivity(), "Succès  ", Toast.LENGTH_LONG).show();
                //deBugShow("Nombre de cards post async: "+mMailCards.size());
                updateUI();
            }
            else {
                Toast.makeText(getActivity(), "Echec", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Boolean doInBackground(Integer ... integers) {
            MailCard mc;
            NetworkUtils netutil=new NetworkUtils(getContext());
            if (!netutil.test204()) {deBugShow("204!"); return false;}
            HashMap<String, String> data = new HashMap<>();
            data.put("iduser", mSession.getUser().getId().toString());
            String json = netutil.sendPostRequestJson(mSession.getURLPath() + PHPGETREQUESTS, data);
            if (json==null) return false;
            if (json.equals("")) return false;
            try {
                JSONArray jarr1=new JSONArray(json);
                for (int i=0; i<jarr1.length(); i++){
                    JSONObject obj = jarr1.getJSONObject(i);
                    mc=netutil.parseObjectRequest(obj);
                    if (mc==null) {
                        deBugShow("Error in parsing request");
                        return false;}
                    mMailCards.add(mc);
                }
            } catch (Exception e){
                deBugShow("Failure in parsing JSON Array Requests "+e);
                return false;
            }
            return true;
        }
    }

    private void deBugShow(String s){
        Log.d(TAG, s);
    }

}

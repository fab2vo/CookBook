package com.fdx.cookbook;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class RecipeCommunityFragment extends Fragment {
    private RecyclerView mRecipeRecyclerView;
    private RecipeAdapter mAdapter;
    private SessionInfo mSession;
    private static final Integer RECENT=1;
    private static final Integer POPULAR=2;
    private static final Integer BESTNOTE=3;
    private Integer mSelectType;
    private MenuItem mMenuR;
    private MenuItem mMenuP;
    private MenuItem mMenuN;
    private CookBooksShort mCookbookShort;
    private getShortRecipesFromCommunity getAsyncShorties;
    private static final String TAG = "CB_ComFrag";

    public static RecipeCommunityFragment newInstance() {
        RecipeCommunityFragment fragment=new RecipeCommunityFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSession= SessionInfo.get(getActivity());
        mCookbookShort=CookBooksShort.get(getActivity());
        mSelectType=mCookbookShort.getSelectType();
        if (mCookbookShort.isNew()){
            getAsyncShorties=new getShortRecipesFromCommunity();
            getAsyncShorties.execute(20);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_menu_com, menu);
        mMenuR = menu.findItem(R.id.com_recent);
        mMenuN = menu.findItem(R.id.com_bestnote);
        mMenuP = menu.findItem(R.id.com_popular);
        mMenuR.setEnabled(mSelectType!=RECENT);
        mMenuN.setEnabled(mSelectType!=BESTNOTE);
        mMenuP.setEnabled(mSelectType!=POPULAR);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mCookbookShort.isDownloading()) return true;
        switch (item.getItemId()) {
            case R.id.com_recent:
                mCookbookShort.setSelectType(RECENT);
                break;
            case R.id.com_popular:
                mCookbookShort.setSelectType(POPULAR);
                break;
            case R.id.com_bestnote:
                mCookbookShort.setSelectType(BESTNOTE);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        mSelectType=mCookbookShort.getSelectType();
        mMenuR.setEnabled(mSelectType!=RECENT);
        mMenuN.setEnabled(mSelectType!=BESTNOTE);
        mMenuP.setEnabled(mSelectType!=POPULAR);
        getAsyncShorties=new getShortRecipesFromCommunity();
        getAsyncShorties.execute(20);
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_community, container, false);
        mRecipeRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_recycler_view);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((RecipeCommunityActivity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int ncol = (Integer) displayMetrics.widthPixels/350;
        if (ncol<3) ncol=3;
        mRecipeRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),ncol));
        updateUI();
        return view;
    }
    @Override
    public void onPause(){
        super.onPause();
        //if (mCookbookShort.isDownloading()) getAsyncShorties.cancel(true);
    }

    private void updateUI() {
        List<Recipe> recipes=mCookbookShort.getRecipes();
        /*if (recipes.isEmpty()){
            Toast.makeText(getContext(), "Vide !!!",Toast.LENGTH_LONG ).show();
        }*/
        if (mAdapter==null){
            mAdapter=new RecipeCommunityFragment.RecipeAdapter(recipes);
            mRecipeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setRecipes(recipes);
            mAdapter.notifyDataSetChanged();
        }
    }
    // -----------------------------------RECIPE HOLDER ------------------------
    private class RecipeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mName;
        //private TextView mFamily;
        private ImageView mPhotoView;
        private Recipe mRecipe;

        public RecipeHolder(LayoutInflater inflater,ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_grid, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView= (TextView) itemView.findViewById(R.id.com_title);
            mName=(TextView) itemView.findViewById(R.id.com_name);
            //mFamily=(TextView) itemView.findViewById(R.id.com_family);
            mPhotoView=(ImageView) itemView.findViewById(R.id.com_photo);
            mPhotoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
        public void bind(Recipe recipe){
            mRecipe=recipe;
            mTitleTextView.setText(mRecipe.getTitle());
            mName.setText(mRecipe.getOwner().getNameComplete());
            //mFamily.setText("@"+mRecipe.getOwner().getFamily());
            Bitmap bm=mRecipe.getImage();
            if (bm!=null) mPhotoView.setImageBitmap(bm);
            else mPhotoView.setImageDrawable(getResources().getDrawable(R.drawable.ic_recipe_see));
        }

        @Override
        public void onClick(View v){    }
    }
    // -----------------------------------ADAPTER------------------------
    private class RecipeAdapter extends RecyclerView.Adapter<RecipeCommunityFragment.RecipeHolder> {
        private List<Recipe> mRecipes;
        public RecipeAdapter(List<Recipe> recipes){
            mRecipes=recipes;
        }

        @NonNull
        @Override
        public RecipeCommunityFragment.RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            return new RecipeCommunityFragment.RecipeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecipeCommunityFragment.RecipeHolder recipeHolder, int i) {
            Recipe recipe=mRecipes.get(i);
            recipeHolder.bind(recipe);
        }

        @Override
        public int getItemCount() {
            return mRecipes.size();
        }

        public void setRecipes(List<Recipe> recipes){
            mRecipes=recipes;
        }
    }

    /******************************************************************************************
     *                            ASYNC                                                        *
     *******************************************************************************************/

    class getShortRecipesFromCommunity extends AsyncTask<Integer, Integer, Boolean> {
        private static final String PHP204 = "return204.php";
        private static final String PHPGETSHORTRECIPES = "getcommunitynews.php";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mCookbookShort.getDLStarted();
            mSelectType=mCookbookShort.getSelectType();
            //Toast.makeText(getActivity(),"Debut : "+mSelectType, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            mCookbookShort.setDLCompleted();
            if (b) {
                //Toast.makeText(getActivity(), "Succès de "+mSelectType, Toast.LENGTH_LONG).show();
            }
            else {
                //Toast.makeText(getActivity(), "Echec", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Boolean doInBackground(Integer ... integers) {
            Integer ntoDownload=integers[0];
            if (!test204()) {deBugShow("204!"); return false;}
            // download texts
            String s=getStringRecipesFromServer(0,ntoDownload,mSelectType, false);
            if (s.equals("")) {deBugShow("Echec query 1"); return false;}
            if (!fillInitialTable(s)) {deBugShow("Echec parse >"+s+"<"); return false;}
            publishProgress(1);
            // download photo i
            for(Integer i=0;i<mCookbookShort.getsize();i++) {
                if(isCancelled()) {deBugShow("Interrupted)"); return false;}
                s = getStringRecipesFromServer(i, 1, mSelectType, true);
                if (s.equals("")) {
                    deBugShow("Echec query "+i);
                    break;
                }
                if (updatePhotoOfRecipe(s)) publishProgress(i+2);
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer ... values){
            updateUI();
        }

        private String getStringRecipesFromServer(Integer start, Integer count, Integer type, Boolean withphoto){
            HashMap<String, String> data = new HashMap<>();
            data.put("iduser", mSession.getUser().getId().toString());
            String comtype="";
            if (type==RECENT) comtype="RECENT";
            if (type==POPULAR) comtype="POPULAR";
            if (type==BESTNOTE) comtype="BESTNOTE";
            data.put("comtype", comtype);
            DecimalFormat formatter = new DecimalFormat("00");
            data.put("start", formatter.format( start));
            data.put("count", formatter.format(count));
            data.put("comparam", (withphoto ? "WP":"WOP"));
            NetworkUtils mNetUtils = new NetworkUtils(getContext());
            String result = mNetUtils.sendPostRequestJson(mSession.getURLPath() + PHPGETSHORTRECIPES, data);
            if (result==null) return "";
            return result;
        }

        private boolean fillInitialTable(String s){
            NetworkUtils mNetUtils = new NetworkUtils(getContext());
            List<Recipe> recipes=mNetUtils.parseRecipesOfCommunity(s,false);
            if ((recipes!=null)&&(!recipes.isEmpty())){
                mCookbookShort.clear();
                mCookbookShort.fill(recipes);
                return true;
            } else return false;
        }

        private boolean updatePhotoOfRecipe(String s){
            NetworkUtils mNetUtils = new NetworkUtils(getContext());
            List<Recipe> recipes=mNetUtils.parseRecipesOfCommunity(s,true);
            if ((recipes!=null)&&(!recipes.isEmpty())){
                mCookbookShort.updatePhoto(recipes.get(0));
                return true;
            } return false;
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
                deBugShow("Test 204 : " + e);
                return false;
            }
        }

    }
    private void deBugShow(String s){
        Log.d(TAG, s);
    }

}
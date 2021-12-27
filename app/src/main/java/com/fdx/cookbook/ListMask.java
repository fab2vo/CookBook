package com.fdx.cookbook;

import java.util.UUID;

public class ListMask {
    private boolean SeasonFiltered;
    private RecipeSeason SeasonState;
    private boolean DifficultyFiltered;
    private RecipeDifficulty DifficultyState;
    private boolean TypeFiltered;
    private RecipeType TypeState;
    private boolean UserFiltered;
    private User UserState;
    private boolean IsNoteSorted;
    private boolean IsSearched;
    private boolean IsTitleAlphaSorted;
    private String Query;

    public ListMask(User u){
        SeasonFiltered=false;
        DifficultyFiltered=false;
        TypeFiltered=false;
        UserFiltered=false;
        IsNoteSorted=false;
        IsSearched=false;
        IsTitleAlphaSorted=false;
        Query="";
        UserState=u;
        SeasonState=RecipeSeason.ALLYEAR;
        DifficultyState=RecipeDifficulty.UNDEFINED;
        TypeState=RecipeType.MAIN;
    }

    public void reset(){
        SeasonFiltered=false;
        DifficultyFiltered=false;
        TypeFiltered=false;
        UserFiltered=false;
        IsNoteSorted=false;
        IsSearched=false;
        IsTitleAlphaSorted=false;
        Query="";
        SeasonState=RecipeSeason.ALLYEAR;
        DifficultyState=RecipeDifficulty.UNDEFINED;
        TypeState=RecipeType.MAIN;
    }

    public Integer toggleSun(){
        Integer i;
        if (SeasonFiltered){
            SeasonFiltered=false;
            SeasonState=RecipeSeason.ALLYEAR;
            i=R.string.TSOFF;
        } else {
            SeasonFiltered=true;
            SeasonState=RecipeSeason.SUMMER;
            i=R.string.TSON;
        }
        return i;
    }

    public Integer toggleWinter(){
        Integer i;
        if (SeasonFiltered){
            SeasonFiltered=false;
            SeasonState=RecipeSeason.ALLYEAR;
            i=R.string.TWOFF;
        } else {
            SeasonFiltered=true;
            SeasonState=RecipeSeason.WINTER;
            i=R.string.TWON;
        }
        return i;
    }

    public Integer toggleTitle(){
        Integer i;
        if (IsTitleAlphaSorted){
            IsTitleAlphaSorted=false;
            i=R.string.TTIOFF;
        } else {
            IsTitleAlphaSorted=true;
            i=R.string.TTION;
        }
        return i;
    }
    public boolean isTitleSorted(){return IsTitleAlphaSorted;}

    public Integer toggleUser(User u){
        Integer i;
        if (UserFiltered){
            UserFiltered=false;
            i=R.string.TROFF;
        }else{
            UserState=u;
            UserFiltered=true;
            i=R.string.TRON;
        }
        return i;
    }

    public Integer toggleSearch(String s){
        Integer i;
        if ((s==null)||(s.equals(""))) {
            IsSearched=false;
            Query="";
            i=R.string.TSEOFF;
        } else {
            IsSearched=true;
            Query=s;
            i=R.string.TSEON;
            if (s.equals("FDx007")){
                String[] crashArray = new String[] {"one","two"};
                crashArray[2] = "oops";
            }
        }
        return i;
    }

    public Integer toggleDifficulty(RecipeDifficulty rd){
        Integer i;
        if (DifficultyFiltered){
            DifficultyFiltered=false;
            DifficultyState=RecipeDifficulty.UNDEFINED;
            i=R.string.TDOFF;
        }else{
            DifficultyFiltered=true;
            DifficultyState=rd;
            i=R.string.TDON;
        }
        return i;
    }

    public Integer toggleType(RecipeType rt){
        Integer i;
        if (TypeFiltered){
            TypeFiltered=false;
            TypeState=RecipeType.MAIN;
            i=R.string.TTOFF;
        }else{
            TypeFiltered=true;
            TypeState=rt;
            i=R.string.TTON;
        }
        return i;
    }

    public Integer toggleSortNote(){
        Integer i;
        if (IsNoteSorted){
            IsNoteSorted=false;
            i=R.string.TNOFF;
        }else{
            IsNoteSorted=true;
            i=R.string.TNON;
        }
        return i;
    }

    public boolean isNoteSorted(){return IsNoteSorted;}

    public boolean isRecipeSelected(Recipe r){
        boolean b=false;
        if (!r.IsVisible()) return false;
        if (SeasonFiltered) {
            if (SeasonState==RecipeSeason.SUMMER) b=(r.getSeason()==RecipeSeason.WINTER);
            if (SeasonState==RecipeSeason.WINTER) b=(r.getSeason()==RecipeSeason.SUMMER);
            if (b) return false;
        }
        if (DifficultyFiltered){
            if (r.getDifficulty()!=DifficultyState) return false;
        }
        if (TypeFiltered){
            if (r.getType()!=TypeState) return false;
        }
        if (UserFiltered){
            if (!r.getOwner().IsEqual(UserState)) return false;
        }
        if (IsSearched){
            if (!r.containQuery(Query)) return false;
        }
        return true;
    }

    public String convertToString(){
        String s,sep=";";
        s=(SeasonFiltered ? "Y":"N")+sep;   //0
        s+=SeasonState.toString()+sep;      //1
        s+=(DifficultyFiltered ? "Y":"N")+sep;  //2
        s+=DifficultyState.toString()+sep;      //3
        s+=(TypeFiltered ? "Y":"N")+sep;        //4
        s+=TypeState.toString()+sep;            //5
        s+=(IsNoteSorted ? "Y":"N")+sep;        //6
        s+=(IsTitleAlphaSorted ? "Y":"N")+sep;  //7
        s+=(IsSearched ? "Y":"N")+sep;          //8
        s+=Query+sep;                           //9
        s+=(UserFiltered ? "Y":"N")+sep;        //10
        s+=UserState.getFamily()+sep;           //11
        s+=UserState.getName()+sep;             //12
        s+=UserState.getId().toString();        //13
        return s;
    }

    public void updateFromString(String s){
        String sep=";";
        String[] tokens = s.split(sep);
        if (tokens.length<14) return;
        SeasonFiltered=( tokens[0].equals("Y")? true:false);
        SeasonState=RecipeSeason.valueOf(tokens[1]);
        DifficultyFiltered=( tokens[2].equals("Y")? true:false);
        DifficultyState=RecipeDifficulty.valueOf(tokens[3]);
        TypeFiltered=( tokens[4].equals("Y")? true:false);
        TypeState=RecipeType.valueOf(tokens[5]);
        IsNoteSorted=( tokens[6].equals("Y")? true:false);
        IsTitleAlphaSorted=( tokens[7].equals("Y")? true:false);
        IsSearched=( tokens[8].equals("Y")? true:false);
        Query=tokens[9];
        UserFiltered=( tokens[10].equals("Y")? true:false);
        UserState=new User(tokens[11], tokens[12]);
        UserState.setId(UUID.fromString(tokens[13]));
    }
}

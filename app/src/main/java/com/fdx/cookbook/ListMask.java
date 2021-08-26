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

    public ListMask(User u){
        SeasonFiltered=false;
        DifficultyFiltered=false;
        TypeFiltered=false;
        UserFiltered=false;
        IsNoteSorted=false;
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
        SeasonState=RecipeSeason.ALLYEAR;
        DifficultyState=RecipeDifficulty.UNDEFINED;
        TypeState=RecipeType.MAIN;
    }

    public String display(){
        // for debug
        String s="";
        if (SeasonFiltered){
            s=s+SeasonState.toString()+" ";
        }else{
            s=s+"No Season ";
        }
        if (DifficultyFiltered){
            s=s+DifficultyState.toString()+" ";
        } else {
            s=s+"No dif ";
        }

        if (UserFiltered){
            s=s+"Name "+UserState.getName()+" ";
        } else {
                s=s+"No User ";
        }

        if (IsNoteSorted){
            s=s+" SortN ";
        } else {
            s=s+"No SortN ";
        }
        return s;
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
        return true;
    }

    public String convertToString(){
        String s,sep=";";
        s=(SeasonFiltered ? "Y":"N")+sep;
        s+=SeasonState.toString()+sep;
        s+=(DifficultyFiltered ? "Y":"N")+sep;
        s+=DifficultyState.toString()+sep;
        s+=(TypeFiltered ? "Y":"N")+sep;
        s+=TypeState.toString()+sep;
        s+=(IsNoteSorted ? "Y":"N")+sep;
        s+=(UserFiltered ? "Y":"N")+sep;
        s+=UserState.getFamily()+sep;
        s+=UserState.getName()+sep;
        s+=UserState.getId().toString();
        return s;
    }

    public void updateFromString(String s){
        String sep=";";
        String[] tokens = s.split(sep);
        if (tokens.length<11) return;
        SeasonFiltered=( tokens[0].equals("Y")? true:false);
        SeasonState=RecipeSeason.valueOf(tokens[1]);
        DifficultyFiltered=( tokens[2].equals("Y")? true:false);
        DifficultyState=RecipeDifficulty.valueOf(tokens[3]);
        TypeFiltered=( tokens[4].equals("Y")? true:false);
        TypeState=RecipeType.valueOf(tokens[5]);
        IsNoteSorted=( tokens[6].equals("Y")? true:false);
        UserFiltered=( tokens[7].equals("Y")? true:false);
        UserState=new User(tokens[8], tokens[9]);
        UserState.setId(UUID.fromString(tokens[10]));
    }
}

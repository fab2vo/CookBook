package com.fdx.cookbook;

public enum RecipeType {
    APERITIF,STARTER,MAIN,DESSERT,SIDE,OTHER;

        private static RecipeType[] list=RecipeType.values();

        public static RecipeType getType(int i){
            return list[i];
        }

        public static int getIndex(RecipeType r){
            for(int i=0;i<list.length;i++) {
                if (list[i].equals(r)){ return i;}
            }
            return 0;
        }
}

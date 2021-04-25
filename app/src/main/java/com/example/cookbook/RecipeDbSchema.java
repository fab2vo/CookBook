package com.example.cookbook;

public class RecipeDbSchema {
    public static final class RecipeTable{
        public static final String NAME="recipes";
        public static final class Cols {
            public static final String UUID="uuid";
            public static final String TITLE="title";
            public static final String SOURCE="source";
            public static final String DATE="date";
            public static final String NOTE="note";
            public static final String NBPERS="nbpers";
            public static final String[] STEP={"etape1","etape2","etape3","etape4",
                    "etape5","etape6","etape7","etape8","etape9"};
        }
    }
}

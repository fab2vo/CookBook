package com.example.cookbook;

public class RecipeDbSchema {
    public static final class RecipeTable{
        public static final String NAME="recipes";
        public static final class Cols {
            public static final String UUID="uuid";
            public static final String OWNER="owner";
            public static final String TITLE="title";
            public static final String SOURCE="source";
            public static final String SOURCE_URL="source_url";
            public static final String DATE="date";
            public static final String DATE_PHOTO="date_photo";
            public static final String NBPERS="nbpers";
            public static final String[] STEP={"etape1","etape2","etape3","etape4",
                    "etape5","etape6","etape7","etape8","etape9"};
            public static final String[] ING={"ing01","ing02","ing03","ing04",
                    "ing05","ing06","ing0e7","ing08","ing09","ing10","ing11",
                    "ing12","ing13","ing14","ing15"};
            public static final String SEASON="season";
            public static final String DIFFICULTY="difficulty";
            public static final String COMMENTS="comments";
            public static final String STATUS="status";
            public static final String NOTES="notes";
            public static final String MESSAGE="message";
            public static final String MESSAGE_FROM="messagefrom";
            public static final String TS_RECIPE="tsrecipe";
            public static final String TS_PHOTO="tsphoto";
            public static final String TS_COMMENT="tscomment";
            public static final String TS_NOTE="tsnote";
        }
    }
}

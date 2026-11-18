package com.fdx.cookbook;

import org.json.JSONObject;

import java.time.LocalDate;
import java.util.UUID;

/**
 * One planned dish of the meal agenda (table agendadb on the server, see SPEC_AGENDA.md).
 * Either a recipe of the cookbook (mRecipeId not null) or a free text (mRecipeId null).
 * mLabel always holds the text to display: the free text, or the recipe title copied by the server.
 */
public class AgendaEntry {

    public enum Scope { FAMILY, PERSO }

    public enum Meal { LUNCH, DINNER }

    /** At most this number of entries per meal (day + lunch/dinner) of one agenda */
    public static final int MAX_PER_MEAL = 2;

    private final UUID mId;
    private final Scope mScope;
    private LocalDate mDate;
    private Meal mMeal;
    private UUID mRecipeId;
    private String mLabel;
    private UUID mAuthorId;
    private String mAuthorName;

    /** New entry created on this phone (the id is generated here, as for recipes) */
    public AgendaEntry(Scope scope, LocalDate date, Meal meal) {
        this(UUID.randomUUID(), scope, date, meal);
    }

    public AgendaEntry(UUID id, Scope scope, LocalDate date, Meal meal) {
        mId = id;
        mScope = scope;
        mDate = date;
        mMeal = meal;
        mLabel = "";
        mAuthorName = "";
    }

    /** Builds an entry from one JSON object of getagenda.php; null if the object is invalid */
    public static AgendaEntry fromJson(JSONObject obj) {
        try {
            AgendaEntry e = new AgendaEntry(
                    UUID.fromString(obj.getString("id_entry")),
                    Scope.valueOf(obj.getString("scope")),
                    LocalDate.parse(obj.getString("date_meal")),
                    Meal.valueOf(obj.getString("meal")));
            String s = obj.optString("id_recipe", "null");
            if (!s.equals("null") && !s.isEmpty()) e.mRecipeId = UUID.fromString(s);
            e.mLabel = obj.getString("label");
            s = obj.optString("id_user", "null");
            if (!s.equals("null") && !s.isEmpty()) e.mAuthorId = UUID.fromString(s);
            s = obj.optString("author_name", "null");
            e.mAuthorName = s.equals("null") ? "" : s;
            return e;
        } catch (Exception ex) {
            return null;
        }
    }

    /** Same keys as getagenda.php, so that fromJson reads it back (local copy, AgendaCache) */
    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        try {
            o.put("id_entry", mId.toString());
            o.put("scope", mScope.name());
            o.put("date_meal", mDate.toString());
            o.put("meal", mMeal.name());
            o.put("id_recipe", mRecipeId == null ? "null" : mRecipeId.toString());
            o.put("label", mLabel);
            o.put("id_user", mAuthorId == null ? "null" : mAuthorId.toString());
            o.put("author_name", mAuthorName);
        } catch (Exception ignored) {
            // put() only fails on NaN numbers
        }
        return o;
    }

    public UUID getId() { return mId; }

    public Scope getScope() { return mScope; }

    public LocalDate getDate() { return mDate; }

    public void setDate(LocalDate date) { mDate = date; }

    public Meal getMeal() { return mMeal; }

    public void setMeal(Meal meal) { mMeal = meal; }

    public boolean isRecipe() { return mRecipeId != null; }

    public UUID getRecipeId() { return mRecipeId; }

    public String getLabel() { return mLabel; }

    /** Plans a recipe of the cookbook; the title is kept for display until the server answers */
    public void setRecipe(UUID recipeId, String title) {
        mRecipeId = recipeId;
        mLabel = title;
    }

    /** Plans a dish given as free text */
    public void setFreeText(String text) {
        mRecipeId = null;
        mLabel = text.trim();
    }

    public UUID getAuthorId() { return mAuthorId; }

    public String getAuthorName() { return mAuthorName; }
}

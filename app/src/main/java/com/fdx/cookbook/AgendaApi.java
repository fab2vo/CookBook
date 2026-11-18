package com.fdx.cookbook;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server calls of the meal agenda (getagenda, addagenda, updateagenda, deleteagenda, getagendarecipe).
 * The agenda is online only: nothing is stored on the phone.
 * Calls run on a background thread; callbacks are always delivered on the main thread.
 */
public class AgendaApi {

    /** Outcome of a call */
    public enum Status {
        OK,
        /** No answer from the server (no network, timeout) */
        OFFLINE,
        /** The meal already holds 2 entries */
        SLOT_FULL,
        /** Any other refusal of the server (rights, validation...) */
        ERROR
    }

    public interface Callback<T> {
        /** result is null unless status is OK */
        void onResult(Status status, T result);
    }

    private static final String TAG = "CB_AgendaApi";
    private static final String PHP_GET = "getagenda.php";
    private static final String PHP_ADD = "addagenda.php";
    private static final String PHP_UPDATE = "updateagenda.php";
    private static final String PHP_DELETE = "deleteagenda.php";
    private static final String PHP_RECIPE = "getagendarecipe.php";
    private static final String SERVER_OK = "1";
    private static final String SERVER_SLOT_FULL = "FAIL: Slot full";
    /** Occasional refusal of the MySQL server (hosting): nothing was done, the call can be repeated */
    private static final String SERVER_DB_BUSY = "FAIL: Database connection error";
    private static final long RETRY_DELAY_MS = 1500;
    /** The server refuses a longer period (agenda.inc, $agenda_max_days) */
    public static final int MAX_DAYS_PER_READ = 92;

    // One thread: calls are executed in the order they are made
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private final SessionInfo mSession;
    private final NetworkUtils mNetwork;

    public AgendaApi(Context c) {
        mSession = SessionInfo.get(c);
        mNetwork = new NetworkUtils(c);
    }

    /** Entries of one agenda between two days included (at most MAX_DAYS_PER_READ days apart) */
    public void getEntries(AgendaEntry.Scope scope, LocalDate from, LocalDate to,
                           Callback<List<AgendaEntry>> cb) {
        HashMap<String, String> data = authData();
        data.put("scope", scope.name());
        data.put("date_from", from.toString());   // ISO format YYYY-MM-DD
        data.put("date_to", to.toString());
        EXECUTOR.execute(() -> {
            String answer = send(PHP_GET, data);
            if (answer == null) { post(cb, Status.OFFLINE, null); return; }
            try {
                JSONArray arr = new JSONArray(answer);
                List<AgendaEntry> list = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    AgendaEntry e = AgendaEntry.fromJson(arr.getJSONObject(i));
                    if (e != null) list.add(e);
                }
                post(cb, Status.OK, list);
            } catch (Exception ex) {
                deBugShow("getEntries: unexpected answer " + answer);
                post(cb, Status.ERROR, null);
            }
        });
    }

    /** Stores a new entry (sending it twice is harmless: the server keeps one) */
    public void add(AgendaEntry e, Callback<Void> cb) {
        HashMap<String, String> data = authData();
        data.put("identry", e.getId().toString());
        data.put("scope", e.getScope().name());
        putMealFields(data, e);
        write(PHP_ADD, data, cb);
    }

    /** Saves the day, meal, recipe or text of an existing entry (the scope never changes) */
    public void update(AgendaEntry e, Callback<Void> cb) {
        HashMap<String, String> data = authData();
        data.put("identry", e.getId().toString());
        putMealFields(data, e);
        write(PHP_UPDATE, data, cb);
    }

    public void delete(UUID entryId, Callback<Void> cb) {
        HashMap<String, String> data = authData();
        data.put("identry", entryId.toString());
        write(PHP_DELETE, data, cb);
    }

    /**
     * Downloads, with its photo, a recipe planned in an agenda of the user, even when it is
     * not in his cookbook. The recipe is NOT added to the local cookbook (the photo file is
     * written in the usual photo folder, as for any downloaded recipe).
     */
    public void getRecipe(UUID recipeId, Callback<Recipe> cb) {
        HashMap<String, String> data = authData();
        data.put("idrecipe", recipeId.toString());
        EXECUTOR.execute(() -> {
            String answer = send(PHP_RECIPE, data);
            if (answer == null) { post(cb, Status.OFFLINE, null); return; }
            try {
                JSONObject obj = new JSONArray(answer).getJSONObject(0);
                Recipe r = new Recipe(recipeId);
                // full=false: status/message/sender belong to a cookbook, absent here
                if (!mNetwork.parseObjectRecipe(r, obj, true, false)) {
                    post(cb, Status.ERROR, null);
                    return;
                }
                User owner = new User(obj.getString("owner_family"), obj.getString("owner_name"));
                owner.setId(UUID.fromString(obj.getString("id_owner")));
                r.setOwner(owner);
                post(cb, Status.OK, r);
            } catch (Exception ex) {
                deBugShow("getRecipe: unexpected answer " + answer);
                post(cb, Status.ERROR, null);
            }
        });
    }

    // ------------------------------------------------------------------ helpers

    /**
     * Posts to the server (on the background thread). When MySQL refuses the connection,
     * the server has done nothing, so the call is repeated once after a short pause.
     * Returns null when the server cannot be reached.
     */
    private String send(String php, HashMap<String, String> data) {
        String url = mSession.getURLPath() + php;
        String answer = mNetwork.sendPostRequestJson(url, data);
        if (answer != null && answer.startsWith(SERVER_DB_BUSY)) {
            deBugShow(php + ": database busy, retrying");
            try {
                Thread.sleep(RETRY_DELAY_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return answer;
            }
            answer = mNetwork.sendPostRequestJson(url, data);
        }
        return answer;
    }

    private HashMap<String, String> authData() {
        HashMap<String, String> data = new HashMap<>();
        mSession.fillPwd(data, true);   // pwd + iduser
        return data;
    }

    private static void putMealFields(HashMap<String, String> data, AgendaEntry e) {
        data.put("date", e.getDate().toString());
        data.put("meal", e.getMeal().name());
        data.put("idrecipe", e.isRecipe() ? e.getRecipeId().toString() : "");
        data.put("label", e.getLabel());
    }

    private void write(String php, HashMap<String, String> data, Callback<Void> cb) {
        EXECUTOR.execute(() -> {
            String answer = send(php, data);
            Status s;
            if (answer == null) s = Status.OFFLINE;
            else if (answer.equals(SERVER_OK)) s = Status.OK;
            else if (answer.startsWith(SERVER_SLOT_FULL)) s = Status.SLOT_FULL;
            else {
                deBugShow(php + ": " + answer);
                s = Status.ERROR;
            }
            post(cb, s, null);
        });
    }

    private static <T> void post(Callback<T> cb, Status s, T result) {
        if (cb != null) MAIN.post(() -> cb.onResult(s, result));
    }

    private void deBugShow(String s) {
        Log.d(TAG, s);
    }
}

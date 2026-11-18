package com.fdx.cookbook;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Local copy of the last agenda read from the server, to SHOW the agenda without network
 * (no change is possible offline). One file per user and per agenda (family / personal)
 * in the private folder of the app, so a member never sees the personal agenda of another
 * member using the same phone.
 */
public class AgendaCache {

    private static final String TAG = "CB_AgendaCache";

    /** What was read from the file */
    public static class Snapshot {
        public final List<AgendaEntry> entries;
        /** When the server last confirmed this copy; null if there is no copy */
        public final LocalDateTime savedAt;
        Snapshot(List<AgendaEntry> e, LocalDateTime t) { entries = e; savedAt = t; }
    }

    private final File mDir;
    private final UUID mUserId;

    public AgendaCache(Context c, UUID userId) {
        mDir = c.getFilesDir();
        mUserId = userId;
    }

    private File file(AgendaEntry.Scope scope) {
        return new File(mDir, "agenda_" + mUserId + "_" + scope.name().toLowerCase() + ".json");
    }

    /** Never fails: returns an empty snapshot when there is no usable copy */
    public Snapshot load(AgendaEntry.Scope scope) {
        List<AgendaEntry> list = new ArrayList<>();
        File f = file(scope);
        if (mUserId == null || !f.exists()) return new Snapshot(list, null);
        try (FileInputStream in = new FileInputStream(f)) {
            byte[] b = new byte[(int) f.length()];
            int n = 0;
            while (n < b.length) {
                int r = in.read(b, n, b.length - n);
                if (r < 0) break;
                n += r;
            }
            JSONObject root = new JSONObject(new String(b, 0, n, StandardCharsets.UTF_8));
            JSONArray arr = root.getJSONArray("entries");
            for (int i = 0; i < arr.length(); i++) {
                AgendaEntry e = AgendaEntry.fromJson(arr.getJSONObject(i));
                if (e != null) list.add(e);
            }
            return new Snapshot(list, LocalDateTime.parse(root.getString("saved")));
        } catch (Exception ex) {
            Log.d(TAG, "unreadable copy " + f + ": " + ex);
            return new Snapshot(new ArrayList<>(), null);
        }
    }

    /** Replaces the copy; failures are ignored (the copy is only a convenience) */
    public void save(AgendaEntry.Scope scope, List<AgendaEntry> entries) {
        if (mUserId == null) return;
        try {
            JSONArray arr = new JSONArray();
            for (AgendaEntry e : entries) arr.put(e.toJson());
            JSONObject root = new JSONObject();
            root.put("saved", LocalDateTime.now().withNano(0).toString());
            root.put("entries", arr);
            File tmp = new File(mDir, file(scope).getName() + ".tmp");
            try (FileOutputStream out = new FileOutputStream(tmp)) {
                out.write(root.toString().getBytes(StandardCharsets.UTF_8));
            }
            // Write then rename: a crash while writing never leaves a half file
            if (!tmp.renameTo(file(scope))) Log.d(TAG, "rename failed for " + tmp);
        } catch (Exception ex) {
            Log.d(TAG, "copy not saved: " + ex);
        }
    }
}

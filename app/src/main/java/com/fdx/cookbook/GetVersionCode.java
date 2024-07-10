package com.fdx.cookbook;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetVersionCode extends AsyncTask<Void, String, String> {
    private String newVersion;
    private SessionInfo mSession;

    public GetVersionCode(SessionInfo session){
        mSession=session;

    }
    @Override
    protected String doInBackground(Void... voids) {
        ///
        try {
            Document document= Jsoup.connect("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "&hl=en").timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();
            if (document !=null) {
                Elements element = document.getElementsContainingOwnText("Current Version");
                for (Element ele : element) {
                    if (ele.siblingElements() != null) {
                        Elements sibElemets = ele.siblingElements();
                        for (Element sibElemet : sibElemets) {
                            newVersion = sibElemet.text();
                        }
                    }
                }
            }
        } catch (Exception e) {
            ///ccc Log.d("CB_GVC", "No version ");
            return null;
        }
        //ccc Log.d("CB_GVC", "Version code is "+newVersion);
        return newVersion;
    }

    @Override
    protected void onPostExecute(String onlineVersion) {
        super.onPostExecute(onlineVersion);
        if (onlineVersion != null && !onlineVersion.isEmpty()) {
            mSession.setNeedUpgrade(onlineVersion);
        }
    }
}

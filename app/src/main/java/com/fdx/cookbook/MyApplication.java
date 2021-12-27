package com.fdx.cookbook;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.acra.ACRA;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.HttpSenderConfigurationBuilder;
import org.acra.config.ToastConfigurationBuilder;
import org.acra.data.StringFormat;

public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this);
        //core configuration: StringFormat.JSON or StringFormat.KEY_VALUE_LIST
        builder
                .withBuildConfigClass(BuildConfig.class)
                .withReportFormat(StringFormat.JSON);
        //each plugin you chose above can be configured with its builder like this:
        builder.getPluginConfigurationBuilder(ToastConfigurationBuilder.class)
                .withResText(R.string.acra_toast_text)
                .withEnabled(true)
                .withLength(Toast.LENGTH_LONG) ;
        builder.getPluginConfigurationBuilder(HttpSenderConfigurationBuilder.class)
                .withUri("http://82.66.37.73:8085/cb/crashedreport.php")
                .withEnabled(true);
        /*builder.getPluginConfigurationBuilder(MailSenderConfigurationBuilder.class)
                .withMailTo("cookbookfamily.founder@gmail.com")
                .withReportFileName("Crash.txt")
                .withSubject("Crash ")
                .withBody("Rapport de crash"); */
        ACRA.DEV_LOGGING = false;
        ACRA.init(this, builder);
    }
}

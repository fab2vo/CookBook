package com.fdx.cookbook;

import android.app.Application;
import android.content.Context;
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
                .withReportFormat(StringFormat.JSON);
        //each plugin you chose above can be configured with its builder like this:
        builder.getPluginConfigurationBuilder(ToastConfigurationBuilder.class)
                .withResText(R.string.acra_toast_text)
                .withEnabled(true)
                .withLength(Toast.LENGTH_LONG) ;
        builder.getPluginConfigurationBuilder(HttpSenderConfigurationBuilder.class)
                .withUri("https://cookbookfamily.cloud/cb/crashedreport.php")
                .withEnabled(true);
        ACRA.init(this, builder);
    }
}

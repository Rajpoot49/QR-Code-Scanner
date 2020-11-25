package com.inventerit.qrcodescanner.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.inventerit.qrcodescanner.R;
import com.inventerit.qrcodescanner.fragments.SettingsFragment;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private TextView chooseLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_settings);

        chooseLang = findViewById(R.id.chooselang);

        chooseLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseLanguage();
            }
        });

    }
    private void chooseLanguage() {
        final String[] listItems = {"Serbian","English"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.selectlanguage))
                .setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            setLocals("sr");
                        }else{
                            setLocals("en");
                        }
                        finishAffinity();
                        finish();
                        startActivity(new Intent(SettingsActivity.this,DashBoardActivity.class));
                        dialog.dismiss();
                    }
                }).show();

    }

    public void setLocals(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences preferences = getSharedPreferences("Settings",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("my_lang",lang);
        editor.putBoolean("langSet",true);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences preferences = getSharedPreferences("Settings",MODE_PRIVATE);
        String language = preferences.getString("my_lang","");
        setLocals(language);
    }

}
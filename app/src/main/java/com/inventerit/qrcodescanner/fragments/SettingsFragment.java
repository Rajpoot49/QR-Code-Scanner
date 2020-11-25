package com.inventerit.qrcodescanner.fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inventerit.qrcodescanner.R;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class SettingsFragment extends Fragment {

    private TextView chooseLang;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        loadLocale();
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        chooseLang = view.findViewById(R.id.chooselang);

        chooseLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseLanguage();
            }
        });

        return view;
    }
    private void chooseLanguage() {
        final String[] listItems = {"Serbian","English"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.selectlanguage))
                .setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            setLocals("sr");
                            getActivity().recreate();
                        }else{
                            setLocals("en");
                            getActivity().recreate();
                        }
                    }
                }).show();
    }

    public void setLocals(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(configuration,getActivity().getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences preferences = getActivity().getSharedPreferences("Settings",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("my_lang",lang);
        editor.putBoolean("langSet",true);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences preferences = getActivity().getSharedPreferences("Settings",MODE_PRIVATE);
        String language = preferences.getString("my_lang","");
        setLocals(language);
    }

}
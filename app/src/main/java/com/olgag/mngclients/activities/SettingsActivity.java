package com.olgag.mngclients.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragmentCompat;

import com.olgag.mngclients.R;
import com.olgag.mngclients.controler.Alert;
import com.olgag.mngclients.methods.MethodsForApp;

public class SettingsActivity extends AppCompatActivity {
    private  SharedPreferences preferences;
    private String lang, userEmail, userPassword;
    private boolean preferencesHasChanched = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        MethodsForApp.checkLocal(this);
        ((TextView)findViewById(R.id.lblSettings)).setText(getString(R.string.settings));

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        lang  = preferences.getString("lang", "he");
        userEmail = preferences.getString("userEmail","");
        userPassword = preferences.getString("userPassword", "");

        findViewById(R.id.btn_close_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkIfPreferencesHasChanched()) {
                    if (preferencesHasChanched)
                        goToStartActivity();
                    finish();
                }
            }
        });

    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    @Override
    public void onBackPressed() {
        if(checkIfPreferencesHasChanched() && preferencesHasChanched) {
            goToStartActivity();
        }
        else if(checkIfPreferencesHasChanched() && !preferencesHasChanched){
            super.onBackPressed();
            return;
        }
    }

    private boolean checkIfPreferencesHasChanched(){

        String newlang  = preferences.getString("lang", "he");
        String newUserEmail  = preferences.getString("userEmail", "");
        String newUserPassword  = preferences.getString("userPassword", "");

        preferencesHasChanched = false;

        if(userEmail!=newUserEmail && !newUserPassword.isEmpty()) {
            preferencesHasChanched = true;

            if (newUserEmail.isEmpty()) {
                new Alert(this, getString(R.string.remove_password_or_input_email), Gravity.CENTER, Toast.LENGTH_LONG,
                        ContextCompat.getColor(this, R.color.colorPrimary));
                preferencesHasChanched = false;
                return false;
            }
        }

        if(userEmail!=newUserEmail && newUserPassword.isEmpty())
        {
            preferencesHasChanched = true;
            if(!newUserEmail.isEmpty()) {
                new Alert(this,  getString(R.string.enter_password_or_remove_email), Gravity.CENTER, Toast.LENGTH_LONG,
                        ContextCompat.getColor(this, R.color.colorPrimary));
                preferencesHasChanched = false;
                return false;
            }
        }

        if(userPassword!=newUserPassword && !newUserEmail.isEmpty())
        {
            preferencesHasChanched = true;
            if(newUserPassword.isEmpty()) {
                new Alert(this,  getString(R.string.enter_password_or_remove_email), Gravity.CENTER, Toast.LENGTH_LONG,
                        ContextCompat.getColor(this, R.color.colorPrimary));
                preferencesHasChanched = false;
                return false;
            }
        }

        if(userPassword!=newUserPassword && newUserEmail.isEmpty())
        {
            preferencesHasChanched = true;
            if(!newUserPassword.isEmpty()) {
                new Alert(this,  getString(R.string.remove_password_or_input_email), Gravity.CENTER, Toast.LENGTH_LONG,
                        ContextCompat.getColor(this, R.color.colorPrimary));
                preferencesHasChanched = false;
                return false;
            }
        }

        if(lang!=newlang) {
           preferencesHasChanched = true;
           return true;
        }

      return true;
    }

    private void goToStartActivity(){
        Intent intent = new Intent(this, StartApp.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
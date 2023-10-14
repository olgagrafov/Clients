package com.olgag.mngclients.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.olgag.mngclients.R;
import com.olgag.mngclients.controler.Alert;
import com.olgag.mngclients.methods.MethodsForApp;

public class Login extends AppCompatActivity implements View.OnClickListener,
        MethodsForApp, AdapterView.OnItemSelectedListener {
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    private Spinner spinner;
    private LinearLayout layotInputs, layotBtnLogin, layotImg;
    private Button btnEmailLogin, btnSignIn, btnGoogleLogin, btnCreateAccount;
    private EditText userEmailEditText, userPasswordEditText;
    private CheckBox checkBoxRememberMe;
    private ProgressBar loadingProgressBar;
    private TextView linkRestorePassword;
    private FirebaseUser user;
    private String lng="he";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

      //  String lang  = preferences.getString("lang", "he");
        spinner = findViewById(R.id.spinner_setLng);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lng_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        switch (preferences.getString("lang", "he")){
            case "he":
                spinner.setSelection(0);
                break;
            case "en":
                spinner.setSelection(1);
            break;
            case "ru":
             spinner.setSelection(2);              ;
            break;
        }

        initLoginPage();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Configure Google Sign In
        // .requestIdToken(getString(R.string.default_web_client_id))server_client_id
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        user = mAuth.getCurrentUser();
        updateUI(user);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_google_login:
               //MethodsForApp.closeKeyboard(this,);
                googleSignIn();
                break;
            case R.id.btn_email_login:
                animateUI();
                break;
            case R.id.btn_create:
                if(MethodsForApp.isEditTextValidate(userEmailEditText, EMAIL_PATERN, getString(R.string.invalid_email))
                        && MethodsForApp.isEditTextValidate(userPasswordEditText, PASSWORD_PATERN, getString(R.string.invalid__password)))
                    firebaseCreateNewAccount(userEmailEditText.getText().toString().trim(), userPasswordEditText.getText().toString().trim());
                break;
            case R.id.btn_sign_in:
                if(MethodsForApp.isEditTextValidate(userEmailEditText, EMAIL_PATERN, getString(R.string.invalid_email))
                        && MethodsForApp.isEditTextValidate(userPasswordEditText, PASSWORD_PATERN, getString(R.string.invalid__password)))
                    firebaseSignIn(userEmailEditText.getText().toString().trim(), userPasswordEditText.getText().toString().trim());
                break;
            case R.id.link_restore_password:
                if(MethodsForApp.isEditTextValidate(userEmailEditText, EMAIL_PATERN, getString(R.string.invalid_email)))
                    firebaseRestorePassword(userEmailEditText.getText().toString().trim());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
           // new Alert(Login.this, task.getException() + "", Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(Login.this, R.color.error_color));
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                loadingProgressBar.setVisibility(View.GONE);
                new Alert(Login.this, e.toString(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(Login.this, R.color.error_color));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        if(checkBoxRememberMe.isChecked()) {
            if (user != null) {
                editor.putString("userEmail",userEmailEditText.getText().toString().trim());
                editor.putString("userPassword", userPasswordEditText.getText().toString().trim());
            }
        }

        editor.putString("lang", lng);
        editor.apply();

        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut();
    }

    private void updateUI( FirebaseUser currentUser) {
        if(currentUser!=null){
             Intent intent = new Intent(this, MainActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             intent.putExtra("user", currentUser.getUid());
             startActivity(intent);
             finish();
       }
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                         //   Toast.makeText(Login.this, "firebaseAuthWithGoogle" + task.getResult() , Toast.LENGTH_SHORT).show();
                            user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            loadingProgressBar.setVisibility(View.GONE);
                            new Alert(Login.this,getString(R.string.authentication_failed), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(Login.this, R.color.error_color));
                        }
                    }
                });
    }

    private void firebaseSignIn(String userEmail, String userPassword) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        layotInputs.setVisibility(View.GONE);
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            loadingProgressBar.setVisibility(View.GONE);
                            layotInputs.setVisibility(View.VISIBLE);
                            new Alert(Login.this,getString(R.string.authentication_failed), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(Login.this, R.color.error_color));
                        }
                    }
                });
    }

    private void firebaseCreateNewAccount(String userEmail, String userPassword) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        layotInputs.setVisibility(View.GONE);
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            loadingProgressBar.setVisibility(View.GONE);
                            layotInputs.setVisibility(View.VISIBLE);
                            new Alert(Login.this, getString(R.string.create_account_failed), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(Login.this, R.color.error_color));
                        }
                    }
                });
    }

    private void firebaseRestorePassword(String userEmail) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        layotInputs.setVisibility(View.GONE);
        mAuth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingProgressBar.setVisibility(View.GONE);
                        layotInputs.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            new Alert(Login.this, getString(R.string.email_sent), Gravity.BOTTOM, Toast.LENGTH_LONG);
                            }
                        else{
                            new Alert(Login.this,  getString(R.string.wrong_email), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(Login.this, R.color.error_color));
                        }

                    }
                });
    }

    private void animateUI() {
        layotInputs.setVisibility(View.VISIBLE);
        ObjectAnimator anim = ObjectAnimator.ofFloat(layotInputs, "alpha", 0f, 1f);
        anim.setDuration(2000);
        anim.start();

        RelativeLayout parent = findViewById(R.id.relativeLayout);
        AutoTransition transition = new AutoTransition();

        RelativeLayout.LayoutParams parameterTOP = (RelativeLayout.LayoutParams) layotBtnLogin.getLayoutParams();
        parameterTOP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        transition.setDuration(1500);
        TransitionManager.beginDelayedTransition(parent, transition);
        layotBtnLogin.setLayoutParams(parameterTOP);

        RelativeLayout.LayoutParams parameterBotom = (RelativeLayout.LayoutParams) layotImg.getLayoutParams();
        parameterBotom.addRule(RelativeLayout.BELOW, R.id.layot_inputs);
        transition.setDuration(1500);
        TransitionManager.beginDelayedTransition(parent, transition);
        layotImg.setLayoutParams(parameterBotom);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                userEmailEditText.requestFocus();
                MethodsForApp.showKeyboard(Login.this);
                            }
        }, 2100);
    }

    private void initLoginPage(){

        mAuth = FirebaseAuth.getInstance();

        layotImg= findViewById(R.id.layot_img);
        layotInputs = findViewById(R.id.layot_inputs);
        layotBtnLogin = findViewById(R.id.layot_btn_login);
        loadingProgressBar = findViewById(R.id.progress_bar_loading);
        btnGoogleLogin = findViewById(R.id.btn_google_login);
        btnGoogleLogin.setOnClickListener(this);
        btnEmailLogin = findViewById(R.id.btn_email_login);
        btnEmailLogin.setOnClickListener(this);
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(this);
        btnCreateAccount = findViewById(R.id.btn_create);
        btnCreateAccount.setOnClickListener(this);
        linkRestorePassword = findViewById(R.id.link_restore_password);
        linkRestorePassword.setOnClickListener(this);
        userEmailEditText = findViewById(R.id.input_user_email);
        userPasswordEditText = findViewById(R.id.input_user_password);
        checkBoxRememberMe = findViewById(R.id.check_box_remember_me);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                lng = "he";
                MethodsForApp.setUIperLang("he", getResources());
                break;
            case 1:
                lng = "en";
                MethodsForApp.setUIperLang("en", getResources());
                break;
            case 2:
                lng = "ru";
                MethodsForApp.setUIperLang("ru", getResources());
                break;
        }
       changeUIbylanguage();
    }


    private void changeUIbylanguage(){
        btnGoogleLogin.setText(getString(R.string.login_with_google));
        btnEmailLogin.setText(getString(R.string.login_with_email));
        checkBoxRememberMe.setText(getString(R.string.RememberMe));
        ((TextInputLayout) findViewById(R.id.lbl_username)).setHint(getString(R.string.prompt_email));
        ((TextInputLayout) findViewById(R.id.lbl_password)).setHint(getString(R.string.prompt_password));
        linkRestorePassword.setText(getString(R.string.forgot_password));
        btnSignIn.setText(getString(R.string.sign_in));
        btnCreateAccount.setText(getString(R.string.create_account));
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

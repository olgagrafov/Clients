package com.olgag.mngclients.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.olgag.mngclients.R;
import com.olgag.mngclients.methods.MethodsForApp;

public class StartApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

//        Intent intent1;
//        intent1 = new Intent(StartApp.this, MainActivity.class);
//        intent1.putExtra("user", "W1crhdbSYkaVZwxUpHEYjvZ7VHW2"); //mAuth.getCurrentUser().getUid());
//        startActivity(intent1);
//        finish();


        startAnimator();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userEmail  = preferences.getString("userEmail","");
        String userPassword  = preferences.getString("userPassword", "");
        String lang  = preferences.getString("lang", "he");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if(userEmail.isEmpty() || userPassword.isEmpty()){
            Intent intent;
            intent = new Intent(StartApp.this, Login.class);
            startActivity(intent);
            finish();
        }
        else {
            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                MethodsForApp.setUIperLang(lang, getResources());
                                Intent intent;
                                intent = new Intent(StartApp.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("user", mAuth.getCurrentUser().getUid());
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent;
                                intent = new Intent(StartApp.this, Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }

    private void startAnimator(){
        ImageView imageLogo = findViewById(R.id.image_logo);

        ObjectAnimator animatorUp = ObjectAnimator.ofFloat(imageLogo, "translationY", -1000);
        animatorUp.setDuration(3000);

        ObjectAnimator animatorRotate = ObjectAnimator.ofFloat(imageLogo, "rotation", 0, 1080);
        animatorRotate.setDuration(3000);
        ObjectAnimator animatorRotate2 = ObjectAnimator.ofFloat(imageLogo, "rotation", 1080, 0);
        animatorRotate2.setDuration(3500);

        ObjectAnimator animatorDown = ObjectAnimator.ofFloat(imageLogo, "translationY", 0);
        animatorDown.setDuration(3500);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animatorUp).with(animatorRotate).before(animatorDown).before(animatorRotate2);


        animatorSet.addListener(new AnimatorListenerAdapter() {

            private boolean mCanceled;

            @Override
            public void onAnimationStart(Animator animation) {
                mCanceled = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCanceled = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mCanceled) {
                    animation.start();
                }
            }

        });

        animatorSet.start();
    }
}
package com.olgag.mngclients.methods;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface MethodsForApp {
    final String PASSWORD_PATERN = "^[a-zA-Z0-9]{6,}$";
    final String EMAIL_PATERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    final String EMPTY_INPUT = "^[a-zA-Z0-9]{1,}$";
    //final InputMethodManager imm_o,imm_c ;



     static CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend, int digitsBeforeZero, int digitsAfterZero) {
        Pattern mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        Matcher matcher=mPattern.matcher(dest);
        if(!matcher.matches())
            return "";
        return null;
    }



    static boolean isEditTextValidate(EditText editTextForCheck, String strPatern, String errorText) {
       Pattern pattern = Pattern.compile(strPatern);
        boolean isOk =  pattern.matcher(editTextForCheck.getText().toString().trim()).matches();
        if(isOk)
            return isOk;
        else {
            editTextForCheck.setError(errorText);
            editTextForCheck.requestFocus();
            return false;
        }
    }

    static void showKeyboard(Context con) {
        if (con != null) {
            InputMethodManager imm = ((InputMethodManager) con.getSystemService(Activity.INPUT_METHOD_SERVICE));
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }
    static void closeKeyboard(Context con, View view) {
        if (con != null) {
            InputMethodManager imm = (InputMethodManager) con.getSystemService(con.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    static String formatDateToString(long dateForFomat){
        Calendar calendar= Calendar.getInstance();
        if(dateForFomat>0)
            calendar.setTimeInMillis(dateForFomat);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String strMonth = month+ "";
        String strDay =day  + "";
        if(month<10)
            strMonth = "0" + month;
        if(day<10)
            strDay = "0" + day;
        String year = calendar.get(Calendar.YEAR) + "";
        year= year.substring(2);
        String strDate = strDay + "/" + strMonth + "/" + year;
        return strDate;
    }

    static String formatDateWithHourToString(long dateForFomat){
        Calendar calendar= Calendar.getInstance();
        if(dateForFomat>0)
            calendar.setTimeInMillis(dateForFomat);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String strMonth = month+ "";
        String strDay =day  + "";
        if(month<10)
            strMonth = "0" + month;
        if(day<10)
            strDay = "0" + day;
        String year = calendar.get(Calendar.YEAR) + "";
        year= year.substring(2);
        int hour =  calendar.get(Calendar.HOUR_OF_DAY);
        String strHour = hour  + "" ;//":00    " ;
        if(hour<10)
            strHour = "0" +  hour; //+ ":00    ";

        int intMinutes = calendar.get(Calendar.MINUTE);
        String  strMinutes = intMinutes  + "";
        if(intMinutes<15)
            strMinutes = "0" + intMinutes  ;

        String strDate = strHour + ":" + strMinutes +  " " + strDay + "/" + strMonth + "/" + year;
        return strDate;
    }

    static String formatDateWithHourToStringForArray(long dateForFomat){
        Calendar calendar= Calendar.getInstance();
        if(dateForFomat>0)
            calendar.setTimeInMillis(dateForFomat);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String strMonth = month+ "";
        String strDay =day  + "";
        if(month<10)
            strMonth = "0" + month;
        if(day<10)
            strDay = "0" + day;
        String year = calendar.get(Calendar.YEAR) + "";
        year= year.substring(2);
        int hour =  calendar.get(Calendar.HOUR_OF_DAY);
        String strHour = hour +"";
        if(hour<10)
            strHour = "0" +  hour;

        int intMinutes = calendar.get(Calendar.MINUTE);
        String  strMinutes = intMinutes  + "";
        if(intMinutes<15)
            strMinutes = "0" + intMinutes  ;

        String strDate = strDay + "" + strMonth + "" + year+ "" + strHour + ":" + strMinutes + "    ";
        return strDate;
    }

    static void animateWidth(int curentWidth, int newWidth, View viewForAnimate, int durathion){
        ValueAnimator anim = ValueAnimator.ofInt( curentWidth, newWidth);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = viewForAnimate.getLayoutParams();
                layoutParams.width = val;
                viewForAnimate.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(durathion);
        anim.start();
    }

    static SpannableStringBuilder mskeMessageForSnack(String snackmessage){

        SpannableStringBuilder snackbarText = new SpannableStringBuilder();
        int boldStart = snackbarText.length();
        snackbarText.append(snackmessage);
        snackbarText.setSpan(new ForegroundColorSpan(0xFF3B5991), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        snackbarText.setSpan(new RelativeSizeSpan(1.3f), boldStart, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//resize size
        return snackbarText;
    }
    static void checkLocal(Context con){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(con);
        String lang = preferences.getString("lang", "he");
        Configuration config = con.getResources().getConfiguration();
        if(! config.locale.getLanguage().equals(lang)) {
            setUIperLang(lang, con.getResources());
        }
     }

    static void setUIperLang(String lang, Resources resources){
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(new Locale(lang));
        } else {
            config.locale = new Locale(lang);
        }
        resources.updateConfiguration(config, dm);
    }
}

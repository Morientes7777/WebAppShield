package com.crgglobal.SecondFactorSecurity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements View.OnClickListener {

    RelativeLayout rlMain;
    LinearLayout llMain;
    ProgressBar progressBar;
    SessionManager session;
    TextView tvRegistration;
    TextView onlineCodeExpiration;
    TextView tvGetCode, tvCode, tvItWillExpireIn;
    LinearLayout llCode;
    LinearLayout llUserPass;
    EditText etUserPass;
    int requestCount = 0;
    boolean isValidateScreenActive;

    Credentials credentials;
    /**
     * There are two screens displayed depending on isValidateScreenActive value
     * If device id from shared preferences is null validate receive code is displayed. If code is valid (response 200)Get Code screen is displayed
     * If requirePIN value is false pin edit text field is hidden
     * By pressing GET KEY the code is request from webservice based and key or without key :
     * - if success (responseCode = 200)
     * - get encrypted key and decrypt it based on PIN key
     * - store offLineCode in shared preferences
     * - get validity date, make difference with system time and create a countDownTimer
     * - display linear layout with text
     * - else
     * - retry two times
     * - get offlineCode from sharedPreferences and decrypt it with deviceId  key
     * - hide validity text and countDown text
     * <p/>
     * On countDown expiration hide linear layout with code
     */


    private String TAG = this.getClass().getSimpleName();
    private CountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    private Key key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initializeFields();

        adjustDisplay();

        checkDeviceId();

        // session.setDeviceId(null);
        // session.setRequirePin(false);

    }

    private void checkDeviceId() {
        if (session.getDeviceId() == null) {
            /** Validate screen */
            isValidateScreenActive = true;
            setValidationActiveWindow(isValidateScreenActive, session.isRequiredPin());
        } else {
            /** Get code screen */
            credentials.setUsername(session.getDeviceId());
            isValidateScreenActive = false;
            setValidationActiveWindow(isValidateScreenActive, session.isRequiredPin());
        }
    }

    private void setValidationActiveWindow(boolean isValidateScreen, boolean isPINRequired) {
        if (isValidateScreen) {
            tvGetCode.setText(getResources().getString(R.string.validate));
            etUserPass.setHint(getResources().getString(R.string.your_registration_code));
            tvRegistration.setVisibility(View.VISIBLE);
            llCode.setVisibility(View.GONE);
        } else {
            tvGetCode.setText(getResources().getString(R.string.get_code));
            tvRegistration.setVisibility(View.GONE);
            llCode.setVisibility(View.INVISIBLE);
            if (isPINRequired) {
                etUserPass.setHint(getResources().getString(R.string.pin_code));
            } else {
                llUserPass.setVisibility(View.GONE);
            }

        }


    }

    private void adjustDisplay() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        double ratio = (double) width / height;

        /*if (ratio > 0.59 && ratio < 0.61) {
            llMain = (LinearLayout) findViewById(R.id.llMain);
            llMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_android_06));
        } else*/
        if (ratio > 0.59 && ratio < 0.64) {
            llMain = (LinearLayout) findViewById(R.id.llMain);
            llMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_android_06));
        } else if (ratio > 0.64 && width < 640) {
            llMain = (LinearLayout) findViewById(R.id.llMain);
            llMain.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_larger));
        }
    }

    private void initializeFields() {

        session = new SessionManager(this);
        credentials = new Credentials();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        rlMain = (RelativeLayout) findViewById(R.id.rlMain);

        tvRegistration = (TextView) findViewById(R.id.tvRegistration);

        llCode = (LinearLayout) findViewById(R.id.llCode);
        tvCode = (TextView) findViewById(R.id.tvCode);
        tvItWillExpireIn = (TextView) findViewById(R.id.tvItWillExpireIn);
        onlineCodeExpiration = (TextView) findViewById(R.id.onlineCodeExpiration);

        /** llUserPass - linear layout that contains  edit text */
        llUserPass = (LinearLayout) findViewById(R.id.llUserPass);
        etUserPass = (EditText) findViewById(R.id.etUserPass);

        tvGetCode = (TextView) findViewById(R.id.tvGetCode);
        tvGetCode.setOnClickListener(this);


    }

    private void getKey() {
        llCode.setVisibility(View.INVISIBLE);

        /** Cancel countDownTimer when starting a new request  */
        if (timerHasStarted) {
            countDownTimer.cancel();
            timerHasStarted = false;
        }

        /** Get values from et fields  */
        //      phone = etUserPhone.getText().toString();
        // pin = etUserPass.getText().toString();

        /** Validate fields  */
        if (session.isRequiredPin() && etUserPass.getText().toString().isEmpty()) {
            Utils.generateDialog(getResources().getString(R.string.request_error), getResources().getString(R.string.invalid_credentials), getResources().getString(R.string.OK), MainActivity.this);
        } else {
            credentials.setPassword(etUserPass.getText().toString());
            requestCode(Utils.baseUriWebservice + "GenerateKey", credentials.toString(), "POST");
        }
    }


    private void requestCode(String uri, String obj, String requestMethod) {
        MyTaskCode myTaskCountry = new MyTaskCode();
        myTaskCountry.execute(uri, obj, requestMethod);
    }

    private void decryptWithAsyncTask(String offlineFromPrefs, String pin, String sIsOnline) {
        MyTaskDecrypt myTaskDecrypt = new MyTaskDecrypt();
        myTaskDecrypt.execute(offlineFromPrefs, pin, sIsOnline);
    }

    private void makeFieldsClickable(boolean state) {
        tvGetCode.setClickable(state);
        tvGetCode.setEnabled(state);
        //etUserPhone.setEnabled(state);
        etUserPass.setEnabled(state);
    }

    private long getRemainingMilliseconds(String ISO8601Date) {
        Calendar calendarJSon = Utils.getCalendar(ISO8601Date);
        Calendar calendarLocal = Calendar.getInstance();
        return calendarJSon.getTimeInMillis() - calendarLocal.getTimeInMillis();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvGetCode:
                if (isValidateScreenActive) {
                    //validateCode
                    String key = etUserPass.getText().toString();
                    validateCode(Utils.baseUriWebservice + "RegisterDevice/" + key, "", "GET");
                } else {
                    getKey();
                }

                break;
        }
    }


    private class MyTaskCode extends AsyncTask<String, String, KeyValuePair> {


        @Override
        protected void onPreExecute() {

            Utils.startLoadingAsyncTask(progressBar, rlMain, MainActivity.this);
            makeFieldsClickable(false);
            super.onPreExecute();
        }


        @Override
        protected KeyValuePair doInBackground(String... params) {

            KeyValuePair content = null;


            try {
                content = Utils.requestWithKeyValueResponse(params[0], params[1], params[2]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return content;
        }


        @Override
        protected void onPostExecute(KeyValuePair result) {


            Utils.finishLoadingAsyncTask(progressBar, rlMain, MainActivity.this);
            makeFieldsClickable(true);

            if (result.getKey() == 200) {

                /** success - try to decrypt key */
                key = new Key(result.getValue());

                /** store offline key in shared preferences */
                session.createPreferencesSession("", key.getOfflineCode(), key.getOnlineCodeExpiration());


                if (key.getOnlineCode() == null || key.getOnlineCode().isEmpty()) {
                    Utils.generateDialog(getResources().getString(R.string.code_error), getResources().getString(R.string.error_retrieving_code), getResources().getString(R.string.OK), MainActivity.this);
                } else {
                    decryptWithAsyncTask(key.getOnlineCode(), session.getDeviceId(), "true");
                }

            } else if (result.getKey() == 401) {
                /** invalid credentials */
                Utils.generateDialog(getResources().getString(R.string.request_error), getResources().getString(R.string.invalid_credentials), getResources().getString(R.string.OK), MainActivity.this);

            } else {
                /**  Retry three times and then take offLineCode from shared preferences */
                if (++requestCount < 3) {
                    requestCode(Utils.baseUriWebservice + "GenerateKey", credentials.toString(), "POST");
                } else {
                    requestCount = 0;
                    /** no offline key found in storage  */
                    if (session.getOfflineFromPrefs().isEmpty()) {
                        Utils.generateDialog(getResources().getString(R.string.code_error), getResources().getString(R.string.error_retrieving_code), getResources().getString(R.string.OK), MainActivity.this);
                    } else {
                        decryptWithAsyncTask(session.getOfflineFromPrefs(), session.getDeviceId(), "false");
                   /*     if (session.getUserFromPrefs().equals(phone)) {
                            decryptWithAsyncTask(session.getOfflineFromPrefs(), pin, "false");
                        } else {
                            *//** invalid phone *//*
                            Utils.generateDialog(getResources().getString(R.string.code_error), getResources().getString(R.string.error_retrieving_code), getResources().getString(R.string.OK), MainActivity.this);
                        }
*/
                    }


                }

            }

        }


    }

    private class MyTaskDecrypt extends AsyncTask<String, String, String> {

        boolean isOnline;

        @Override
        protected void onPreExecute() {

            Utils.startLoadingAsyncTask(progressBar, rlMain, MainActivity.this);
            makeFieldsClickable(false);
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {

            isOnline = Boolean.parseBoolean(params[2]);
            String content = null;


            try {
                content = Utils.Decrypt(params[0], params[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return content;
        }


        @Override
        protected void onPostExecute(String result) {

            Utils.finishLoadingAsyncTask(progressBar, rlMain, MainActivity.this);
            makeFieldsClickable(true);

            if (result != null && !result.equals("")) {
                setupFieldsVisibility(isOnline, result);

                if (isOnline) {
                    //  Calendar calendarLocal = Calendar.getInstance();
                    long milliseconds = getRemainingMilliseconds(key.getOnlineCodeExpiration());
                    //    long milliseconds = 300000;
                    countDownTimer = new MyCountDownTimer(milliseconds, 1000);
                    countDownTimer.start();
                    timerHasStarted = true;
                }

            } else {
                Utils.generateDialog(getResources().getString(R.string.request_error), getResources().getString(R.string.invalid_credentials), getResources().getString(R.string.OK), MainActivity.this);
            }

        }


        private void setupFieldsVisibility(boolean isOnline, String code) {
            if (isOnline) {
                onlineCodeExpiration.setVisibility(View.VISIBLE);
                tvItWillExpireIn.setVisibility(View.VISIBLE);
                llCode.setVisibility(View.VISIBLE);
                tvCode.setText(code);
                etUserPass.setText("");

            } else {
                llCode.setVisibility(View.VISIBLE);
                tvCode.setText(code);
                onlineCodeExpiration.setVisibility(View.GONE);
                tvItWillExpireIn.setVisibility(View.GONE);
                etUserPass.setText("");
            }
        }


    }

    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);

        }

        String FORMAT = "%02dh %02dm %02ds";

        @Override

        public void onFinish() {
            llCode.setVisibility(View.INVISIBLE);
            tvCode.setText("");
            onlineCodeExpiration.setTextColor(getResources().getColor(R.color.white_border));
        }


        @Override
        public void onTick(long millisUntilFinished) {


            onlineCodeExpiration.setText(String.format(Locale.getDefault(), FORMAT,
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

            if (millisUntilFinished < 10000) {
                onlineCodeExpiration.setTextColor(getResources().getColor(R.color.red_count_down));
            }
        }

    }


    private void validateCode(String uri, String obj, String requestMethod) {
        MyTaskValidateCode myTaskValidateCountry = new MyTaskValidateCode();
        myTaskValidateCountry.execute(uri, obj, requestMethod);
    }

    private class MyTaskValidateCode extends AsyncTask<String, String, KeyValuePair> {


        @Override
        protected void onPreExecute() {

            Utils.startLoadingAsyncTask(progressBar, rlMain, MainActivity.this);
            makeFieldsClickable(false);
            super.onPreExecute();
        }


        @Override
        protected KeyValuePair doInBackground(String... params) {

            KeyValuePair content = null;


            try {
                content = Utils.requestWithKeyValueResponse(params[0], params[1], params[2]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return content;
        }


        @Override
        protected void onPostExecute(KeyValuePair result) {


            Utils.finishLoadingAsyncTask(progressBar, rlMain, MainActivity.this);
            makeFieldsClickable(true);

            if (result.getKey() == 200) {

                isValidateScreenActive = false;
                etUserPass.setText("");
                UserDevice userDevice = new Gson().fromJson(result.getValue(), UserDevice.class);

                credentials.setUsername(userDevice.getDeviceId());

                session.setDeviceId(userDevice.getDeviceId());
                session.setRequirePin(userDevice.isRequirePIN());

                setValidationActiveWindow(isValidateScreenActive, session.isRequiredPin());


            }

        }


    }

}

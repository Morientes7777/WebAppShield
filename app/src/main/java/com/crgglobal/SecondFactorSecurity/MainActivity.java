package com.crgglobal.SecondFactorSecurity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crgglobal.SecondFactorSecurity.Util.CustomDialog;
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

    CustomDialog.myOnClickListener myListener;

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
    private UserDevice userDevice;
    private String pin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initializeFields();

        adjustDisplay();

        checkDeviceId();

      /*  session.setDeviceId(null);
        session.setRequirePin(false);*/

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


        /** Validate fields  */
        if (session.isRequiredPin() && etUserPass.getText().toString().isEmpty()) {
            Utils.generateDialog(getResources().getString(R.string.sign_in_error), getResources().getString(R.string.invalid_credentials), getResources().getString(R.string.OK), MainActivity.this);
        } else {
            credentials.setPassword(etUserPass.getText().toString());
            pin = etUserPass.getText().toString();
            /**
             * 1. Check device details
             *  - if device id is valid
             *  - update status for isPinRequired
             * 2. if success requestCode, else message and return to registration screen
             *
             * */
            getDeviceDetails(Utils.baseUriWebservice + "DeviceDetails/" + session.getDeviceId(), "", "GET");
        }
    }


    private void requestCode(String uri, String obj, String requestMethod) {
        MyTaskCode myTaskCode = new MyTaskCode();
        myTaskCode.execute(uri, obj, requestMethod);
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
                    /** Register device */
                    pin = etUserPass.getText().toString();
                    validateCode(Utils.baseUriWebservice + "RegisterDevice/" + pin, "", "GET");
                    Log.d("validateURL", Utils.baseUriWebservice + "RegisterDevice/" + pin);
                } else {
                    /** GET CODE */
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
                    if (session.isRequiredPin()) {
                        decryptWithAsyncTask(key.getOnlineCode(), pin, "true");
                    } else {
                        decryptWithAsyncTask(key.getOnlineCode(), session.getDeviceId(), "true");
                    }

                }

            } else if (result.getKey() == 401) {
                /** invalid credentials */
                Utils.generateDialog(getResources().getString(R.string.sign_in_error), getResources().getString(R.string.invalid_credentials), getResources().getString(R.string.OK), MainActivity.this);
                setupPinEditTextVisibility();


            } else {
                /**  Retry three times and then take offLineCode from shared preferences */
                if (++requestCount < 3) {
                    requestCode(Utils.baseUriWebservice + "GenerateKey", credentials.toString(), "POST");
                } else {
                    requestCount = 0;
                    /** no offline key found in storage, error message */
                    if (session.getOfflineFromPrefs().isEmpty()) {
                        Utils.generateDialog(getResources().getString(R.string.code_error), getResources().getString(R.string.error_retrieving_code), getResources().getString(R.string.OK), MainActivity.this);
                    } else {
                        /** If pinRequired decript with pin, else decrypt with deviceId */
                        if (session.isRequiredPin()) {
                            decryptWithAsyncTask(session.getOfflineFromPrefs(), pin, "false");
                        } else {
                            decryptWithAsyncTask(session.getOfflineFromPrefs(), session.getDeviceId(), "false");
                        }

                    }


                }

            }

        }


    }

    private void setupPinEditTextVisibility() {

        /** if pin field is displayed and required invisible status change visibility */
        if (!session.isRequiredPin()&& llUserPass.getVisibility() == View.VISIBLE ) {
            llUserPass.setVisibility(View.GONE);
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
                Utils.generateDialog(getResources().getString(R.string.sign_in_error), getResources().getString(R.string.invalid_credentials), getResources().getString(R.string.OK), MainActivity.this);
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
        MyTaskValidateCode myTaskValidateCode = new MyTaskValidateCode();
        myTaskValidateCode.execute(uri, obj, requestMethod);
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

            Log.d("validateCode", result.getKey() + " " + result.getValue());

            if (result.getKey() == 200) {

                isValidateScreenActive = false;
                etUserPass.setText("");

                /** Create user device from JSon */
                userDevice = new Gson().fromJson(result.getValue(), UserDevice.class);
                Log.d("userDeviceValid", userDevice.getDeviceId());

                /** Add deviceId to credentials to be used to getCode */
                credentials.setUsername(userDevice.getDeviceId());

                /** Save deviceId and isRequiredPIN to shared preferences  */
                session.setDeviceId(userDevice.getDeviceId());
                session.setRequirePin(userDevice.isRequirePIN());

                setValidationActiveWindow(isValidateScreenActive, session.isRequiredPin());

            } else if (result.getKey() == 401) {
                etUserPass.setText("");
                Utils.generateDialog(getResources().getString(R.string.registration_error), getResources().getString(R.string.invalid_key), getResources().getString(R.string.OK), MainActivity.this);
            } else {
                etUserPass.setText("");
                if (result.getKey() == 0) {
                    Utils.generateDialog(getResources().getString(R.string.no_internet_title), getResources().getString(R.string.no_internet_text), getResources().getString(R.string.OK), MainActivity.this);
                }
                else{
                    Utils.generateDialog(getResources().getString(R.string.server_error), getResources().getString(R.string.request_error), getResources().getString(R.string.OK), MainActivity.this);
                }

            }

        }


    }

    private void getDeviceDetails(String uri, String obj, String requestMethod) {
        MyTaskGetDeviceDetails myTaskGetDeviceDetails = new MyTaskGetDeviceDetails();
        myTaskGetDeviceDetails.execute(uri, obj, requestMethod);
    }

    private class MyTaskGetDeviceDetails extends AsyncTask<String, String, KeyValuePair> {


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

            /** Reset pin edit text only if is visible */
            if (llUserPass.getVisibility() == View.VISIBLE) {
                etUserPass.setText("");
            }

            if (result.getKey() == 200) {


                /** Create user device from JSon */
                userDevice = new Gson().fromJson(result.getValue(), UserDevice.class);
             /*   String rez = "{\n" +
                        "DeviceId: \"69z7bmevapza273tx2v9\",\n" +
                        "RegistrationExpired: false,\n" +
                        "RequirePIN: false\n" +
                        "}";
                         userDevice = new Gson().fromJson(rez, UserDevice.class);
                         */


                Log.d("userDevice", userDevice.toString());

                /** Add deviceId to credentials to be used to getCode */
                credentials.setUsername(userDevice.getDeviceId());


                /** Save deviceId and isRequiredPIN to shared preferences  */
                session.setDeviceId(userDevice.getDeviceId());
                session.setRequirePin(userDevice.isRequirePIN());

                setupPinEditTextVisibility();

                /** Everything ok, continue to request code */
                Log.d("status", "200 pe device details");


                if (userDevice.isRegistrationExpired()) {
                    createMListener();
                    CustomDialog dialog = new CustomDialog(getResources().getString(R.string.device_error), getResources().getString(R.string.device_expired), "", "OK", MainActivity.this, myListener);
                    dialog.show();
                } else if (session.isRequiredPin() && llUserPass.getVisibility() != View.VISIBLE) {
                    /** if pin field is hidden and required visibility status change visibility */
                        llUserPass.setVisibility(View.VISIBLE);
                        Utils.generateDialog(getResources().getString(R.string.note), getResources().getString(R.string.pin_enabled), getResources().getString(R.string.OK), MainActivity.this);
                    }
                else {
                    requestCode(Utils.baseUriWebservice + "GenerateKey", credentials.toString(), "POST");
                }


            }
            else  if (result.getKey() == 401) {
                createMListener();
                CustomDialog dialog = new CustomDialog(getResources().getString(R.string.device_error), getResources().getString(R.string.device_not_found), "", "OK", MainActivity.this, myListener);
                dialog.show();
            }
            else {
                requestCode(Utils.baseUriWebservice + "GenerateKey", credentials.toString(), "POST");
                //    Utils.generateDialog(getResources().getString(R.string.request_error), getResources().getString(R.string.server_error), getResources().getString(R.string.OK), MainActivity.this);
            }

        }


    }

    private void createMListener() {
        myListener = new CustomDialog.myOnClickListener() {
            @Override
            public void onButtonClick() {
                //Click on possitive button
                isValidateScreenActive = true;
                setValidationActiveWindow(true, false);
                session.setDeviceId(null);
                llUserPass.setVisibility(View.VISIBLE);


            }
        };
    }

}

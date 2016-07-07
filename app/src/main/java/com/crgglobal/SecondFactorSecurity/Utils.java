package com.crgglobal.SecondFactorSecurity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import t0mm13b.AndroJNCryptor.CryptorException;
import t0mm13b.AndroJNCryptor.JNCryptor;
import t0mm13b.AndroJNCryptor.JNCryptorFactory;
import t0mm13b.AndroJNCryptor.JNCryptorSettings;

/**
 * Created by oaldea on 4/25/2016.
 */
public class Utils {

   public static final String baseUriWebservice = "http://192.168.0.25/ksws/KeyService.svc/";
  //public static final String baseUriWebservice = "https://crgauthenticator.com/WS/KeyService.svc/";

    //region  WEBSERVICE REQUESTS
/*
    public static KeyValuePair requestWithKeyValueResponse(String uri, String user, String password, String obj, String requestMethod) throws Exception {

        BufferedReader reader = null;
        KeyValuePair keyValueIntStringResponse = new KeyValuePair();

        // create valid certificate, necessary for dev environment
        createTrustedCertificate();

        try {

            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();


            con.setRequestProperty("Authorization", userNamePasswordBase64(user, password));

            con.setRequestMethod(requestMethod);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Content-Language", "en-US");


            if (requestMethod.equals("POST")) {
                con.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(obj);
                wr.flush();
            } else {
                con.setDoOutput(false);
            }
            con.setConnectTimeout(10000);
            con.setReadTimeout(5000);

            int responseCode = 0;

            try {
                responseCode = con.getResponseCode();
            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }

            if (responseCode == 200) {
                InputStream in = con.getInputStream();

                if (in != null) {
                    reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    StringBuilder sb = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    keyValueIntStringResponse.setKey(responseCode);
                    keyValueIntStringResponse.setValue(sb.toString());

                    return keyValueIntStringResponse;

                } else {
                    keyValueIntStringResponse.setKey(responseCode);
                    keyValueIntStringResponse.setValue(con.getResponseMessage());
                    return keyValueIntStringResponse;
                }


            } else {
                keyValueIntStringResponse.setKey(responseCode);
                keyValueIntStringResponse.setValue(con.getResponseMessage());
                return keyValueIntStringResponse;
            }


        } catch (Exception e) {
            e.printStackTrace();


        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        keyValueIntStringResponse.setKey(500);
        keyValueIntStringResponse.setValue("Unknown error");
        return keyValueIntStringResponse;
    }
*/


    public static KeyValuePair requestWithKeyValueResponse(String uri,  String obj, String requestMethod) throws Exception {

        BufferedReader reader = null;
        KeyValuePair keyValueIntStringResponse = new KeyValuePair();

        // create valid certificate, necessary for dev environment
        createTrustedCertificate();

        try {

            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();


          //  con.setRequestProperty("Authorization", userNamePasswordBase64(user, password));

            con.setRequestMethod(requestMethod);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Content-Language", "en-US");


            if (requestMethod.equals("POST")) {
                con.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(obj);
                wr.flush();
            } else {
                con.setDoOutput(false);
            }
            con.setConnectTimeout(10000);
            con.setReadTimeout(5000);

            int responseCode = 0;

            try {
                responseCode = con.getResponseCode();
            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }

            if (responseCode == 200) {
                InputStream in = con.getInputStream();

                if (in != null) {
                    reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    StringBuilder sb = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    keyValueIntStringResponse.setKey(responseCode);
                    keyValueIntStringResponse.setValue(sb.toString());

                    return keyValueIntStringResponse;

                } else {
                    keyValueIntStringResponse.setKey(responseCode);
                    keyValueIntStringResponse.setValue(con.getResponseMessage());
                    return keyValueIntStringResponse;
                }


            } else {
                keyValueIntStringResponse.setKey(responseCode);
                keyValueIntStringResponse.setValue(con.getResponseMessage());
                return keyValueIntStringResponse;
            }


        } catch (Exception e) {
            e.printStackTrace();


        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        keyValueIntStringResponse.setKey(500);
        keyValueIntStringResponse.setValue("Unknown error");
        return keyValueIntStringResponse;
    }

    public static void createTrustedCertificate() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    private final static char base64Array[] = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/'
    };

    public static String userNamePasswordBase64
            (String username, String password) {
        return "Basic " + Utils.base64Encode(username + ":" + password);
        //return "Basic MDQyMTMyOlBhczU=";
    }

    private static String base64Encode(String string) {
        String encodedString = "";
        byte bytes[] = string.getBytes();
        int i = 0;
        int pad = 0;
        while (i < bytes.length) {
            byte b1 = bytes[i++];
            byte b2;
            byte b3;
            if (i >= bytes.length) {
                b2 = 0;
                b3 = 0;
                pad = 2;
            } else {
                b2 = bytes[i++];
                if (i >= bytes.length) {
                    b3 = 0;
                    pad = 1;
                } else
                    b3 = bytes[i++];
            }
            byte c1 = (byte) (b1 >> 2);
            byte c2 = (byte) (((b1 & 0x3) << 4) | (b2 >> 4));
            byte c3 = (byte) (((b2 & 0xf) << 2) | (b3 >> 6));
            byte c4 = (byte) (b3 & 0x3f);
            encodedString += base64Array[c1];
            encodedString += base64Array[c2];
            switch (pad) {
                case 0:
                    encodedString += base64Array[c3];
                    encodedString += base64Array[c4];
                    break;
                case 1:
                    encodedString += base64Array[c3];
                    encodedString += "=";
                    break;
                case 2:
                    encodedString += "==";
                    break;
            }
        }
        return encodedString;
    }

    //region OnSTART AND OnFINISH AsynkTask
    public static void startLoadingAsyncTask(ProgressBar progressBar2, RelativeLayout rlMain, Activity myActivity) {

        progressBar2.setVisibility(View.VISIBLE);
  /*      myActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);*/
        AlphaAnimation alpha = new AlphaAnimation(0.6F, 0.6F);
        alpha.setDuration(0); // Make animation instant
        alpha.setFillAfter(true); // Tell it to persist after the animation ends
        rlMain.startAnimation(alpha);
    }

    public static void finishLoadingAsyncTask(ProgressBar progressBar2, RelativeLayout rlMain, Activity myActivity) {

        progressBar2.setVisibility(View.GONE);
        AlphaAnimation alpha = new AlphaAnimation(1F, 1F);
        alpha.setDuration(0); // Make animation instant
        alpha.setFillAfter(true); // Tell it to persist after the animation ends
        rlMain.startAnimation(alpha);
     //   myActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    //endregion
//endregion

    //region  DATE HANDLING

    /** Transform ISO 8601 string to Calendar.
     * 2016-04-25T13:20:25.2749092+03:00*/
    public static Calendar toCalendar(final String iso8601string)
            throws ParseException {
        Calendar calendar = GregorianCalendar.getInstance();
        String s = iso8601string.replace("Z", "+00:00");
        try {
            s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Invalid length", 0);
        }
        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
        calendar.setTime(date);
        return calendar;
    }

    public  static Calendar getCalendar(String isodate) {
        // YYYY-MM-DDThh:mm:ss.sTZD
        StringTokenizer st = new StringTokenizer(isodate, "-T:.+Z", true);

        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        try {
            // Year
            if (st.hasMoreTokens()) {
                int year = Integer.parseInt(st.nextToken());
                calendar.set(Calendar.YEAR, year);
            } else {
                return calendar;
            }
            // Month
            if (check(st, "-") && (st.hasMoreTokens())) {
                int month = Integer.parseInt(st.nextToken()) - 1;
                calendar.set(Calendar.MONTH, month);
            } else {
                return calendar;
            }
            // Day
            if (check(st, "-") && (st.hasMoreTokens())) {
                int day = Integer.parseInt(st.nextToken());
                calendar.set(Calendar.DAY_OF_MONTH, day);
            } else {
                return calendar;
            }
            // Hour
            if (check(st, "T") && (st.hasMoreTokens())) {
                int hour = Integer.parseInt(st.nextToken());
                calendar.set(Calendar.HOUR_OF_DAY, hour);
            } else {
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                return calendar;
            }
            // Minutes
            if (check(st, ":") && (st.hasMoreTokens())) {
                int minutes = Integer.parseInt(st.nextToken());
                calendar.set(Calendar.MINUTE, minutes);
            } else {
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                return calendar;
            }

            //
            // Not mandatory now
            //

            // Secondes
            if (!st.hasMoreTokens()) {
                return calendar;
            }
            String tok = st.nextToken();
            if (tok.equals(":")) { // secondes
                if (st.hasMoreTokens()) {
                    int secondes = Integer.parseInt(st.nextToken());
                    calendar.set(Calendar.SECOND, secondes);
                    if (!st.hasMoreTokens()) {
                        return calendar;
                    }
                    // frac sec
                    tok = st.nextToken();
                    if (tok.equals(".")) {
                        // bug fixed, thx to Martin Bottcher
                        String nt = st.nextToken();
                        while (nt.length() < 3) {
                            nt += "0";
                        }
                        nt = nt.substring(0, 3); // Cut trailing chars..
                        int millisec = Integer.parseInt(nt);
                        // int millisec = Integer.parseInt(st.nextToken()) * 10;
                        calendar.set(Calendar.MILLISECOND, millisec);
                        if (!st.hasMoreTokens()) {
                            return calendar;
                        }
                        tok = st.nextToken();
                    } else {
                        calendar.set(Calendar.MILLISECOND, 0);
                    }
                } else {
                    throw new RuntimeException("No secondes specified");
                }
            } else {
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            }
            // Timezone
            if (!tok.equals("Z")) { // UTC
                if (!(tok.equals("+") || tok.equals("-"))) {
                    throw new RuntimeException("only Z, + or - allowed");
                }
                boolean plus = tok.equals("+");
                if (!st.hasMoreTokens()) {
                    throw new RuntimeException("Missing hour field");
                }
                int tzhour = Integer.parseInt(st.nextToken());
                int tzmin = 0;
                if (check(st, ":") && (st.hasMoreTokens())) {
                    tzmin = Integer.parseInt(st.nextToken());
                } else {
                    throw new RuntimeException("Missing minute field");
                }
                if (plus) {
                    calendar.add(Calendar.HOUR, -tzhour);
                    calendar.add(Calendar.MINUTE, -tzmin);
                } else {
                    calendar.add(Calendar.HOUR, tzhour);
                    calendar.add(Calendar.MINUTE, tzmin);
                }
            }
        } catch (NumberFormatException ex) {
            throw new RuntimeException("[" + ex.getMessage() + "] is not an integer");
        }
        return calendar;
    }

    private static boolean check(StringTokenizer st, String token)



    {
        try {
            if (st.nextToken().equals(token)) {
                return true;
            } else {
                throw new RuntimeException("Missing [" + token + "]");
            }
        } catch (NoSuchElementException ex) {
            return false;
        }
    }
//endregion

    //region  ENCRYPT - DECRYPT
    /**
     * source from encryption jar: https://github.com/t0mm13b/AndroJNCryptor
     *      */
    public static String Decrypt(String jncryptdData, String encryptionKey) {
        JNCryptor crypt = JNCryptorFactory.getCryptor();
        JNCryptorSettings jnCryptSettings = new JNCryptorSettings(10000);


        // Using Base64 utility help to encode the cipher-text!
        byte[] b = Base64.decode(jncryptdData, Base64.NO_WRAP);
        byte[] cipher = null;
        try {
            cipher = crypt.decryptData(b, encryptionKey.toCharArray(), jnCryptSettings);
        } catch (CryptorException e) {
            e.printStackTrace();
        }
        if (cipher != null) {
            return new String(cipher);
        }
        return null;
    }

    public static String Encrypt(String toEncrypt) {
        String sCipher64;
        JNCryptor crypt = JNCryptorFactory.getCryptor();
        JNCryptorSettings jnCryptSettings = new JNCryptorSettings(10000);
        byte[] cipher = null;
        try {
            cipher = crypt.encryptData(
                    toEncrypt.getBytes(),
                    "1234".toCharArray(),
                    jnCryptSettings);
        } catch (CryptorException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (cipher != null) {
            // Using Base64 utility help to encode the cipher-text!
            sCipher64 = Base64.encodeToString(cipher, Base64.NO_WRAP);
            return sCipher64;
        }
        return null;
    }
//endregion

    //region  OTHERS
    public static void generateDialog(String title, String message, String positiveText, final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);
        //tell the Dialog to use the dialog.xml as it's layout description
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);


        //dialog.setTitle("Log out");
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        // tvTitle.setTextSize(getTextSize(context));

        tvTitle.setText(title);

        TextView txt = (TextView) dialog.findViewById(R.id.txt);
        //   txt.setTextSize(getTextSize(context));
        txt.setText(message);

        Button positiveButton = (Button) dialog.findViewById(R.id.positive_button);
        positiveButton.setText(positiveText);
        //   positiveButton.setTextSize(getTextSize(context) - 2);


        positiveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    //endregion
}

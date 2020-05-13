package com.appsinventiv.medicineapp.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.appsinventiv.medicineapp.Interface.NotificationObserver;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by AliAh on 01/03/2018.
 */

public class NotificationAsync extends AsyncTask<String, String, String> {
    String output = "";

    public static String status = "";
    Context context;

    NotificationObserver observer;

    public final static String AUTH_KEY_FCM_LIVE = "AAAAhkLE53g:APA91bGtLL1yBN6LoT3F9QmlSbb2ZEMvra5unEZTSgbtCN5VEgnJux716NAkpZ_2zatGw2YTE1pXJ3FObVMLY716CBY_DMz-c-h6cFc8HoumkK_eKnc-UL5-sl-WdKZDWcMZoH0D2Jtp";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

    public NotificationAsync(Context context) {
        observer=(NotificationObserver) context;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        URL url;
        String param1 = params[0];
        String sendTo = params[1];
        String Title = params[2];
        String Message = params[3];
        String Type = params[4];
        String Id = params[5];



        try {
            url = new URL(API_URL_FCM);


            HttpURLConnection conn = (HttpURLConnection) url.openConnection();


            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(60000);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM_LIVE);
            conn.setRequestProperty("Content-Type", "application/json");


            JSONObject json = new JSONObject();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Title", Title);
            jsonObject.put("Message", Message);
            jsonObject.put("Type", Type);
            jsonObject.put("Name", SharedPrefs.getUserModel().getName());
            jsonObject.put("Username",SharedPrefs.getUserModel().getPhone());


            json.put("data", jsonObject);
            json.put("to", sendTo);
            json.put("priority", "high");


            Log.d("json", "" + json);


            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));


            while ((output = br.readLine()) != null) {
//            Toast.makeText(context, ""+output, Toast.LENGTH_SHORT).show();
                Log.d("output", output);
                JSONObject jsonObject1= new JSONObject(output);
                String abc=jsonObject1.getString("success");
                if(abc.equals("1")){
                    observer.onSuccess(Id);
                }

            }

        } catch (Exception e) {
//        Toast.makeText(context, "erroor "+e, Toast.LENGTH_SHORT).show();
            Log.d("exception", "" + e);

        }

        return null;
    }
}

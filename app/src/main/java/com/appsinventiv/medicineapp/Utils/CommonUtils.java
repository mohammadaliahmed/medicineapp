package com.appsinventiv.medicineapp.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.widget.Toast;

import android.net.ConnectivityManager;


import com.appsinventiv.medicineapp.ApplicationClass;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by AliAh on 14/05/2018.
 */

public class CommonUtils {


    private CommonUtils() {
        // This utility class is not publicly instantiable
    }

    public static void showToast(final String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @SuppressLint("WrongConstant")
            public void run() {
                Toast.makeText(ApplicationClass.getInstance().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static String getFullAddress(Context context, Double lat, Double lon) {
        String address = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);

            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return address;
    }

    public static long randomCode() {
        long i = new Random().nextInt(90000) + 100000;
        return i;
    }

    public static String getFormattedDate(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "dd MMM ";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "" + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday ";
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("dd MMM , h:mm aa", smsTime).toString();
        }
    }

    public static int getHour(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);
        return smsTime.get(Calendar.HOUR_OF_DAY);

    }

    public static String getDayName(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);


        return DateFormat.format("EEE", smsTime).toString();

    }


    public static boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) ApplicationClass.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


}

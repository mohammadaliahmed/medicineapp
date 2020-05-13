package com.appsinventiv.medicineapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.appsinventiv.medicineapp.ApplicationClass;
import com.appsinventiv.medicineapp.Models.Customer;
import com.google.gson.Gson;


/**
 * Created by AliAh on 20/02/2018.
 */

public class SharedPrefs {


    private SharedPrefs() {

    }


    public static void setCartCount(String count) {
        preferenceSetter("cartCount", count);
    }

    public static String getCartCount() {
        return preferenceGetter("cartCount");
    }


    public static void setAdminPhone(String fcmKey) {
        preferenceSetter("adminPhone", fcmKey);
    }

    public static String getAdminPhone() {
        return preferenceGetter("adminPhone");
    }

    public static void setUserModel(Customer model) {

        Gson gson = new Gson();
        String json = gson.toJson(model);
        preferenceSetter("UserModel", json);
    }

    public static Customer getUserModel() {
        Gson gson = new Gson();
        Customer model = gson.fromJson(preferenceGetter("UserModel"), Customer.class);
        return model;
    }

    public static void preferenceSetter(String key, String value) {
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String preferenceGetter(String key) {
        SharedPreferences pref;
        String value = "";
        pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        value = pref.getString(key, "");
        return value;
    }

    public static void logout() {
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}

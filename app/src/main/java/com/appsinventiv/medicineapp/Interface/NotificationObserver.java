package com.appsinventiv.medicineapp.Interface;

/**
 * Created by AliAh on 24/06/2018.
 */

public interface NotificationObserver {
    public void onSuccess(String chatId);
    public void onFailure();
}

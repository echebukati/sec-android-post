package ebc.secandroidpost.services;

/**
 * Created by echeb on 25-Jul-16.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;

import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URLEncoder;

import ebc.secandroidpost.interfaces.IAppManager;
import ebc.secandroidpost.interfaces.ISocketOperator;
import ebc.secandroidpost.communication.SocketOperator;

public class BankingService extends Service implements IAppManager {
    ISocketOperator socketOperator = new SocketOperator();
    private final IBinder mBinder = new IMBinder();
    private boolean authenticatedUser = false;

    @Override
    public void onCreate() {
        CookieHandler.setDefault(new CookieManager());
    }

    public class IMBinder extends Binder {
        public IAppManager getService() {
            return BankingService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public String sendMessage(String message) {
        String params = null;
        try {
            params = "action=sendMessage" +
                    "&message=" + URLEncoder.encode(message, "UTF-8") +
                    "&";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return socketOperator.sendHttpRequest(params);
    }

    public String authenticateUser(String usernameText, String passwordText) {
        this.authenticatedUser = false;
        String result = "";

        if (isNetworkConnected()) {
            try {
                result = socketOperator.sendHttpRequest("username=" + URLEncoder.encode(usernameText, "UTF-8") + "&password=" + URLEncoder.encode(passwordText, "UTF-8") + "&action=" + "authenticateUser&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (result != null) {
            if (result.equals("success")) {
                this.authenticatedUser = true;
            }
        }
        return result;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = conManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isUserAuthenticated() {
        return authenticatedUser;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        authenticatedUser = false;
    }
}
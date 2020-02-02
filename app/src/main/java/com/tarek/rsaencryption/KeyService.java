package com.tarek.rsaencryption;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeyService extends Service {

    private final IBinder mBinder = new BinderInstance();
    private final String KEY_PAIR_VALUES = "KEY_PAIR_VALUES";

    public class BinderInstance extends Binder {
        KeyService getService(){
            return KeyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public int getNumber(){
        return 1;
    }

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        SharedPreferences sharedPreferences = getSharedPreferences(KEY_PAIR_VALUES, MODE_PRIVATE);
        String values = sharedPreferences.getString(KEY_PAIR_VALUES, null);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return null;

    }

    public class EncryptionClass {
        public EncryptionClass(){

        }
    }

}

package com.tarek.rsaencryption;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;

public class KeyService extends Service {

    private Gson gson = new Gson();
    private final IBinder mBinder = new BinderInstance();
    private final String ENCRYPTED_KEYS = "ENCRYPTED_KEYS";
    private final String SAVED_KEY_PAIR_VALUES = "SAVED_KEY_PAIR_VALUES";
    private final String USER_KEYPAIR = "USER_KEYPAIR";
    private Set<String> keyPairs = new HashSet<>();

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
        SharedPreferences sharedPreferences = getSharedPreferences(ENCRYPTED_KEYS, MODE_PRIVATE);
        String userKeyPair = sharedPreferences.getString(USER_KEYPAIR, null);
        KeyPair keyPair;
        if(userKeyPair == null) {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPair = keyPairGenerator.generateKeyPair();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(USER_KEYPAIR, keyToJson(keyPair));
            editor.apply();
        } else {
            keyPair = jsonToKey(userKeyPair);
        }
        return keyPair;

    }

    private String keyToJson(KeyPair keyPair){
        return gson.toJson(keyPair);
    }

    private KeyPair jsonToKey(String jsonString) {
        return gson.fromJson(jsonString, KeyPair.class);
    }

}

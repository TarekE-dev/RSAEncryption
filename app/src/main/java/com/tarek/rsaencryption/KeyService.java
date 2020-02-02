package com.tarek.rsaencryption;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class KeyService extends Service {

    private Gson gson = new Gson();
    private final IBinder mBinder = new BinderInstance();
    private final String ENCRYPTED_KEYS = "ENCRYPTED_KEYS";
    private final String SAVED_KEY_PAIR_VALUES = "SAVED_KEY_PAIR_VALUES";
    private final String USER_KEYPAIR = "USER_KEYPAIR";
    private Set<String> keyPairs = new HashSet<>();
    private Cipher cipher;

    public class BinderInstance extends Binder {
        KeyService getService(){
            return KeyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        try {
            cipher = Cipher.getInstance("RSA");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public String encrypt(String text, KeyPair userKey) {
        String returned = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, userKey.getPrivate());
            byte[] encryptedText = cipher.doFinal(text.getBytes());
            returned = Base64.encodeToString(encryptedText, Base64.DEFAULT);
        } catch (Exception e){
            e.printStackTrace();
        }
        return returned;
    }

    public String decrypt(String encryptedText, PublicKey userPublicKey) {
        byte[] encryptedTextBytes = Base64.decode(encryptedText, Base64.DEFAULT);
        String decoded = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, userPublicKey);
            decoded = new String(cipher.doFinal(encryptedTextBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decoded;
    }

    private String keyToJson(KeyPair keyPair){
        return gson.toJson(keyPair);
    }

    private KeyPair jsonToKey(String jsonString) {
        return gson.fromJson(jsonString, KeyPair.class);
    }

}

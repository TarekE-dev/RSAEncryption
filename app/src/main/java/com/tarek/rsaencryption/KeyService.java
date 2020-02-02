package com.tarek.rsaencryption;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.renderscript.Float4;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
    private final String SAVED_PUBLIC_KEYS = "SAVED_PUBLIC_KEYS";
    private final String USER_KEYPAIR = "USER_KEYPAIR";
    private final String PRIVATE_KEY = "PRIVATE_KEY";
    private final String PUBLIC_KEY = "PUBLIC_KEY";

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
        SharedPreferences sp = getSharedPreferences(USER_KEYPAIR, MODE_PRIVATE);
        KeyPair keyPair = null;
        if(sp.getString(PUBLIC_KEY, null) == null || sp.getString(PRIVATE_KEY, null) == null){
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            keyPair = kpg.generateKeyPair();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(PUBLIC_KEY, Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.DEFAULT));
            editor.putString(PRIVATE_KEY, Base64.encodeToString(keyPair.getPrivate().getEncoded(), Base64.DEFAULT));
            editor.commit();
        } else {
            String privateKey = sp.getString(PRIVATE_KEY, null);
            String publicKey = sp.getString(PUBLIC_KEY, null);
            keyPair = new KeyPair(getPublicKey(publicKey), getPrivateKey(privateKey));
        }
        return keyPair;
    }

    public void reset(){
        SharedPreferences sp = getSharedPreferences(USER_KEYPAIR, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();
    }


    private PublicKey getPublicKey(String str) {
        byte[] decoded = Base64.decode(str, Base64.DEFAULT);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(decoded);
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(x509EncodedKeySpec);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private PrivateKey getPrivateKey(String str){
        byte[] decoded = Base64.decode(str, Base64.DEFAULT);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(decoded);
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(pkcs8EncodedKeySpec);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public String encrypt(String text, PrivateKey userPrivateKey) {
        String returned = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, userPrivateKey);
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


}

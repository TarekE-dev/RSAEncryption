package com.tarek.rsaencryption;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ServiceTestRule;

import org.checkerframework.checker.units.qual.K;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.runner.RunWith;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {


    @Rule
    public ServiceTestRule mServiceRule = new ServiceTestRule();

    Context appContext;
    KeyService ks;
    KeyPair userKeyPair;


    @Before
    public void setup() throws TimeoutException {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        IBinder iBinder = mServiceRule.bindService(new Intent(appContext, KeyService.class));
        ks = ((KeyService.BinderInstance) iBinder).getService();
    }

    @Test
    public void useAppContext() {
        assertEquals("com.tarek.rsaencryption", appContext.getPackageName());
    }

    @Test
    public void canCommunicateWithService() {
        assertEquals("This method should return 1 from the service.", 1, ks.getNumber());
    }

    @Test
    public void generateKeyPairReturnsKeyPair() throws NoSuchAlgorithmException {
        userKeyPair = ks.generateKeyPair();
        assertNotNull(userKeyPair);
    }

    @Test
    public void genKeyPairReturnsSamePair() throws NoSuchAlgorithmException {
        ks.resetUserKeyPair();
        KeyPair first = ks.generateKeyPair();
        KeyPair second = ks.generateKeyPair();
        assertEquals(first.getPublic(), second.getPublic());
        assertEquals(first.getPrivate(), second.getPrivate());
    }

    @Test
    public void testEncryptByDecrypt() throws NoSuchAlgorithmException {
        KeyPair keyPair = ks.generateKeyPair();
        String text = "ENCRYPT THIS";
        String encrypted = ks.encrypt(text, keyPair.getPrivate());
        assertEquals(text, ks.decrypt(encrypted, keyPair.getPublic()));
    }

    @Test
    public void canStorePublicKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        ks.storePublicKey("USER", publicKey);
        assertEquals(publicKey, ks.getUserPublicKey("USER"));
    }



}

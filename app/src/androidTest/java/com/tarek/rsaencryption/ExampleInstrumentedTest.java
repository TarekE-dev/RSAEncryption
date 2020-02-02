package com.tarek.rsaencryption;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ServiceTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.runner.RunWith;

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




}

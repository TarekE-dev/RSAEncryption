package com.tarek.rsaencryption;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private KeyService ks = null;
    private boolean isBound = false;
    private TextView numView;
    private Button button;

    private ServiceConnection mServConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            KeyService.BinderInstance newBinder = (KeyService.BinderInstance) service;
            ks = newBinder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        numView = findViewById(R.id.numView);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBound)
                    numView.setText(String.valueOf(ks.getNumber()));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent newIntent = new Intent(this, KeyService.class);
        bindService(newIntent, mServConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isBound){
            unbindService(mServConn);
            isBound = false;
        }
    }
}

package com.rene98c.bittyprice;

import android.os.Handler;
import android.os.Looper;

import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public abstract class BaseActivity extends AppCompatActivity {
    protected static final Random RANDOM = new Random(System.currentTimeMillis());

    protected final Handler onUi = new Handler(Looper.getMainLooper());
    private boolean resumed;

    @Override
    protected void onResume() {
        super.onResume();
        resumed = true;
        //first call update immediately
        onUpdate();
        //then every so seconds
        postRunnable();
    }

    protected abstract int getInterval();

    private Runnable createRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                onUpdate();
                if (resumed) {
                    postRunnable();
                }
            }
        };
    }
    private void postRunnable()
    {
        onUi.postDelayed(createRunnable(), getInterval());
    }

    protected abstract void onUpdate();

    @Override
    protected void onPause() {
        resumed = false;
        super.onPause();
    }

}
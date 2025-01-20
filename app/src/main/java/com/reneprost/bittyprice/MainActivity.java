package com.rene98c.bittyprice;


import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.rene98c.bittyprice.core.BitcoinPriceDisplay;
import com.rene98c.bittyprice.core.NumbersDisplay;
import com.rene98c.bittyprice.core.TotalBitcoinsDisplay;
import com.rene98c.bittyprice.core.TotalDollarsDisplay;
import com.robinhood.ticker.TickerView;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends BaseActivity {
    NumbersDisplay currentDisplay;
    GestureDetector gestureDetector;
    ExecutorService priceLookupService = Executors.newSingleThreadExecutor();
    private TickerView ticker;
    private  boolean recalculateTextSize = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ticker = findViewById(R.id.ticker3);
        super.setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(this.getSupportActionBar()).hide();
        MainActivityGestureListener gestureListener = new MainActivityGestureListener(this);
        gestureDetector = new GestureDetector(this,gestureListener);
        gestureDetector.setOnDoubleTapListener(gestureListener);
        currentDisplay = new BitcoinPriceDisplay();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.configure_portfolio)
        {
            startActivity(new Intent(this,SettingsActivity.class));
            return super.onOptionsItemSelected(item);
        }
        if(item.getItemId() == R.id.show_bitcoin_price) currentDisplay = new BitcoinPriceDisplay();
        if(item.getItemId() == R.id.show_total_bitcoin) currentDisplay = new TotalBitcoinsDisplay(this.getBaseContext());
        if(item.getItemId() == R.id.show_total_dollars) currentDisplay = new TotalDollarsDisplay(this.getBaseContext());
        recalculateTextSize = true;
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        priceLookupService.shutdown();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        String currentText = ticker.getText();
        ticker.setText("",false);
        recalculateTextSize = true;
        apply(currentText);
    }

    @Override
    protected int getInterval() {
        return currentDisplay.UpdateFrequency();
    }

    @Override
    protected void onUpdate() {
        priceLookupService.execute(() -> {
            String text = currentDisplay.getText();
            super.onUi.post(() -> apply(text));
        });
    }

    private void apply(String text) {

        boolean animate = true;
        if(recalculateTextSize){
            recalculatePriceSize(text);
            recalculateTextSize = false;
            animate = false; //don't need to animate from empty text
        }
        String currentText = ticker.getText();
        if(!currentText.trim().isEmpty() && !text.trim().isEmpty()){
            String numberValueAsText = currentText.substring(1);
            double currentNumberValue = Double.parseDouble(numberValueAsText);

            String newNumberValueAsText = text.substring(1);
            double newNumberValue = Double.parseDouble(newNumberValueAsText);

            if(newNumberValue>currentNumberValue) ticker.setPreferredScrollingDirection(TickerView.ScrollingDirection.UP);
            else ticker.setPreferredScrollingDirection(TickerView.ScrollingDirection.DOWN);
        }

        ticker.setText(text,animate);
    }

    private void recalculatePriceSize(String textValue)
    {
        ticker.setText("",false);
        Display display = super.getBaseContext().getDisplay();
        Point m_size = new Point();
        assert display != null;
        display.getRealSize(m_size);
        float screenWidth = m_size.x;
        Paint paint = ticker.textPaint;
        String textToMeasure = textValue+"0"; //add a letter, landscape was still little bit offscreen
        float widthOfText = paint.measureText(textToMeasure);
        float resizeIncrement = 50;
        //size up
        if(widthOfText <= screenWidth)
        {
            do{
                float currentTextSize = ticker.getTextSize();
                ticker.setTextSize(currentTextSize+resizeIncrement);
                widthOfText = paint.measureText(textToMeasure);
            }while (widthOfText <= screenWidth);
        }
        if(widthOfText >screenWidth)
        {
            do{
                float currentTextSize = ticker.getTextSize();
                ticker.setTextSize(currentTextSize-resizeIncrement);
                widthOfText = paint.measureText(textToMeasure);
            }while (widthOfText >screenWidth);
        }
    }

    static class MainActivityGestureListener extends GestureDetector.SimpleOnGestureListener {
        MainActivity activity;

        public MainActivityGestureListener(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {

            if (Objects.requireNonNull(this.activity.getSupportActionBar()).isShowing()) {
                Objects.requireNonNull(this.activity.getSupportActionBar()).hide();
            } else {
                Objects.requireNonNull(this.activity.getSupportActionBar()).show();
            }
            return super.onDoubleTap(e);
        }
    }
}


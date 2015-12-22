/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.sunshine.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn't shown. On
 * devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient mode.
 */
public class MyWatchFace extends CanvasWatchFaceService {
    /**
     * Update rate in milliseconds for interactive mode. We update once a second to advance the
     * second hand.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<MyWatchFace.Engine> mWeakReference;

        public EngineHandler(MyWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            MyWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine implements
            DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        final Handler mUpdateTimeHandler = new EngineHandler(this);
        boolean mRegisteredTimeZoneReceiver = false;
        Paint mBackgroundPaint;
        Paint mHandPaint;
        boolean mAmbient;
        Time mTime;
        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();
            }
        };
        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;



        Bitmap bmp;
        String high;
        String low;

        int hour;
        int min;
        int dayOfMonth;
        int year;
        String d;
        String m;

        boolean changedOnce;


        private GoogleApiClient mGoogleApiClient;

        @Override
        public void onDataChanged(DataEventBuffer dataEvents) {
            Log.d("ONDATACHANGED", "ONDATACHANGED");
            for (DataEvent dataEvent: dataEvents) {
                if (dataEvent.getType()==DataEvent.TYPE_CHANGED) {
                    DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                    String path = dataEvent.getDataItem().getUri().getPath();
                    if (path.equals("/forecast-today")) {
                        low = dataMap.getString("low");
                        high = dataMap.getString("high");
                        int num = dataMap.getInt("num");
                        byte[] byteArray = dataMap.getByteArray("im");
                        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        Calendar c = Calendar.getInstance();
                        hour = c.get(Calendar.HOUR_OF_DAY);
                        min = c.get(Calendar.MINUTE);
                        int day = c.get(Calendar.DAY_OF_WEEK);
                        int month = c.get(Calendar.MONTH);
                        dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                        year = c.get(Calendar.YEAR);
                        d = getDayOfWeek(day);
                        m = getMonthOfYear(month);
                        Log.d("DEBUG", "successfully sent. low = "+low);
                        Log.d("DEBUG", "successfully sent. high = "+high);
                        Log.d("DEBUG", "successfully sent. num++ = "+Integer.valueOf(num));
                        invalidate();
                        changedOnce=true;
                    }
                }
            }
        }

        private String getMonthOfYear(int i) {
            String ret = "";
            switch (i) {
                case 0: ret = "Jan";
                    break;
                case 1: ret = "Feb";
                    break;
                case 2: ret = "Mar";
                    break;
                case 3: ret = "Apr";
                    break;
                case 4: ret = "May";
                    break;
                case 5: ret = "Jun";
                    break;
                case 6: ret = "Jul";
                    break;
                case 7: ret = "Aug";
                    break;
                case 8: ret = "Sep";
                    break;
                case 9: ret = "Oct";
                    break;
                case 10: ret = "Nov";
                    break;
                case 11: ret = "Dec";
                    break;
                default:
                    ret = "Err";
                    break;
            }
            return ret;
        }

        private String getDayOfWeek(int i) {
            String ret = "";
            switch (i) {
                case 2: ret = "Mon";
                    break;
                case 3: ret = "Tue";
                    break;
                case 4: ret = "Wed";
                    break;
                case 5: ret = "Thu";
                    break;
                case 6: ret = "Fri";
                    break;
                case 7: ret = "Sat";
                    break;
                case 1: ret = "Sun";
                    break;
                default:
                    ret = "Err";
                    break;
            }
            return ret;
        }

        @Override
        public void onConnected(Bundle bundle) {
            Log.d("CONNECTED", "CONNECTED");
            Wearable.DataApi.addListener(mGoogleApiClient, this);
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }









        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());



//            Resources resources = MyWatchFace.this.getResources();
//
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(Color.BLUE);
//
            mHandPaint = new Paint();
            mHandPaint.setColor(Color.WHITE);
            mHandPaint.setStrokeWidth(3);
            mHandPaint.setAntiAlias(true);
            mHandPaint.setStrokeCap(Paint.Cap.ROUND);
//
//            mTime = new Time();

            mGoogleApiClient = new GoogleApiClient.Builder(MyWatchFace.this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();

        }

        @Override
        public void onDestroy() {
//            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
            super.onDestroy();
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
//            // Find the center. Ignore the window insets so that, on round watches with a
//            // "chin", the watch face is centered on the entire screen, not just the usable
//            // portion.
            float centerX = bounds.width() / 2f;
            float centerY = bounds.height() / 2f;

            if (changedOnce==true) {
                canvas.drawText(hour + ":" + min, centerX - 50, centerY - 20, mHandPaint);
                canvas.drawText(d + ", " + m + " " + dayOfMonth + " " + year, centerX - 50, centerY - 5, mHandPaint);
                canvas.drawBitmap(bmp, centerX - 50, centerY + 10, null);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }
    }
}

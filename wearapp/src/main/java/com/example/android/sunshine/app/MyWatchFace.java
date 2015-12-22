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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
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

/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn't shown. On
 * devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient mode.
 */
public class MyWatchFace extends CanvasWatchFaceService {

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<MyWatchFace.Engine> mWeakReference;

        public EngineHandler(MyWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine implements
            DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        final Handler mUpdateTimeHandler = new EngineHandler(this);
        boolean mRegisteredTimeZoneReceiver = false;

        Paint mBackgroundPaint, mPaintBig, mPaintSmall;

        Bitmap bmp;
        String high, low;

        int hour, min, dayOfMonth, year;
        String d, m;

        boolean dataChangedOnce;

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
                        dataChangedOnce =true;
                    }
                }
            }
        }

        private String getMonthOfYear(int i) {
            String month = "";
            switch (i) {
                case 0: month = "Jan";
                    break;
                case 1: month = "Feb";
                    break;
                case 2: month = "Mar";
                    break;
                case 3: month = "Apr";
                    break;
                case 4: month = "May";
                    break;
                case 5: month = "Jun";
                    break;
                case 6: month = "Jul";
                    break;
                case 7: month = "Aug";
                    break;
                case 8: month = "Sep";
                    break;
                case 9: month = "Oct";
                    break;
                case 10: month = "Nov";
                    break;
                case 11: month = "Dec";
                    break;
                default:
                    month = "Err";
                    break;
            }
            return month;
        }

        private String getDayOfWeek(int i) {
            String day = "";
            switch (i) {
                case 2: day = "Mon";
                    break;
                case 3: day = "Tue";
                    break;
                case 4: day = "Wed";
                    break;
                case 5: day = "Thu";
                    break;
                case 6: day = "Fri";
                    break;
                case 7: day = "Sat";
                    break;
                case 1: day = "Sun";
                    break;
                default:
                    day = "Err";
                    break;
            }
            return day;
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

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(Color.BLUE);

            mPaintBig = new Paint();
            mPaintBig.setColor(Color.WHITE);
            mPaintBig.setStrokeWidth(3);
            mPaintBig.setTextSize(28);
            mPaintBig.setAntiAlias(true);
            mPaintBig.setStrokeCap(Paint.Cap.ROUND);

            mPaintSmall = new Paint();
            mPaintSmall.setColor(Color.WHITE);
            mPaintSmall.setTextSize(18);
            mPaintSmall.setAntiAlias(true);
            mPaintSmall.setStrokeCap(Paint.Cap.ROUND);

            mGoogleApiClient = new GoogleApiClient.Builder(MyWatchFace.this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }

        @Override
        public void onDestroy() {
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
            float centerX = bounds.width() / 2f;
            float centerY = bounds.height() / 2f;

            if (dataChangedOnce==true) {
                canvas.drawText(hour + ":" + min, centerX - 50, centerY - 30, mPaintBig);
                canvas.drawText(d + ", " + m + " " + dayOfMonth + " " + year, centerX - 55, centerY - 10, mPaintSmall);
                canvas.drawBitmap(bmp, null, new RectF(centerX-120, centerY-50, centerX-60, centerY+10), null);
                canvas.drawText(high + "   " + low, centerX - 50, centerY + 20, mPaintBig);
            }
        }
    }
}
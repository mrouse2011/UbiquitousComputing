package com.example.android.sunshine.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.wearable.view.CircledImageView;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.util.Calendar;

public class MainActivity extends Activity implements
        DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d("ONDATACHANGED", "ONDATACHANGED");
        for (DataEvent dataEvent: dataEvents) {
            if (dataEvent.getType()==DataEvent.TYPE_CHANGED) {
                DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                String path = dataEvent.getDataItem().getUri().getPath();
                if (path.equals("/forecast-today")) {
                    String low = dataMap.getString("low");
                    String high = dataMap.getString("high");
                    int num = dataMap.getInt("num");
                    byte[] byteArray = dataMap.getByteArray("im");
                    Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    CircledImageView image = (CircledImageView)findViewById(R.id.imageView1);
                    TextView textView1 = (TextView)findViewById(R.id.textView);
                    TextView textView2 = (TextView)findViewById(R.id.textView2);
                    TextView textView3 = (TextView)findViewById(R.id.textView3);
                    TextView textView4 = (TextView)findViewById(R.id.textView4);
                    textView1.setText(high);
                    textView2.setText(low);
                    Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int min = c.get(Calendar.MINUTE);
                    int day = c.get(Calendar.DAY_OF_WEEK);
                    int month = c.get(Calendar.MONTH);
                    int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                    int year = c.get(Calendar.YEAR);
                    textView3.setText(String.valueOf(hour+":"+min));
                    String d = getDayOfWeek(day);
                    String m = getMonthOfYear(month);
                    textView4.setText(String.valueOf(d+", "+m+" "+dayOfMonth+" "+year));
                    image.setImageDrawable(new BitmapDrawable(getResources(), bmp));
                    Log.d("DEBUG", "successfully sent. low = "+low);
                    Log.d("DEBUG", "successfully sent. high = "+high);
                    Log.d("DEBUG", "successfully sent. num++ = "+Integer.valueOf(num));
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
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
}

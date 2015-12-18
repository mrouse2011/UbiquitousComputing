package com.example.android.sunshine.app;

import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class MyService extends WearableListenerService {

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d("ONDATACHANGED","ONDATACHANGED");
        for (DataEvent dataEvent: dataEvents) {
            if (dataEvent.getType()==DataEvent.TYPE_CHANGED) {
                DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
                String path = dataEvent.getDataItem().getUri().getPath();
                if (path.equals("/step-counter")) {
                    int steps = dataMap.getInt("step-count");
                    Log.d("DEBUG", "successfully sent. steps = "+String.valueOf(steps));
                }
            }
        }
    }
}

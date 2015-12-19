package com.example.android.sunshine.app;

import com.google.android.gms.wearable.WearableListenerService;

public class MyService extends WearableListenerService {

//    @Override
//    public void onDataChanged(DataEventBuffer dataEvents) {
//        Log.d("ONDATACHANGED","ONDATACHANGED");
//        for (DataEvent dataEvent: dataEvents) {
//            if (dataEvent.getType()==DataEvent.TYPE_CHANGED) {
//                DataMap dataMap = DataMapItem.fromDataItem(dataEvent.getDataItem()).getDataMap();
//                String path = dataEvent.getDataItem().getUri().getPath();
//                if (path.equals("/forecast-today")) {
//                    String low = dataMap.getString("low");
//                    String high = dataMap.getString("high");
//                    int num = dataMap.getInt("num");
//                    byte[] byteArray = dataMap.getByteArray("im");
//                    Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//                    ImageView image = (ImageView)findViewById(R.id.imageView1);
//                    image.setImageBitmap(bmp);
//                    Log.d("DEBUG", "successfully sent. low = "+low);
//                    Log.d("DEBUG", "successfully sent. high = "+high);
//                    Log.d("DEBUG", "successfully sent. num = "+Integer.valueOf(num));
//                }
//            }
//        }
//    }
}
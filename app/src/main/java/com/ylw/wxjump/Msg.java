package com.ylw.wxjump;

import android.util.Log;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by ylw on 2018/1/2.
 */

class Msg {
    private static final String TAG = "Msg";
    private int[] his = new int[4];


    void send(final int time, int x) {
//        boolean allSame = true;
//        for (int i = his.length - 1; i > 0; i--) {
//            if (his[i] != x) {
//                allSame = false;
//            }
//            his[i] = his[i - 1];
//        }
//        his[0] = x;
//        if (!allSame) {
//            return;
//        }
//        Arrays.fill(his, -1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlName = GBData.ipaddr + "/" + time;
                    URL realUrl = new URL(urlName);
                    URLConnection conn = realUrl.openConnection();
                    conn.connect();
                    InputStream inputStream = conn.getInputStream();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i(TAG, GBData.ipaddr + "/" + time);
            }
        }).start();
    }
}

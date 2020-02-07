package com.example.wuhanfighting;

import android.os.Message;
import android.util.Log;

import java.net.URL;
import java.util.logging.Handler;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpRequest {
    private static  String responseData;

    //发送网络请求访问接口
    static void sendRequestWithOkHttp(){

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //访问疫情数据接口
                    URL url = new URL("https://www.tianqiapi.com/api?version=epidemic&appid=23035354&appsecret=8YvlPNrz");
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    Message msg = new Message();
                    msg.what = 3;
                    MainActivity.mHandler.sendMessage(msg);
                }
            }
        });
        t.start();
    }

    public static String getResponseData() {
        return responseData;
    }
}

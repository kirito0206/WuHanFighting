package com.example.wuhanfighting;

import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpRequest {
    private static  String responseData;

    //发送网络请求访问接口
    static String sendRequestWithOkHttp(){

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
                    //将返回的数据分析
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        while(t.isAlive()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return responseData;
    }

    public static String getResponseData() {
        return responseData;
    }
}

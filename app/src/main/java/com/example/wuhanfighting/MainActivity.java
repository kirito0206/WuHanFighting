package com.example.wuhanfighting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Province> provincesList = new ArrayList<>();
    private ProvinceAdapter adapter;
    private Handler mHandler;
    private String head;
    private TextView headText;
    private TextView diagnosed;
    private TextView suspect;
    private TextView death;
    private TextView cured;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化各控件
        init();
        //设置recycle
        initRecycle();
        //处理handle机制
        initHandle();
        //访问接口获取数据
        String response = OkHttpRequest.sendRequestWithOkHttp();
        //解析获取的json数据
        pareJSONData(response);
    }

    private void init(){
        headText = (TextView)findViewById(R.id.head_view);
        diagnosed = (TextView) findViewById(R.id.diagnosed);
        suspect = (TextView) findViewById(R.id.suspect);
        death = (TextView)findViewById(R.id.death);
        cured = (TextView)findViewById(R.id.cured);
    }
    private void initHandle(){
        //新建Handler对象
        mHandler = new Handler(){
            //handleMessage为处理消息的方法
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1) {
                    headText.setText(head);
                    diagnosed.setText("\n已确诊总人数\n"+provincesList.get(0).getTotalNumber()+"\n");
                    suspect.setText("\n疑似总人数\n"+provincesList.get(0).getSuspectedNumber()+"\n");
                    death.setText("\n死亡总人数\n"+provincesList.get(0).getDeathNumber()+"\n");
                    cured.setText("\n治愈总人数\n"+provincesList.get(0).getCureNumber()+"\n");
                    provincesList.remove(0);

                    adapter.notifyDataSetChanged();
                }
            }
        };
    }
    private void initRecycle() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ProvinceAdapter(provincesList);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    private void pareJSONData(final String JSONData) {
        try {
            JSONObject rootObject = new JSONObject(JSONData);
            JSONObject secondObject = rootObject.getJSONObject("data");
            String time = secondObject.getString("date");
            String totalDiagnosed = secondObject.getString("diagnosed");
            String totalSuspect = secondObject.getString("suspect");
            String totalCured = secondObject.getString("cured");
            String totalDeath = secondObject.getString("death");
            provincesList.add(new Province("中国",totalDiagnosed,totalSuspect,totalCured,totalDeath));
            head = " 截至："+time+"\n 中国各地区疫情如下";
            //处理具体省份信息
            JSONArray jsonArray = secondObject.getJSONArray("list");
            provincesList.add(new Province("地区","确诊病例","疑似病例","治愈病例","死亡病例"));
            for (int i = 0; i < jsonArray.length(); i++) {
                String data = jsonArray.optString(i);
                Log.d("123",data);
                //处理获得的字符串
                String arr[] = data.split(" ");
                String eare = "";
                String suspected = "-";
                String total = "0";
                String cure = "0";
                String death = "0";
                for(int j = 0;j < arr.length;j++)
                {
                    if(j == 0)
                    eare = arr[j];
                    else if(arr[j].equals("确诊"))
                    {
                        total = arr[j+1];
                        j++;
                    }
                    else if(arr[j].equals("例，疑似"))
                    {
                        suspected = arr[j+1];
                        j++;
                    }
                    else if (arr[j].equals("例，治愈"))
                    {
                        cure = arr[j+1];
                        j++;
                    }
                    else if(arr[j].equals("例，死亡"))
                    {
                        death = arr[j+1];
                        j++;
                    }
                }
                provincesList.add(new Province(eare,total,suspected,cure,death));
            }

            Message mes = new Message();
            mes.what = 1;
            mHandler.sendMessage(mes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
